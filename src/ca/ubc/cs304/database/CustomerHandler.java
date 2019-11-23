package ca.ubc.cs304.database;

/* this class handles all customer operations */

import ca.ubc.cs304.model.*;

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

    public CustomerModel getCustomer(String dlicense) {
        CustomerModel customer = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM customer WHERE customer.DLICENSE = ?");
            ps.setString(1, dlicense);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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
            rollbackConnection();
        }
        return result;
    }

    public List<LocationCityModel> getLocations() {
        List<LocationCityModel> result = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT location, city FROM VEHICLE");
            while (rs.next()) {
                result.add(new LocationCityModel(rs.getString("location"), rs.getString("city")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    private PreparedStatement makeQueryAvailableVehicles(String vtname, String location, String city, String fromDate,
                                                         String fromTime, String toDate, String toTime, boolean isCount) {

        PreparedStatement ps = null;
        try {

            boolean isLocationEmpty = location == null || location.equals("");
            boolean isVtnameEmpty = vtname == null || vtname.equals("");
            boolean hasTimeInterval = fromDate != null && !fromDate.equals("");
            String locationQuery = isLocationEmpty ? "" : "vehicle.LOCATION = ? AND vehicle.CITY = ? AND ";
            String vtnameQuery = isVtnameEmpty ? "" : "vehicle.vtname = ? AND ";
            String selectStmt = isCount ? "COUNT(DISTINCT vehicle.vlicense) AS count " : "DISTINCT make, model, year, color, vtname, location, city ";
            String timeIntervalQuery = hasTimeInterval ?
                    "AND (? < rental.RENTAL_FROMDATETIME OR ? > rental.RENTAL_TODATETIME)))" : "))";
            ps = connection.prepareStatement(
                    "SELECT " + selectStmt +
                            " FROM vehicle, rental " +
                            "WHERE " +
                            vtnameQuery +
                            locationQuery +
                            "((vehicle.VLICENSE NOT IN (SELECT rental.VLICENSE FROM rental) " +
                            "AND vehicle.STATUS = 'available') " +
                            "OR (rental.VLICENSE = vehicle.VLICENSE " +
                            timeIntervalQuery + " ORDER BY vehicle.MAKE"
            );
            int numParameters = 0;
            if (!isLocationEmpty) numParameters+=2;
            if (!isVtnameEmpty) numParameters++;
            if (hasTimeInterval) numParameters += 2;

            boolean vtnameSet = false;
            boolean locationSet = false;
            boolean citySet = false;
            boolean toTimestampSet = false;
            boolean fromTimestampSet = false;
            for (int i = 1; i <= numParameters; i++) {
                if (!isVtnameEmpty && !vtnameSet) {
                    ps.setString(i, vtname);
                    vtnameSet = true;
                    continue;
                }
                if (!isLocationEmpty && !locationSet) {
                    ps.setString(i, location);
                    locationSet = true;
                    continue;
                }
                if (!isLocationEmpty && !citySet) {
                    ps.setString(i, city);
                    citySet = true;
                    continue;
                }
                if (hasTimeInterval && !toTimestampSet) {
                    String inputToDateTime = toDate + " " + toTime + ":00.00"; //date in YYYY-MM-DD and time in HH:mm
                    Timestamp toTimestamp = Timestamp.valueOf(inputToDateTime);
                    ps.setTimestamp(i, toTimestamp);
                    toTimestampSet = true;
                    continue;
                }
                if (hasTimeInterval && !fromTimestampSet) {
                    String inputFromDateTime = fromDate + " " + fromTime + ":00.00";
                    Timestamp fromTimestamp = Timestamp.valueOf(inputFromDateTime);
                    ps.setTimestamp(i, fromTimestamp);
                    fromTimestampSet = true;
                }

            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return ps;
    }

    //format: YYYY-MM-DD and HH:mm
    public int countAvailableVehicles(String vtname, String location, String city, String fromDate,
                                      String fromTime, String toDate, String toTime) {
        int count = 0;
        PreparedStatement ps = makeQueryAvailableVehicles(vtname, location, city, fromDate, fromTime, toDate, toTime, true);

        try {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                count += rs.getInt("count");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return count;
    }

    public List<VehicleDetailsModel> getAvailableVehicleDetails(String vtname, String location, String city, String fromDate,
                                                                String fromTime, String toDate, String toTime) {
        PreparedStatement ps = makeQueryAvailableVehicles(vtname, location, city, fromDate, fromTime, toDate, toTime, false);

        List<VehicleDetailsModel> vehicleDetailsList = new LinkedList<>();
        try {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                VehicleDetailsModel vehicleDetails = new VehicleDetailsModel(rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getString("color"),
                        rs.getString("vtname"),
                        rs.getString("location"),
                        rs.getString("city"));
                vehicleDetailsList.add(vehicleDetails);
            }
            rs.close();
            ps.close();
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
