package ca.ubc.cs304.ui;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;

import java.io.BufferedReader;
import java.io.IOException;

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




}
