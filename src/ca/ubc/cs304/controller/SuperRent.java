package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.VehicleTypeModel;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.TerminalTransactions;

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

	public List<String> getLocations(){
    	return dbHandler.getCustomerHandler().getLocations();
	}

	public int countAvailableVehicles(String vtname, String location, String fromDate,
									  String fromTime, String toDate, String toTime){
    	return dbHandler.getCustomerHandler().countAvailableVehicles(vtname, location, fromDate,
				fromTime, toDate, toTime);
	}
}
