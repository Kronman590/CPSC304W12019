package ca.ubc.cs304.database;

/* this class handles all customer operations */

import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.ReservationModel;
import ca.ubc.cs304.model.VehicleDetailsModel;
import ca.ubc.cs304.model.VehicleTypeModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomerHandler {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private Connection connection;

    public CustomerHandler(Connection connection) {
        this.connection = connection;
    }

    public void insertCustomer(CustomerModel model) {
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
            rollbackConnection();
        }
    }

    public void insertReservation(ReservationModel model) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO reservation VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, model.getconfNo());
            ps.setString(2, model.getVtname());
            ps.setString(3, model.getDlicense());
            ps.setString(4, model.getFromDate());
            ps.setString(5, model.getFromTime());
            ps.setString(6, model.getToDate());
            ps.setString(7, model.getToTime());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public List<VehicleTypeModel> getVehicleTypes() {
        List<VehicleTypeModel> result = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM VEHICLETYPE");

            while (rs.next()) {
                VehicleTypeModel vtm = new VehicleTypeModel(rs.getString("vtname"),
                        rs.getString("features"),
                        rs.getDouble("hrate"),
                        rs.getDouble("drate"),
                        rs.getDouble("wrate"),
                        rs.getDouble("hirate"),
                        rs.getDouble("dirate"),
                        rs.getDouble("wirate"),
                        rs.getDouble("krate"));
                result.add(vtm);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getLocations() {
        List<String> result = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT location FROM VEHICLE");
            while(rs.next()){
                result.add(rs.getString("location"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //format: MM/DD/YYYY and HH:mm
    public int countAvailableVehicles(String vtname, String location, String fromDate,
                                       String fromTime, String toDate, String toTime) {
        int count = 0;
        try {

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
            while (rs1.next()) {
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
            while (rs2.next()) {
                count += rs2.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public void getAvailableVehicleDetails(String vtname, String location, String fromDate,
                                           String fromTime, String toDate, String toTime) {
        try {
            List<VehicleDetailsModel> vehicleDetailsList = new LinkedList<>();
            PreparedStatement ps1 = connection.prepareStatement(
                    "SELECT vehicle.MAKE, vehicle.model, vehicle.year, vehicle.COLOR, vehicle.VTNAME " +
                            "FROM vehicle, rental " +
                            "WHERE vehicle.VLICENSE NOT IN (SELECT rental.VLICENSE FROM rental) " +
                            "AND vehicle.vtname = ? " +
                            "AND vehicle.LOCATION = ?" +
                            "AND vehicle.STATUS = 'available'"
            );
            ps1.setString(1, vtname);
            ps1.setString(2, location);

            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                VehicleDetailsModel vehicleDetails = new VehicleDetailsModel(rs1.getString("make"),
                        rs1.getString("model"),
                        rs1.getInt("year"),
                        rs1.getString("color"),
                        rs1.getString("vtname"));
                vehicleDetailsList.add(vehicleDetails);
            }

            PreparedStatement ps2 = connection.prepareStatement(
                    "SELECT vehicle.MAKE, vehicle.model, vehicle.year, vehicle.COLOR, vehicle.VTNAME " +
                            "FROM vehicle, rental " +
                            "WHERE rental.VLICENSE = vehicle.VLICENSE " +
                            "AND vehicle.VTNAME = ? " +
                            "AND vehicle.LOCATION = ?" +
                            "AND (TO_DATE(?, 'MM/DD/YYYY HH24:MI') < TO_DATE(CONCAT(CONCAT(rental.RENTAL_FROMDATE, ' '), rental.RENTAL_FROMTIME), 'MM/DD/YYYY HH24:MI') " +
                            "OR TO_DATE(?, 'MM/DD/YYYY HH24:MI') > TO_DATE(CONCAT(CONCAT(rental.RENTAL_TODATE, ' '), rental.RENTAL_TOTIME), 'MM/DD/YYYY HH24:MI'))");
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                VehicleDetailsModel vehicleDetails = new VehicleDetailsModel(rs1.getString("make"),
                        rs1.getString("model"),
                        rs1.getInt("year"),
                        rs1.getString("color"),
                        rs1.getString("vtname"));
                vehicleDetailsList.add(vehicleDetails);
            }

            System.out.println("Details of available vehicles: ");
            for (VehicleDetailsModel v : vehicleDetailsList) {
                System.out.println(v.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rollbackConnection() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }
}
