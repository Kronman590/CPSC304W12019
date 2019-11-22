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
    private String datestring;

    public ReportGenerator() {
        Date date = new Date();
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        datestring = localDate.format(formatter);
    }

    public void dailyRental(Connection connection) {
        try {

            String query = "SELECT * FROM vehicle " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND rental_fromDate = ?"+
                    "ORDER BY location, city, vehicle.vtname";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, datestring);
            ResultSet rs = ps.executeQuery();
            int rows = printResultSet(rs);
            rs.close();
            ps.close();

            String query2 = "SELECT vtname, COUNT(*) FROM vehicle " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND rental_fromDate = ?"+
                    "GROUP BY vehicle.vtname";
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ps2.setString(1, datestring);
            ResultSet rs2 = ps2.executeQuery();
            printCategories(rs2);
            rs2.close();
            ps2.close();

            String query3 = "SELECT location, city, COUNT(*) FROM vehicle " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND rental_fromDate = ?"+
                    "GROUP BY vehicle.location, vehicle.city";
            PreparedStatement ps3 = connection.prepareStatement(query3);
            ps.setString(1, datestring);
            ResultSet rs3 = ps3.executeQuery();
            printBranchRentals(rs3);
            rs.close();
            ps3.close();

            System.out.println("There are "+rows +" new rentals.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printBranchRentals(ResultSet rs) throws SQLException {
        System.out.println(String.format("%-11s %-11s %-11s","Location", "City", "Rentals"));
        while (rs.next()) {
            String location = rs.getString(1);
            String city = rs.getString(2);
            int count = rs.getInt(3);
            System.out.println(String.format("%-11s %-11s %-11d",location, city, count));
        }
    }
    private void printCategories(ResultSet rs2) throws SQLException {
        int i =0;
        while (rs2.next()) {
            String category = rs2.getString(1);
            int count = rs2.getInt(2);
            if (i ==0) {
                System.out.print(count + " " + category + " rentals");
                i++;
            } else {
                System.out.print(" , " +count + " " + category + " rentals");
            }
        }
    }

    private int printResultSet(ResultSet rs) throws SQLException {
        System.out.println(String.format("%-11s %-11s %-11s %-11s %-11s %-11s %-11s %-11s %-11s %-11s",
                "vLicense","make","model", "year", "color", "odometer", "status", "vtname",
                "location", "city"));
        String vlicense, make, model, color, status, vtname, location, city;
        int odometer, year;
        int rows = 0;
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
            if (rs.last()) {
                rows = rs.getRow();
            }
        }
        return rows;
    }

    private void columnPrinter(String vlicense, String make,String model, int year, String color,
                          int odometer, String status, String vtname, String location, String city) {
        System.out.println(String.format("%-11s %-11s %-11s %-11d %-11s %-11d%-11s %-11s %-11s %-11s",
                vlicense, make, model, year, color, odometer, status, vtname, location, city));
    }
}
