package ca.ubc.cs304.database;

/* this class handles all customer operations */

import ca.ubc.cs304.model.CustomerModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerOperations {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";

    public CustomerOperations() {
    }

    public void insertCustomer(Connection connection, CustomerModel model) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO customer VALUES (?,?,?,?)");
            ps.setInt(1, model.getCellphone());
            ps.setString(2, model.getName());
            ps.setString(3, model.getAddress());
            ps.setString(4, model.getDlicense());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection(connection);
        }
    }

    //format: MM/DD/YYYY and HH:mm
    public void countAvailableVehicles(Connection connection, String vtname, String location, String fromDate,
                                        String fromTime, String toDate, String toTime) {
        try {
            int count = 0;
            //First count vehicles that have no rentals
            PreparedStatement ps1 = connection.prepareStatement(
                    "SELECT COUNT(*) " +
                            "FROM vehicle " +
                            "WHERE vehicle.VLICENSE NOT IN (SELECT rental.VLICENSE FROM rental) " +
                            "AND vehicle.vtname = ? " +
                            "AND vehicle.LOCATION = ?" +
                            "AND vehicle.STATUS = 'available'"
            );
            ps1.setString(1, vtname);
            ps1.setString(2, location);

            ResultSet rs1 = ps1.executeQuery();
            while(rs1.next()){
                count += rs1.getInt(1);
            }

            //Then count vehicles that have rentals, but are available in the requested period
            PreparedStatement ps2 = connection.prepareStatement(
                    "SELECT COUNT(*) " +
                            "FROM vehicle, rental " +
                            "WHERE rental.VLICENSE = vehicle.VLICENSE " +
                            "AND vehicle.VTNAME = ? " +
                            "AND vehicle.LOCATION = ?" +
                            "AND (TO_DATE(?, 'MM/DD/YYYY HH24:MI') < TO_DATE(CONCAT(CONCAT(rental.RENTAL_FROMDATE, ' '), rental.RENTAL_FROMTIME), 'MM/DD/YYYY HH24:MI') " +
                            "OR TO_DATE(?, 'MM/DD/YYYY HH24:MI') > TO_DATE(CONCAT(CONCAT(rental.RENTAL_TODATE, ' '), rental.RENTAL_TOTIME), 'MM/DD/YYYY HH24:MI'))");
            ps2.setString(1, vtname);
            ps2.setString(2, location);
            String inputFromDateTime = fromDate + " " + fromTime;
            ps2.setString(3, inputFromDateTime);
            String inputToDateTime = toDate + " " + toTime;
            ps2.setString(4, inputToDateTime);

            ResultSet rs2 = ps2.executeQuery();
            while(rs2.next()){
                count += rs2.getInt(1);
            }

            System.out.println("Number of available vehicles: " + count);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rollbackConnection(Connection connection) {
        try  {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }
}
