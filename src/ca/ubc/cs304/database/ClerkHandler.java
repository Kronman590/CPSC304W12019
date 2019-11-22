package ca.ubc.cs304.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.ReservationModel;
import ca.ubc.cs304.model.VehicleModel;
import ca.ubc.cs304.model.CreditCard;

import static java.lang.Math.floor;

/**
 * This class handles all database related transactions
 * ssh -l jw97 -L localhost:1522:dbhost.students.cs.ubc.ca:1522 remote.students.cs.ubc.ca
 */
public class ClerkHandler {
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	
	private Connection connection = null;

	private int rentalID = 0;
	
	public ClerkHandler(Connection connection) {
//		try {
//			// Load the Oracle JDBC driver
//			// Note that the path could change for new drivers
//			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//		}
		this.connection = connection;
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
			Statement stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery("SELECT (rental_fromDate,rental_fromTime,rental_toDate,rental_toTime) FROM reservation WHERE reservation.res_confNo = " + confNo);
			String fromDate = res.getString("rental_fromDate");
			String fromTime = res.getString("rental_fromTime");
			String toDate = res.getString("rental_toDate");
			String toTime = res.getString("rental_toTime");
			PreparedStatement ps = connection.prepareStatement("INSERT INTO rental VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			String rid = String.valueOf(rentalID);
			while (rid.length() < 6) {
				rid = "0" + rid;
			}
			ps.setString(1, rid);
			rentalID++;
			ps.setString(2, vlicense);
			ps.setString(3, dlicense);
			ps.setString(4, fromDate);
			ps.setString(5, fromTime);
			ps.setString(6, toDate);
			ps.setString(7, toTime);
			ps.setInt(8, odometer);
			ps.setString(9, card.getCardName());
			ps.setString(10, card.getCardNo());
			ps.setString(11, card.getExpDate());
			ps.setString(12, confNo);
			ps.executeUpdate();
			PreparedStatement ps2 = connection.prepareStatement("UPDATE vehicle SET status = ? WHERE vlicense = ?");
			ps2.setString(1, "rented");
			ps2.setString(2, vlicense);
			int rowCount = ps2.executeUpdate();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Vehicle " + vlicense + " does not exist!");
			}
			System.out.println(" RECEIPT FOR RENTAL: \n Your Rental ID: " + rentalID + "\n Vehicle License Plate: "
					+ vlicense + "\n From " + fromDate + ", " + fromTime + " to " + toDate + ", " + toTime + "\n Paid for by: " + card.getCardName());
			connection.commit();
			ps.close();
			ps2.close();
			res.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	public void rentVehicleNoReserve(String fromDate, String fromTime, String toDate, String toTime, String vlicense, String dlicense, int odometer, CreditCard card) { // If reservation is *not* provided
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO rental VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			String rid = String.valueOf(rentalID);
			while (rid.length() < 6) {
				rid = "0" + rid;
			}
			ps.setString(1, rid);
			rentalID++;
			ps.setString(2, vlicense);
			ps.setString(3, dlicense);
			ps.setString(4, fromDate);
			ps.setString(5, fromTime);
			ps.setString(6, toDate);
			ps.setString(7, toTime);
			ps.setInt(8, odometer);
			ps.setString(9, card.getCardName());
			ps.setString(10, card.getCardNo());
			ps.setString(11, card.getExpDate());
			ps.setNull(5, java.sql.Types.CHAR);
			ps.executeUpdate();
			PreparedStatement ps2 = connection.prepareStatement("UPDATE vehicle SET status = ? WHERE vlicense = ?");
			ps2.setString(1, "rented");
			ps2.setString(2, vlicense);
			int rowCount = ps2.executeUpdate();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Vehicle " + vlicense + " does not exist!");
			}
			System.out.println(" RECEIPT FOR RENTAL: \n Your Rental ID: " + rentalID + "\n Vehicle License Plate: "
					+ vlicense + "\n From " + fromDate + ", " + fromTime + " to " + toDate + ", " + toTime
					+ "\n Paid for by: " + card.getCardName());
			connection.commit();
			ps.close();
			ps2.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	public void returnVehicle(String rid, String retDate, String retTime, int retOdometer, boolean fullTank) {
		try {
			Statement stmt = connection.createStatement();
			ResultSet rntl = stmt.executeQuery("SELECT * FROM rental WHERE rental.rental_rid = " + rid);
			String strtDate = rntl.getString("rental_fromDate");
			String strtTime = rntl.getString("rental_fromTime");
			int odometer = rntl.getInt("rental_odometer");
			ResultSet vech = stmt.executeQuery("SELECT vtname FROM vehicle WHERE vehicle.vlicense = " + rntl.getString("vlicense"));
			ResultSet vtype = stmt.executeQuery("SELECT * FROM vehicleType WHERE vehicleType.vtname = " + vech.getString("vtname"));
			Date dateBegin = null;
			Date dateEnd = null;
			Date timeBegin = null;
			Date timeEnd = null;
			try {
				dateBegin = new SimpleDateFormat("MM/DD/YYYY").parse(strtDate);
				dateEnd = new SimpleDateFormat("MM/DD/YYYY").parse(retDate);
				timeBegin = new SimpleDateFormat("HH:MM").parse(strtTime);
				timeEnd = new SimpleDateFormat("HH:MM").parse(retTime);
			} catch (Exception dateErr) {
				System.out.println("Invalid Date input.");
				rollbackConnection();
			}
			double weeks = floor(TimeUnit.DAYS.convert(dateEnd.getTime() - dateBegin.getTime(), TimeUnit.MILLISECONDS) / 7.0);
			double days = TimeUnit.DAYS.convert(dateEnd.getTime() - dateBegin.getTime(), TimeUnit.MILLISECONDS) % 7.0;
			double hours = TimeUnit.HOURS.convert(dateEnd.getTime() - dateBegin.getTime(), TimeUnit.MILLISECONDS) - (weeks*7+days)*24;
			double totalCost = weeks*(vtype.getDouble("wrate")+vtype.getDouble("wirate")) +
					days*(vtype.getDouble("drate")+vtype.getDouble("dirate")) + hours*(vtype.getDouble("hrate")+vtype.getDouble("hirate"));
			PreparedStatement ps = connection.prepareStatement("INSERT INTO return VALUES (?,?,?,?,?,?)");
			ps.setString(1, rid);
			ps.setString(2, retDate);
			ps.setString(3, retTime);
			ps.setInt(4, retOdometer);
			if (fullTank) {
				ps.setString(5, "1");
			} else {
				ps.setString(5, "0");
			}
			ps.setDouble(6, totalCost);
			ps.executeUpdate();
			System.out.println(" Cost Breakdown: \n Weeks Rented: " + weeks + "\t - \t Weekly Rate: "
					+ vtype.getDouble("wrate") + "\n Addl. Days Rented: " + days + "\t - \t Daily Rate: " + vtype.getDouble("drate")
					+ "\n Addl. Hours Rented: " + hours + "\t - \t Hourly Rate: " + vtype.getDouble("hrate") + " Insurance Cost: \n Weeks Rented: " + weeks + "\t - \t Weekly Rate: "
					+ vtype.getDouble("wirate") + "\n Addl. Days Rented: " + days + "\t - \t Daily Rate: " + vtype.getDouble("dirate")
					+ "\n Addl. Hours Rented: " + hours + "\t - \t Hourly Rate: " + vtype.getDouble("hirate")
					+ "\n Total Costs: " + totalCost);
			connection.commit();
			ps.close();
			vtype.close();
			vech.close();
			rntl.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

//	public boolean login(String username, String password) {
//		try {
//			if (connection != null) {
//				connection.close();
//			}
//
//			connection = DriverManager.getConnection(ORACLE_URL, username, password);
//			connection.setAutoCommit(false);
//
//			System.out.println("\nConnected to Oracle!");
//			return true;
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//			return false;
//		}
//	}

	private void rollbackConnection() {
		try  {
			connection.rollback();	
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
}
