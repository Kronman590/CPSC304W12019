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
            ps.setLong(1, model.getCellphone());
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

    public CustomerModel getCustomer(String dlicense){
        CustomerModel customer = null;
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM customer WHERE customer.DLICENSE = ?");
            ps.setString(1, dlicense);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                customer = new CustomerModel(rs.getLong("cellphone"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("dlicense"));
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
        return customer;
    }

    public void insertReservation(ReservationModel model) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO reservation VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, model.getconfNo());
            ps.setString(2, model.getVtname());
            ps.setString(3, model.getDlicense());
            ps.setTimestamp(4, model.getFromDateTime());
            ps.setTimestamp(5, model.getToDateTime());


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
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();        }
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
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    //format: YYYY-MM-DD and HH:mm
    public int countAvailableVehicles(String vtname, String location, String fromDate,
                                       String fromTime, String toDate, String toTime, boolean excludeLocation) {
        int count = 0;
        try {

            String locationQuery = excludeLocation ? "" : "AND vehicle.LOCATION = ? ";
            //First count vehicles that have no rentals
            PreparedStatement ps1 = connection.prepareStatement(
                    "SELECT COUNT(*) " +
                            "FROM vehicle " +
                            "WHERE vehicle.VLICENSE NOT IN (SELECT rental.VLICENSE FROM rental) " +
                            "AND vehicle.vtname = ? " +
                            locationQuery +
                            "AND vehicle.STATUS = 'available'"
            );
            ps1.setString(1, vtname);
            if (!excludeLocation) ps1.setString(2, location);

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
                            locationQuery +
                            "AND (? < rental.RENTAL_FROMDATETIME " +
                            "OR ? > rental.RENTAL_TODATETIME) ");
            ps2.setString(1, vtname);
            if (!excludeLocation) ps2.setString(2, location);
            String inputToDateTime = toDate + " " + toTime + ":00.00"; //date in YYYY-MM-DD and time in HH:mm
            Timestamp toTimestamp = Timestamp.valueOf(inputToDateTime);
            ps2.setTimestamp(excludeLocation ? 2 : 3, toTimestamp);
            String inputFromDateTime = fromDate + " " + fromTime + ":00.00";
            Timestamp fromTimestamp = Timestamp.valueOf(inputFromDateTime);
            ps2.setTimestamp(excludeLocation ? 3 : 4, fromTimestamp);


            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                count += rs2.getInt(1);
            }

            rs1.close();
            rs2.close();
            ps1.close();
            ps2.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return count;
    }

    public List<VehicleDetailsModel> getAvailableVehicleDetails(String vtname, String location, String fromDate,
                                           String fromTime, String toDate, String toTime) {
        List<VehicleDetailsModel> vehicleDetailsList = new LinkedList<>();
        try {
            PreparedStatement ps1 = connection.prepareStatement(
                    "SELECT * " +
                            "FROM vehicle " +
                            "WHERE vehicle.VLICENSE NOT IN (SELECT rental.VLICENSE FROM rental) " +
                            "AND vehicle.vtname = ? " +
                            "AND vehicle.LOCATION = ? " +
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
                            "AND vehicle.LOCATION = ? " +
                            "AND (? < rental.RENTAL_FROMDATETIME " +
                            "OR ? > rental.RENTAL_TODATETIME)");
            ps2.setString(1, vtname);
            ps2.setString(2, location);
            String inputToDateTime = toDate + " " + toTime + ":00.00"; //date in YYYY-MM-DD and time in HH:mm
            Timestamp toTimestamp = Timestamp.valueOf(inputToDateTime);
            ps2.setTimestamp(3, toTimestamp);
            String inputFromDateTime = fromDate + " " + fromTime + ":00.00";
            Timestamp fromTimestamp = Timestamp.valueOf(inputFromDateTime);
            ps2.setTimestamp(4, fromTimestamp);

            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                VehicleDetailsModel vehicleDetails = new VehicleDetailsModel(rs2.getString("make"),
                        rs2.getString("model"),
                        rs2.getInt("year"),
                        rs2.getString("color"),
                        rs2.getString("vtname"));
                vehicleDetailsList.add(vehicleDetails);
            }

            rs1.close();
            rs2.close();
            ps1.close();
            ps2.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return vehicleDetailsList;
    }

    private void rollbackConnection() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }
}
