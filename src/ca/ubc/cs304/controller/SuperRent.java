package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.*;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.ReportGenerator;
import ca.ubc.cs304.ui.TerminalTransactions;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

/**
 * This is the main controller class that will orchestrate everything.
 * ssh -l jw97 -L localhost:1522:dbhost.students.cs.ubc.ca:1522 remote.students.cs.ubc.ca
 */
public class SuperRent implements LoginWindowDelegate, TerminalTransactionsDelegate {
	private DatabaseConnectionHandler dbHandler = null;
	private LoginWindow loginWindow = null;

	public SuperRent() {
		dbHandler = new DatabaseConnectionHandler();
	}

	/**
	 * Main method called at launch time
	 */
	public static void main(String args[]) {
		SuperRent bank = new SuperRent();
		bank.start();
	}


	private void start() {
		loginWindow = new LoginWindow();
		loginWindow.showFrame(this);
	}
	
	/**
	 * LoginWindowDelegate Implementation
	 * 
     * connects to Oracle database with supplied username and password
     */ 
	public void login(String username, String password) {
		boolean didConnect = dbHandler.login(username, password);

		if (didConnect) {
			// Once connected, remove login window and start text transaction flow
			loginWindow.dispose();

			TerminalTransactions transaction = new TerminalTransactions();
			transaction.showMainMenu(this);
		} else {
			loginWindow.handleLoginFailed();

			if (loginWindow.hasReachedMaxLoginAttempts()) {
				loginWindow.dispose();
				System.out.println("You have exceeded your number of allowed attempts");
				System.exit(-1);
			}
		}
	}

    /**
	 * TerminalTransactionsDelegate Implementation
	 * 
     * The TerminalTransaction instance tells us that it is done with what it's 
     * doing so we are cleaning up the connection since it's no longer needed.
     */ 
    public void terminalTransactionsFinished() {
    	dbHandler.close();
    	dbHandler = null;
    	
    	System.exit(0);
    }

    public List<VehicleTypeModel> getVehicleTypes(){
    	return dbHandler.getCustomerHandler().getVehicleTypes();
	}

	public List<LocationCityModel> getLocations(){
    	return dbHandler.getCustomerHandler().getLocations();
	}

	public int countAvailableVehicles(String vtname, String location, String city, String fromDate,
									  String fromTime, String toDate, String toTime){
    	return dbHandler.getCustomerHandler().countAvailableVehicles(vtname, location, city, fromDate,
				fromTime, toDate, toTime);
	}

	public List<VehicleDetailsModel> getAvailableVehicleDetails(String vtname, String location, String city, String fromDate,
                                                                String fromTime, String toDate, String toTime) {
        return dbHandler.getCustomerHandler().getAvailableVehicleDetails(vtname, location, city, fromDate, fromTime, toDate, toTime);
    }

    public void insertCustomer(CustomerModel customerModel){
    	dbHandler.getCustomerHandler().insertCustomer(customerModel);
	}

	public CustomerModel getCustomer(String dlicense){
    	return dbHandler.getCustomerHandler().getCustomer(dlicense);
	}

	public void insertReservation(ReservationModel model){
    	dbHandler.getCustomerHandler().insertReservation(model);
	}

    @Override
    public void dailyRental() {
        dbHandler.getClerkHandler().dailyRental();
    }

    @Override
    public void dailyBranchRental(String location, String city) {
        dbHandler.getClerkHandler().dailyBranchRental(location,city);
    }

    @Override
    public void dailyReturn() {
        dbHandler.getClerkHandler().dailyReturn();
    }

    @Override
    public void dailyBranchReturn(String location, String city) {
        dbHandler.getClerkHandler().dailyBranchReturn(location, city);
    }

    /**
	 * TermainalTransactionsDelegate Implementation
	 *
	 * Make a rental with the given info
	 */
	public void makeRental(String vlicense, String dlicense, int odometer, CreditCard card, String resNo, Timestamp fromDateTime, Timestamp toDateTime) {
		if (resNo != null) {
			dbHandler.getClerkHandler().rentVehicle(resNo, vlicense, dlicense, odometer, card);
		} else {
			dbHandler.getClerkHandler().rentVehicleNoReserve(fromDateTime, toDateTime, vlicense, dlicense, odometer, card);
		}
	}

	/**
	 * TermainalTransactionsDelegate Implementation
	 *
	 * Return a vehicle
	 */
	public void returnVehicle(String rid, Timestamp retDateTime, int retOdometer, boolean fullTank) {
		dbHandler.getClerkHandler().returnVehicle(rid, retDateTime, retOdometer, fullTank);
	}
}
