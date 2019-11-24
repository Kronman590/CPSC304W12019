package ca.ubc.cs304.database;

import java.sql.*;
import ca.ubc.cs304.model.CreditCard;
import ca.ubc.cs304.ui.ReportGenerator;


/**
 * This class handles all database related transactions
 * ssh -l jw97 -L localhost:1522:dbhost.students.cs.ubc.ca:1522 remote.students.cs.ubc.ca
 */
public class ClerkHandler {
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	
	private Connection connection;
	private ReportGenerator re;

	private int rentalID = 1;
	
	public ClerkHandler(Connection connection) {
		this.connection = connection;
		re = new ReportGenerator();
	}
	
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public void rentVehicle(String confNo, String vlicense, String dlicense, int odometer, CreditCard card) { // If reservation is provided
		try {
			String vtnameSelected = "";
			Statement stmt = connection.createStatement();
			ResultSet checkVType = stmt.executeQuery("SELECT vtname FROM vehicle WHERE vehicle.vlicense = '" + vlicense + "'");
			while (checkVType.next()) {
				vtnameSelected = checkVType.getString("vtname");
			}
			ResultSet res = stmt.executeQuery("SELECT vtname,dlicense,rental_fromDateTime,rental_toDateTime FROM reservation WHERE reservation.res_confNo = '" + confNo + "'");
			while(res.next()) {
				String vtnameReserved = res.getString("vtname");
				if (!vtnameSelected.equals(vtnameReserved)) {
					System.out.println(WARNING_TAG + " Selected vehicle is not of the reserved type!");
					return;
				}
				if (!dlicense.equals(res.getString("dlicense"))) {
					System.out.println(WARNING_TAG + " Given driver's license does not match reservation license!");
					return;
				}
				Timestamp fromDateTime = res.getTimestamp("rental_fromDateTime");
				Timestamp toDateTime = res.getTimestamp("rental_toDateTime");
				PreparedStatement ps2 = connection.prepareStatement("UPDATE vehicle SET status = ? WHERE vlicense = ?");
				ps2.setString(1, "rented");
				ps2.setString(2, vlicense);
				int rowCount = ps2.executeUpdate();
				if (rowCount == 0) {
					System.out.println(WARNING_TAG + " Vehicle " + vlicense + " does not exist!");
					return;
				}
				PreparedStatement ps = connection.prepareStatement("INSERT INTO rental VALUES (?,?,?,?,?,?,?,?,?,?)");
				String rid = String.valueOf(rentalID);
				while (rid.length() < 6) {
					rid = "0" + rid;
				}
				ps.setString(1, rid);
				rentalID++;
				ps.setString(2, vlicense);
				ps.setString(3, dlicense);
				ps.setTimestamp(4, fromDateTime);
				ps.setTimestamp(5, toDateTime);
				ps.setInt(6, odometer);
				ps.setString(7, card.getCardName());
				ps.setString(8, card.getCardNo());
				ps.setString(9, card.getExpDate());
				ps.setString(10, confNo);
				ps.executeUpdate();
				System.out.println(" RECEIPT FOR RENTAL: \n Your Rental ID: " + rid + "\n Vehicle License Plate: " + vlicense
						+ "\n From " + fromDateTime + ", " + " to " + toDateTime + "\n Paid for by: " + card.getCardName());
				connection.commit();
				ps.close();
				ps2.close();
			}
			res.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	public void rentVehicleNoReserve(java.sql.Timestamp fromDateTime, java.sql.Timestamp toDateTime, String vlicense, String dlicense, int odometer, CreditCard card) { // If reservation is *not* provided
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO rental VALUES (?,?,?,?,?,?,?,?,?,?)");
			String rid = String.valueOf(rentalID);
			while (rid.length() < 6) {
				rid = "0" + rid;
			}
			ps.setString(1, rid);
			rentalID++;
			ps.setString(2, vlicense);
			ps.setString(3, dlicense);
			ps.setTimestamp(4, fromDateTime);
			ps.setTimestamp(5, toDateTime);
			ps.setInt(6, odometer);
			ps.setString(7, card.getCardName());
			ps.setString(8, card.getCardNo());
			ps.setString(9, card.getExpDate());
			ps.setNull(10, java.sql.Types.CHAR);
			ps.executeUpdate();
			PreparedStatement ps2 = connection.prepareStatement("UPDATE vehicle SET status = ? WHERE vlicense = ?");
			ps2.setString(1, "rented");
			ps2.setString(2, vlicense);
			int rowCount = ps2.executeUpdate();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Vehicle " + vlicense + " does not exist!");
			}
			System.out.println(" RECEIPT FOR RENTAL: \n Your Rental ID: " + rid + "\n Vehicle License Plate: " + vlicense
					+ "\n From " + fromDateTime + ", " + " to " + toDateTime + "\n Paid for by: " + card.getCardName());
			connection.commit();
			ps.close();
			ps2.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	public void returnVehicle(String rid, java.sql.Timestamp retDateTime, int retOdometer, boolean fullTank) {
		try {
			boolean returnComplete = false;
			double totalCost = 0.0;
			String vlicense = "";
			Statement stmt = connection.createStatement();
			ResultSet checkRepeatID = stmt.executeQuery("SELECT * FROM return WHERE return.rental_rid = '" + rid + "'");
			while (checkRepeatID.next()) {
				System.out.println("***Vehicle is already returned, please select a different vehicle.***");
				rollbackConnection();
				return;
			}
			checkRepeatID.close();
			ResultSet vtype = stmt.executeQuery("SELECT rental.vlicense,rental.rental_fromDateTime,hrate,drate,wrate,hirate,dirate,wirate FROM vehicle,vehicleType,rental " +
					"WHERE rental.rental_rid = '" + rid + "' AND vehicle.vlicense = rental.vlicense AND vehicle.vtname = vehicleType.vtname");
			while (vtype.next()) {
				Timestamp strtDateTime = vtype.getTimestamp("rental_fromDateTime");
				vlicense = vtype.getString("vlicense");
				//int odometer = vtype.getInt("rental_odometer");
				long end = retDateTime.getTime();
				long start = strtDateTime.getTime();
				int hours = (int)((end - start)/(1000*3600));
				int weeks = hours/(24*7);
				int days = (hours/24)%7;
				hours = hours%24;
				totalCost = weeks * (vtype.getDouble("wrate") + vtype.getDouble("wirate")) +
						days * (vtype.getDouble("drate") + vtype.getDouble("dirate")) + hours * (vtype.getDouble("hrate") + vtype.getDouble("hirate"));
				System.out.println(" Cost Breakdown: \n Weeks Rented:\t \t \t" + weeks + " x Weekly Rate:\t" + vtype.getDouble("wrate") + "\t = \t" + weeks*vtype.getDouble("wrate")
						+ "\n Addl. Days Rented:\t \t" + days + " x Daily Rate:\t \t" + vtype.getDouble("drate") + "\t = \t" + days*vtype.getDouble("drate")
						+ "\n Addl. Hours Rented:\t" + hours + " x Hourly Rate:\t" + vtype.getDouble("hrate") + "\t = \t" + hours*vtype.getDouble("hrate")
						+ "\n Insurance Cost: \n Weeks Rented: \t \t \t" + weeks + " x Weekly Rate:\t" + vtype.getDouble("wirate") + "\t = \t" + weeks*vtype.getDouble("wirate")
						+ "\n Addl. Days Rented:\t \t" + days + " x Daily Rate:\t \t" + vtype.getDouble("dirate") + "\t = \t" + days*vtype.getDouble("dirate")
						+ "\n Addl. Hours Rented:\t" + hours + " x  Hourly Rate:\t" + vtype.getDouble("hirate") + "\t = \t" + hours*vtype.getDouble("hirate")
						+ "\n Total Costs: " + totalCost);
				returnComplete = true;
			}
			vtype.close();
			stmt.close();
			if (!returnComplete) {
				System.out.println("***Return failed, input parameters invalid.***");
				return;
			}
			PreparedStatement ps = connection.prepareStatement("INSERT INTO return VALUES (?,?,?,?,?)");
			ps.setString(1, rid);
			ps.setTimestamp(2, retDateTime);
			ps.setInt(3, retOdometer);
			if (fullTank) {
				ps.setString(4, "1");
			} else {
				ps.setString(4, "0");
			}
			ps.setDouble(5, totalCost);
			ps.executeUpdate();
            PreparedStatement ps2 = connection.prepareStatement("UPDATE vehicle SET status = ? WHERE vlicense = ?");
            ps2.setString(1, "available");
            ps2.setString(2, vlicense);
            ps2.executeUpdate();
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	private void rollbackConnection() {
		try  {
			connection.rollback();	
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public void dailyRental() {
        re.dailyRental(connection);
    }

    public void dailyBranchRental(String location, String city){
        re.dailyBranchRental(connection,location,city);
    }

    public void dailyReturn() {
        re.dailyReturn(connection);
    }

    public void dailyBranchReturn(String location, String city) {
        re.dailyBranchReturn(connection,location,city);
    }
}
