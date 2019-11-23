package ca.ubc.cs304.ui;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.*;

import javax.xml.stream.Location;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomerTransactions {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";
    private static final int INVALID_INPUT = Integer.MIN_VALUE;
    private static final int EMPTY_INPUT = 0;

    private BufferedReader bufferedReader = null;
    private TerminalTransactionsDelegate delegate = null;

    public CustomerTransactions(BufferedReader bufferedReader, TerminalTransactionsDelegate delegate) {
        this.bufferedReader = bufferedReader;
        this.delegate = delegate;
    }

    public void showCustomerMenu() {
        int choice = Integer.MIN_VALUE;
        while (choice != 3) {
            System.out.println();
            System.out.println("1. Search Vehicles");
            System.out.println("2. Make a Reservation");
            System.out.println("3. Quit");
            System.out.print("Please choose one of the above 3 options: ");

            choice = readInteger(false);

            System.out.println(" ");

            if (choice != INVALID_INPUT) {
                switch (choice) {
                    case 1:
                        handleSearchOption();
                        break;
                    case 2:
                        handleReservationOption();
                        break;
                    case 3:
                        handleQuitOption();
                        break;
                    default:
                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                        break;
                }
            }
        }
    }

    public void handleSearchOption() {
        System.out.println("Select vehicle type (optional): ");
        List<VehicleTypeModel> vehicleTypeModels = delegate.getVehicleTypes();
        for (int i = 0; i < vehicleTypeModels.size(); i++) {
            System.out.println((i + 1) + ": " + vehicleTypeModels.get(i).getVtname());
        }
        int vehicleTypeChoice = INVALID_INPUT;
        while (vehicleTypeChoice < 1 || vehicleTypeChoice > vehicleTypeModels.size() + 1) {
            System.out.print("Select a vehicle type: ");
            vehicleTypeChoice = readInteger(true);
            if (vehicleTypeChoice==EMPTY_INPUT) break;
        }
        String vtname = vehicleTypeChoice==EMPTY_INPUT ? "": vehicleTypeModels.get(vehicleTypeChoice - 1).getVtname();

        System.out.println("Select location (optional): ");
        List<LocationCityModel> locations = delegate.getLocations();
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i+1) + ": " + locations.get(i).getLocation() + ", " + locations.get(i).getCity());
        }
        int locationChoice = INVALID_INPUT;
        while (locationChoice < 1 || locationChoice > locations.size() + 1) {
            System.out.print("Select a location: ");
            locationChoice = readInteger(true);
            if (locationChoice==EMPTY_INPUT) break;
        }
        String location = locationChoice==EMPTY_INPUT ? "" : locations.get(locationChoice - 1).getLocation();
        String city = locationChoice==EMPTY_INPUT ? "" : locations.get(locationChoice - 1).getCity();

        String ynTimeInterval = " ";
        while (!(ynTimeInterval.equalsIgnoreCase("y") || ynTimeInterval.equalsIgnoreCase("n"))){
            System.out.print("Would you like to enter a time interval? (Y/N): ");
            ynTimeInterval = readLine();
            System.out.println();
        }
        String fromDate = "";
        String fromTime = "";
        String toDate = "";
        String toTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (ynTimeInterval.equalsIgnoreCase("y")) {
            while (!isDateValid(fromDate)) {
                System.out.print("Enter start date (YYYY-MM-DD): ");
                fromDate = readLine();
                if (!isDateValid(fromDate)) {
                    System.out.println("Invalid date entered. Please try again!");
                }
                System.out.println();
            }

            while (!isTimeValid(fromTime)) {
                System.out.print("Enter start time (HH:mm e.g. 17:30): ");
                fromTime = readLine();
                if (!isTimeValid(fromTime)) {
                    System.out.println("Invalid time entered. Please try again!");
                }
                System.out.println();
            }

            boolean endDateAfterStart = false;
            while (!isDateValid(toDate) || !endDateAfterStart) {
                System.out.print("Enter end date (YYYY-MM-DD): ");
                toDate = readLine();
                if (!isDateValid(toDate)) {
                    System.out.println("Invalid date entered. Please try again!");
                    System.out.println();
                    continue;
                }
                try {
                    Date startDate = sdf.parse(fromDate);
                    Date endDate = sdf.parse(toDate);
                    endDateAfterStart = startDate.before(endDate);
                    if (!endDateAfterStart){
                        System.out.println("The end date must be after the start date!");
                    }
                    System.out.println();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            while (!isTimeValid(toTime)) {
                System.out.print("Enter end time (HH:mm e.g. 17:30): ");
                toTime = readLine();
                if (!isTimeValid(toTime)) {
                    System.out.println("Invalid time entered. Please try again!");
                }
                System.out.println();
            }
        }

        int numAvailableVehicles = delegate.countAvailableVehicles(vtname, location, city, fromDate, fromTime, toDate, toTime);

        System.out.println("Number of available vehicles: " + numAvailableVehicles);

        if (numAvailableVehicles == 0) {
            return;
        }

        String yesNo = " ";
        while (!(yesNo.equalsIgnoreCase("y") || yesNo.equalsIgnoreCase("n"))) {
            System.out.println("View details? (Y/N): ");
            yesNo = readLine();
            if (yesNo.equalsIgnoreCase("y")) {
                List<VehicleDetailsModel> vehicleDetails = delegate.getAvailableVehicleDetails(vtname, location, city, fromDate, fromTime, toDate, toTime);
                System.out.println();
                System.out.println("Details of available vehicles: ");
                System.out.printf("%-15s%-15s%-15s%-15s%-15s%-15s%-15s", "MAKE", "MODEL", "YEAR", "COLOR", "VEHICLE TYPE", "LOCATION", "CITY");
                for (VehicleDetailsModel v : vehicleDetails) {
                    System.out.println();
                    System.out.printf("%-15s", v.getMake());
                    System.out.printf("%-15s", v.getModel());
                    System.out.printf("%-15s", v.getYear());
                    System.out.printf("%-15s", v.getColor());
                    System.out.printf("%-15s", v.getVtname());
                    System.out.printf("%-15s", v.getLocation());
                    System.out.printf("%-15s", v.getCity());
                }
                System.out.println();
                break;
            }
        }
    }

    public void handleReservationOption() {
        String newCustomer = " ";
        while (!(newCustomer.equalsIgnoreCase("y") || newCustomer.equals("n"))) {
            System.out.println("Are you a new customer? (Y/N): ");
            newCustomer = readLine();
        }

        CustomerModel currentCustomer = null;

        String dlicenseForReservation = null;
        if (newCustomer.equalsIgnoreCase("y")) {
            System.out.print("Enter your name: ");
            String name = readLine();
            System.out.println();
            System.out.print("Enter your cellphone number: ");
            int cellphone = readInteger(false);
            System.out.println();
            System.out.print("Enter your address: ");
            String address = readLine();
            System.out.println();
            System.out.print("Enter your driver's license number: ");
            String dlicense = readLine();
            dlicenseForReservation = dlicense;
            System.out.println();
            CustomerModel newCustomerModel = new CustomerModel(cellphone, name, address, dlicense);
            delegate.insertCustomer(newCustomerModel);
            currentCustomer = newCustomerModel;
        }

        while (dlicenseForReservation == null) {
            System.out.print("Enter your driver's license number: ");
            String dlicense = readLine();
            currentCustomer = delegate.getCustomer(dlicense);
            if (currentCustomer != null) {
                dlicenseForReservation = dlicense;
                System.out.println("Welcome, " + currentCustomer.getName());
                break;
            } else {
                System.out.println("No such driver license found. Please try again!");
            }
        }

        System.out.println("Select vehicle type: ");
        List<VehicleTypeModel> vehicleTypeModels = delegate.getVehicleTypes();
        for (int i = 0; i < vehicleTypeModels.size(); i++) {
            System.out.println((i + 1) + ": " + vehicleTypeModels.get(i).getVtname());
        }
        int vehicleTypeChoice = INVALID_INPUT;
        while (vehicleTypeChoice < 1 || vehicleTypeChoice > vehicleTypeModels.size() + 1) {
            System.out.print("Select a vehicle type: ");
            vehicleTypeChoice = readInteger(false);
        }
        String vtname = vehicleTypeModels.get(vehicleTypeChoice - 1).getVtname();


        String fromDate = "";
        while(!isDateValid(fromDate)) {
            System.out.print("Enter start date (YYYY-MM-DD): ");
            fromDate = readLine();
            if (!isDateValid(fromDate)){
                System.out.println("Invalid date entered. Please try again!");
            }
            System.out.println();
        }

        String fromTime = "";
        while (!isTimeValid(fromTime)) {
            System.out.print("Enter start time (HH:mm e.g. 17:30): ");
            fromTime = readLine();
            if (!isTimeValid(fromTime)){
                System.out.println("Invalid time entered. Please try again!");
            }
            System.out.println();
        }

        String toDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean endDateAfterStart = false;
        while (!isDateValid(toDate) || !endDateAfterStart) {
            System.out.print("Enter end date (YYYY-MM-DD): ");
            toDate = readLine();
            if (!isDateValid(toDate)) {
                System.out.println("Invalid date entered. Please try again!");
                System.out.println();
                continue;
            }
            try {
                Date startDate = sdf.parse(fromDate);
                Date endDate = sdf.parse(toDate);
                endDateAfterStart = startDate.before(endDate);
                if (!endDateAfterStart){
                    System.out.println("The end date must be after the start date!");
                }
                System.out.println();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String toTime = "";
        while(!isTimeValid(toTime)) {
            System.out.print("Enter end time (HH:mm e.g. 17:30): ");
            toTime = readLine();
            if (!isTimeValid(toTime)){
                System.out.println("Invalid time entered. Please try again!");
            }
            System.out.println();
        }

        int availVehicles = delegate.countAvailableVehicles(vtname, "", "", fromDate, fromTime, toDate, toTime);
        if (availVehicles == 0) {
            System.out.println("There are no available vehicles for your requested criteria. ");
        } else {

            ReservationModel reservation = new ReservationModel(vtname,
                    currentCustomer,
                    Timestamp.valueOf(fromDate + " " + fromTime + ":00.00"),
                    Timestamp.valueOf(toDate + " " + toTime + ":00.00"));

            delegate.insertReservation(reservation);

            System.out.println("Reservation Complete!");
            System.out.println("Reservation Details: ");
            System.out.printf("%-20s%-20s%-20s%-20s%-20s", "CONFIRMATION NO.", "VEHICLE TYPE", "DRIVER'S LICENSE", "FROM DATE/TIME", "TO DATE/TIME");
            System.out.println();
            System.out.printf("%-20s%-20s%-20s%-20s%-20s",
                    reservation.getconfNo(),
                    reservation.getVtname(),
                    reservation.getDlicense(),
                    reservation.getFromDateTime().toString().substring(0, reservation.getFromDateTime().toString().length()-5),
                    reservation.getToDateTime().toString().substring(0, reservation.getToDateTime().toString().length()-5));
        }
    }

    private void handleQuitOption() {
        System.out.println("Good Bye!");

        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println("IOException!");
            }
        }

        delegate.terminalTransactionsFinished();
    }

    private int readInteger(boolean allowEmpty) {
        String line = null;
        int input = INVALID_INPUT;
        try {
            line = bufferedReader.readLine();
            input = Integer.parseInt(line);
        } catch (IOException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        } catch (NumberFormatException e) {
            if (allowEmpty && line.length() == 0) {
                input = EMPTY_INPUT;
            } else {
                System.out.println(WARNING_TAG + " Your input was not an integer");
            }
        }
        return input;
    }

    private String readLine() {
        String result = null;
        try {
            result = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    private boolean isDateValid(String dateString){
        return dateString.matches("\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])");
    }

    private boolean isTimeValid(String timeString){
        return timeString.matches("(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]");
    }


}
