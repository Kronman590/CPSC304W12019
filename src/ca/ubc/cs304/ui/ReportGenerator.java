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
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        datestring = localDate.format(formatter);
    }

    public void dailyRental(Connection connection) {
        try {
            String query = "SELECT vehicle.vlicense, make, model, year, color, odometer, status, vtname, location, city FROM vehicle,rental " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(rental.rental_fromDateTime) = to_date(?, 'YYYY-MM-DD') "+
                    "ORDER BY location, city, vtname";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, datestring);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No vehicles were rented today in the whole company.");
                return;
            }
            System.out.println("The given report contains information about all the vehicles rented out during the day." + "\n" +
                    "It provides 3 tables: a table of vehicle information, a table of total vehicles rented per category, and a table of total rentals per branch.");
            System.out.println();
            int rows = printResultSet(rs);
            System.out.println();
            rs.close();
            ps.close();


            String query2 = "SELECT vtname, COUNT(*) FROM vehicle,rental " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(rental.rental_fromDateTime) = to_date(?, 'YYYY-MM-DD') "+
                    "GROUP BY vehicle.vtname";
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ps2.setString(1, datestring);
            ResultSet rs2 = ps2.executeQuery();
            printCategories(rs2);
            rs2.close();
            ps2.close();

            String query3 = "SELECT location, city, COUNT(*) FROM vehicle,rental " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(rental.rental_fromDateTime) = to_date(?, 'YYYY-MM-DD') "+
                    "GROUP BY vehicle.location, vehicle.city";
            PreparedStatement ps3 = connection.prepareStatement(query3);
            ps3.setString(1, datestring);
            ResultSet rs3 = ps3.executeQuery();
            printBranchRentals(rs3);
            System.out.println();
            rs.close();
            ps3.close();


            printNumRentals(rows);
            System.out.print(" in the company.\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void dailyBranchRental(Connection connection, String location, String city) {
        try {

            String query = "SELECT vehicle.vlicense, make, model, year, color, odometer, status, vtname, location, city FROM vehicle,rental " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(rental.rental_fromDateTime) = to_date(?, 'YYYY-MM-DD') AND vehicle.location = ? AND vehicle.city = ? " +
                    "ORDER BY vehicle.vtname";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, datestring);
            ps.setString(2, location);
            ps.setString(3, city);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No vehicles were rented today in this branch.");
                return;
            }
            System.out.println("The given report contains information about all the vehicles rented out during the day." + "\n" +
                    "It provides 3 tables: a table of vehicle information, a table of total vehicles rented per category, and a table of total rentals per branch.");
            System.out.println();
            int rows = printResultSet(rs);
            System.out.println();
            rs.close();
            ps.close();

            String query2 = "SELECT vtname, COUNT(*) FROM vehicle, rental " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(rental.rental_fromDateTime) = to_date(?, 'YYYY-MM-DD') AND vehicle.location = ? AND vehicle.city = ? " +
                    "GROUP BY vehicle.vtname";
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ps2.setString(1, datestring);
            ps2.setString(2, location);
            ps2.setString(3, city);
            ResultSet rs2 = ps2.executeQuery();
            printCategories(rs2);
            System.out.println();
            rs2.close();
            ps2.close();

            String query3 = "SELECT location, city, COUNT(*) FROM vehicle,rental " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(rental.rental_fromDateTime) = to_date(?, 'YYYY-MM-DD') AND vehicle.location = ? AND vehicle.city = ? "+
                    "GROUP BY vehicle.location, vehicle.city";
            PreparedStatement ps3 = connection.prepareStatement(query3);
            ps3.setString(1, datestring);
            ps3.setString(2, location);
            ps3.setString(3, city);
            ResultSet rs3 = ps3.executeQuery();
            printBranchRentals(rs3);
            rs.close();
            ps3.close();

            System.out.println();
            printNumRentals(rows);

            System.out.print(" in the branch.\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dailyReturn(Connection connection) {
        try {

            String query = "SELECT vehicle.vlicense, make, model, year, color, return.return_odometer AS odometer, status, vtname, location, city FROM vehicle,rental,return " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(return.return_dateTime) = to_date(?, 'YYYY-MM-DD') AND return.rental_rid = rental.rental_rid "+
                    "ORDER BY location, city, vtname";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, datestring);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No vehicles were returned today in the whole company.");
                return;
            }
            System.out.println("The given report contains info about vehicles returned during the day.\nIt gives three tables: a table of vehicle info, " +
                    "a table of total returns and revenue for each vehicle category, and a table of total returns and revenue for each branch.");
            System.out.println();
            int rows = printResultSet(rs);
            System.out.println();
            rs.close();
            ps.close();


            String query2 = "SELECT vtname, COUNT(*), SUM(value) FROM vehicle,rental, return " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(return.return_dateTime) = to_date(?, 'YYYY-MM-DD') AND return.rental_rid = rental.rental_rid "+
                    "GROUP BY vehicle.vtname";
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ps2.setString(1, datestring);
            ResultSet rs2 = ps2.executeQuery();
            printCategoryReturn(rs2);
            System.out.println();
            rs2.close();
            ps2.close();

            String query3 = "SELECT location, city, COUNT(*), SUM(value) FROM vehicle, rental, return " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(return.return_dateTime) = to_date(?, 'YYYY-MM-DD') AND return.rental_rid = rental.rental_rid "+
                    "GROUP BY vehicle.location, vehicle.city";
            PreparedStatement ps3 = connection.prepareStatement(query3);
            ps3.setString(1, datestring);
            ResultSet rs3 = ps3.executeQuery();
            printBranchReturns(rs3);
            rs.close();
            ps3.close();
            System.out.println();
            printNumReturns(rows);
            System.out.print(" in the company.\n");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dailyBranchReturn(Connection connection, String location, String city) {
        try {

            String query = "SELECT vehicle.vlicense, make, model, year, color, return.return_odometer AS odometer, status, vtname, location, city FROM vehicle,rental,return " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(return.return_dateTime) = to_date(?, 'YYYY-MM-DD') AND return.rental_rid = rental.rental_rid AND vehicle.location = ? AND vehicle.city = ? "+
                    "ORDER BY location, city, vtname";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, datestring);
            ps.setString(2, location);
            ps.setString(3, city);
            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No vehicles were returned today in this branch.");
                return;
            }
            System.out.println("The given report contains info about vehicles returned during the day. \nIt gives three tables: a table of vehicle info, " +
                    "a table of total returns and revenue for each vehicle category, and a table of total returns and revenue for each branch.");

            System.out.println();
            int rows = printResultSet(rs);
            System.out.println();
            rs.close();
            ps.close();


            String query2 = "SELECT vtname, COUNT(*), SUM(value) FROM vehicle,rental, return " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(return.return_dateTime) = to_date(?, 'YYYY-MM-DD') AND return.rental_rid = rental.rental_rid AND return.rental_rid = rental.rental_rid " +
                    "AND vehicle.location = ? AND vehicle.city = ? "+
                    "GROUP BY vehicle.vtname";
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ps2.setString(1, datestring);
            ps2.setString(2, location);
            ps2.setString(3, city);
            ResultSet rs2 = ps2.executeQuery();
            printCategoryReturn(rs2);
            System.out.println();
            rs2.close();
            ps2.close();

            String query3 = "SELECT location, city, COUNT(*), SUM(value) FROM vehicle, rental, return " +
                    "WHERE vehicle.vlicense = rental.vlicense "+
                    "AND trunc(return.return_dateTime) = to_date(?, 'YYYY-MM-DD') AND return.rental_rid = rental.rental_rid AND return.rental_rid = rental.rental_rid " +
                    "AND vehicle.location = ? AND vehicle.city = ? "+
                    "GROUP BY vehicle.location, vehicle.city";
            PreparedStatement ps3 = connection.prepareStatement(query3);
            ps3.setString(1, datestring);
            ps3.setString(2, location);
            ps3.setString(3, city);
            ResultSet rs3 = ps3.executeQuery();
            printBranchReturns(rs3);
            rs.close();
            ps3.close();
            System.out.println();
            printNumReturns(rows);
            System.out.print(" in the company.\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printBranchReturns(ResultSet rs) throws SQLException{
        System.out.println(String.format("%-11s %-11s %-17s %-20s","Location", "City", "Total Returns", "Total Revenue"));
        System.out.println(String.format("%s", "---------------------------------------------------------------"));
        while (rs.next()) {
            String location = rs.getString(1);
            String city = rs.getString(2);
            int count = rs.getInt(3);
            double revenue = rs.getDouble(4);
            System.out.println(String.format("%-11s %-11s %-17d %-20f",location, city, count,revenue));
        }
    }

    private void printCategoryReturn(ResultSet rs) throws SQLException {
        System.out.println(String.format("%-11s %-17s %-20s",
                "Category","Total Returns","Total Revenue"));
        System.out.println(String.format("%s", "--------------------------------------------------------"));
        String category;
        int returns;
        double revenue;
        while (rs.next()) {
            category = rs.getString(1);
            returns = rs.getInt(2);
            revenue = rs.getDouble(3);
            System.out.println(String.format("%-11s %-17d %-20f",
                    category, returns, revenue));
        }
    }

    private void printNumReturns(int rows) {
        String grammar;
        String returns="returns";
        if (rows == 1) {
            grammar = "is ";
            returns = "return";
        } else {
            grammar = "are ";
        }
        System.out.print("There "+grammar+rows +" new "+returns);
    }

    private void printNumRentals(int rows) {
        String grammar;
        String rental="rentals";
        if (rows == 1) {
            grammar = "is ";
            rental = "rental";
        } else {
            grammar = "are ";
        }
        System.out.print("There "+grammar+rows +" new "+rental);
    }

    private void printBranchRentals(ResultSet rs) throws SQLException {
        System.out.println(String.format("%-11s %-11s %-11s","Location", "City", "Total Rentals"));
        System.out.println(String.format("%s", "------------------------------------------"));
        while (rs.next()) {
            String location = rs.getString(1);
            String city = rs.getString(2);
            int count = rs.getInt(3);
            System.out.println(String.format("%-11s %-11s %-11d",location, city, count));
        }
    }
    private void printCategories(ResultSet rs2) throws SQLException {
        System.out.println(String.format("%-11s %-11s",
                "Category","Total Rentals"));
        System.out.println(String.format("%s", "------------------------------"));
        int i =0;
        while (rs2.next()) {
            String category = rs2.getString(1);
            int count = rs2.getInt(2);
            System.out.println(String.format("%-11s %-11d",
                    category, count));
        }
        System.out.print("\n");
    }

    private int printResultSet(ResultSet rs) throws SQLException {
        System.out.println(String.format("%-11s %-11s %-11s %-11s %-11s %-11s %-11s %-11s %-11s %-11s",
                "License","Make","Model", "Year", "Color", "Odometer", "Status", "Vtname",
                "Location", "City"));
        System.out.println(String.format("%s", "-----------------------------------------------------------------------------------------------------------------------"));
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
            rows++;
        }
        return rows;
    }

    private void columnPrinter(String vlicense, String make,String model, int year, String color,
                          int odometer, String status, String vtname, String location, String city) {
        System.out.println(String.format("%-11s %-11s %-11s %-11d %-11s %-11d %-11s %-11s %-11s %-11s",
                vlicense, make, model, year, color, odometer, status, vtname, location, city));
    }
}
