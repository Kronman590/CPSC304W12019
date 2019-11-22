package ca.ubc.cs304.ui;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
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

    public void showCustomerMenu(){
        int choice = Integer.MIN_VALUE;
        while (choice != 3){
            System.out.println();
            System.out.println("1. Search Vehicles");
            System.out.println("2. Make a Reservation");
            System.out.println("3. Quit");
            System.out.print("Please choose one of the above 3 options: ");

            choice = readInteger(false);

            System.out.println(" ");
        }
    }

    public void handleSearchOption(){
        System.out.println("Select vehicle type: ");
        List<VehicleTypeModel> vehicleTypeModels = delegate.getVehicleTypes();
        for (int i = 0; i < vehicleTypeModels.size(); i++){
            System.out.println((i+1) + ": " + vehicleTypeModels.get(i).getVtname());
        }
        int vehicleTypeChoice = INVALID_INPUT;
        while (vehicleTypeChoice < 1 || vehicleTypeChoice > vehicleTypeModels.size() + 1){
            System.out.print("Select a vehicle type: ");
            vehicleTypeChoice = readInteger(false);
        }
        String vtname = vehicleTypeModels.get(vehicleTypeChoice-1).getVtname();

        System.out.println("Select location: ");
        List<String> locations = delegate.getLocations();
        for (int i = 0; i < locations.size(); i++){
            System.out.println((i+1) + ": " + locations.get(i));
        }
        int locationChoice = INVALID_INPUT;
        while (locationChoice < 1 || locationChoice > locations.size()+1){
            System.out.print("Select a location: ");
            locationChoice = readInteger(false);
        }
        String location = locations.get(locationChoice-1);

        System.out.print("Enter start date (MM/DD/YYYY): ");
        String fromDate = readLine();
        System.out.println();
        System.out.print("Enter start time (HH:mm e.g. 17:30): ");
        String fromTime = readLine();
        System.out.println();

        System.out.print("Enter end date (MM/DD/YYYY): ");
        String toDate = readLine();
        System.out.println();
        System.out.print("Enter end time (HH:mm e.g. 17:30): ");
        String toTime = readLine();
        System.out.println();

        int numAvailableVehicles = delegate.countAvailableVehicles(vtname, location, fromDate, fromTime, toDate, toTime);

        System.out.println("Number of available vehicles: " + numAvailableVehicles);
    }

    public void handleReservationOption(){

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