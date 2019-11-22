package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.ClerkHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.CreditCard;
import ca.ubc.cs304.model.ReservationModel;
import ca.ubc.cs304.model.VehicleModel;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.TerminalTransactions;

/**
 * This is the main controller class that will orchestrate everything.
 */
public class Clerk implements LoginWindowDelegate, TerminalTransactionsDelegate {
	private ClerkHandler dbHandler = null;
	private LoginWindow loginWindow = null;

	public Clerk() {
		dbHandler = new ClerkHandler();
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
	 * TermainalTransactionsDelegate Implementation
	 * 
	 * Make a rental with the given info
	 */
    public void makeRental(String vlicense, String dlicense, int odometer, CreditCard card, String resNo, String fromDate, String fromTime, String toDate, String toTime) {
    	if (resNo != null) {
			dbHandler.rentVehicle(resNo, vlicense, dlicense, odometer, card);
		} else {
    		dbHandler.rentVehicleNoReserve(fromDate, fromTime, toDate, toTime, vlicense, dlicense, odometer, card);
		}
    }

    /**
	 * TermainalTransactionsDelegate Implementation
	 * 
	 * Return a vehicle
	 */ 
    public void returnVehicle(String rid, String retDate, String retTime, int retOdometer, boolean fullTank) {
    	dbHandler.returnVehicle(rid, retDate, retTime, retOdometer, fullTank);
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
    
	/**
	 * Main method called at launch time
	 */
	public static void main(String args[]) {
		Clerk bank = new Clerk();
		bank.start();
	}
}
