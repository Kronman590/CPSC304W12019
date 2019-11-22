package ca.ubc.cs304.delegates;
import ca.ubc.cs304.model.CreditCard;

import ca.ubc.cs304.model.VehicleTypeModel;

import java.util.List;

/**
 * This interface uses the delegation design pattern where instead of having
 * the TerminalTransactions class try to do everything, it will only
 * focus on handling the UI. The actual logic/operation will be delegated to the 
 * controller class (in this case Bank).
 * 
 * TerminalTransactions calls the methods that we have listed below but 
 * Bank is the actual class that will implement the methods.
 */
public interface TerminalTransactionsDelegate {

	void makeRental(String vlicense, String dlicense, int odometer, CreditCard card, String resID, String fromDate, String fromTime, String toDate, String toTime);
	void returnVehicle(String rid, String retDate, String retTime, int retOdometer, boolean fullTank);

	void terminalTransactionsFinished();

	List<VehicleTypeModel> getVehicleTypes();

	List<String> getLocations();

	int countAvailableVehicles(String vtname, String location, String fromDate,
									  String fromTime, String toDate, String toTime);
}
