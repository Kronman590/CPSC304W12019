package ca.ubc.cs304.ui;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.VehicleDetailsModel;
import ca.ubc.cs304.model.VehicleTypeModel;

import java.io.BufferedReader;
import java.io.IOException;
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

        System.out.println("Select location: ");
        List<String> locations = delegate.getLocations();
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ": " + locations.get(i));
        }
        int locationChoice = INVALID_INPUT;
        while (locationChoice < 1 || locationChoice > locations.size() + 1) {
            System.out.print("Select a location: ");
            locationChoice = readInteger(false);
        }
        String location = locations.get(locationChoice - 1);

        System.out.print("Enter start date (YYYY-MM-DD): ");
        String fromDate = readLine();
        System.out.println();
        System.out.print("Enter start time (HH:mm e.g. 17:30): ");
        String fromTime = readLine();
        System.out.println();

        System.out.print("Enter end date (YYYY-MM-DD): ");
        String toDate = readLine();
        System.out.println();
        System.out.print("Enter end time (HH:mm e.g. 17:30): ");
        String toTime = readLine();
        System.out.println();

        int numAvailableVehicles = delegate.countAvailableVehicles(vtname, location, fromDate, fromTime, toDate, toTime);

        System.out.println("Number of available vehicles: " + numAvailableVehicles);

        String yesNo = " ";
        while (!(yesNo.equalsIgnoreCase("y") || yesNo.equalsIgnoreCase("n"))) {
            System.out.println("View details? (Y/N): ");
            yesNo = readLine();
            if (yesNo.equalsIgnoreCase("y")) {
                List<VehicleDetailsModel> vehicleDetails = delegate.getAvailableVehicleDetails(vtname, location, fromDate, fromTime, toDate, toTime);
                System.out.println("Details of available vehicles: ");
                System.out.printf("%-15s%-15s%-15s%-15s%-15s", "MAKE", "MODEL", "YEAR", "COLOR", "VEHICLE TYPE");
                for (VehicleDetailsModel v : vehicleDetails) {
                    System.out.println();
                    System.out.printf("%-15s", v.getMake());
                    System.out.printf("%-15s", v.getModel());
                    System.out.printf("%-15s", v.getYear());
                    System.out.printf("%-15s", v.getColor());
                    System.out.printf("%-15s", v.getVtname());
                }
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


}
