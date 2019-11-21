package ca.ubc.cs304.ui;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;

import javax.xml.transform.Result;
import java.io.BufferedReader;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class ReportGenerator {
    private BufferedReader bufferedReader = null;
    private TerminalTransactionsDelegate delegate = null;

    public void dailyRental(Connection connection) {
        try {
            Date date = new Date();
            LocalDateTime localDate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String datestring = localDate.format(formatter);

            String query = "SELECT * FROM vehicle " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND rental_fromDate = ?"+
                    "ORDER BY location, city, vehicle.vtname";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, datestring);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printResultSet(ResultSet rs) throws SQLException {
        System.out.println(String.format("%-11s %-11s %-11s %-11d %-11s %-11d%-11s %-11s %-11s %-11s",
                "vLicense","make","model", "year", "color", "odometer", "status", "vtname",
                "location", "city"));
        String vlicense, make, model, color, status, vtname, location, city;
        int odometer, year;
        while (rs.next()) {
            vlicense = rs.getString("vlicense");
            make = rs.getString("make");
            model = rs.getString("model");
            year = rs.getInt("year");
            color = rs.getString("color");
            odometer = rs.getInt("odometer");
            status = rs.getString("status");
            vtname = rs.getString("vtname");
            location = rs.getString("location");
            city = rs.getString("city");
            columnPrinter(vlicense, make, model, year, color, odometer, status,
                    vtname, location, city);
        }
    }

    private void columnPrinter(String vlicense, String make,String model, int year, String color,
                          int odometer, String status, String vtname, String location, String city) {
        System.out.println(String.format("%-11s %-11s %-11s %-11d %-11s %-11d%-11s %-11s %-11s %-11s",
                vlicense, make, model, year, color, odometer, status, vtname, location, city));
    }
}
