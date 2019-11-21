package ca.ubc.cs304.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.ReservationModel;
import ca.ubc.cs304.model.VehicleModel;
import ca.ubc.cs304.model.CreditCard;

/**
 * This class handles all database related transactions
 */
public class ClerkHandler {
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	
	private Connection connection = null;

	private int rentalID = 0;
	
	public ClerkHandler() {
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
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

	public void rentVehicle(ReservationModel reservation, VehicleModel vehicle, String dlicense, int odometer, CreditCard card) { // If reservation is provided
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO rental VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, String.valueOf(rentalID));
			rentalID++;
			ps.setString(2, vehicle.getVlicense());
			ps.setString(3, dlicense);
			ps.setString(4, reservation.getFromDate());
			ps.setString(5, reservation.getFromTime());
			ps.setString(6, reservation.getToDate());
			ps.setString(7, reservation.getToTime());
			ps.setInt(8, odometer);
			ps.setString(9, card.getCardName());
			ps.setString(10, card.getCardNo());
			ps.setString(11, card.getExpDate());
			ps.setString(12, reservation.getconfNo());
			ps.executeUpdate();
			PreparedStatement ps2 = connection.prepareStatement("UPDATE vehicle SET status = ? WHERE vlicense = ?");
			ps2.setString(1, "rented");
			ps2.setString(2, vehicle.getVlicense());
			int rowCount = ps2.executeUpdate();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Vehicle " + vehicle.getVlicense() + " does not exist!");
			}
			connection.commit();
			ps.close();
			ps2.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	public void rentVehicleNoReserve(String fromDate, String fromTime, String toDate, String toTime, VehicleModel vehicle, String dlicense, int odometer, CreditCard card) { // If reservation is *not* provided
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO rental VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, String.valueOf(rentalID));
			rentalID++;
			ps.setString(2, vehicle.getVlicense());
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
			ps2.setString(2, vehicle.getVlicense());
			int rowCount = ps2.executeUpdate();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Vehicle " + vehicle.getVlicense() + " does not exist!");
			}
			connection.commit();
			ps.close();
			ps2.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}
	
			connection = DriverManager.getConnection(ORACLE_URL, username, password);
			connection.setAutoCommit(false);
	
			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
	}

	private void rollbackConnection() {
		try  {
			connection.rollback();	
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
}
