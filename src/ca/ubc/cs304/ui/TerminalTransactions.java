package ca.ubc.cs304.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.ui.CustomerTransactions;
import ca.ubc.cs304.model.CreditCard;

/**
 * The class is only responsible for handling terminal text inputs. 
 */
public class TerminalTransactions {
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	private static final int INVALID_INPUT = Integer.MIN_VALUE;
	private static final int EMPTY_INPUT = 0;
	
	private BufferedReader bufferedReader = null;
	private TerminalTransactionsDelegate delegate = null;

	public TerminalTransactions() {
	}

	/**
	 * Displays simple text interface
	 */ 
	public void showMainMenu(TerminalTransactionsDelegate delegate) {
		this.delegate = delegate;
		
	    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;
		
		while (choice != 3) {
			System.out.println();
			System.out.println("1. Customer");
			System.out.println("2. Clerk");
			System.out.println("3. Quit");
			System.out.print("Please choose one of the above 3 options: ");

			choice = readInteger(false);

			System.out.println(" ");

			if (choice != INVALID_INPUT) {
				switch (choice) {
				case 1:  
					handleCustomerOption();
					break;
				case 2:  
					handleClerkOption();
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

	private void handleCustomerOption(){
     	//TODO: initiate new class with logic
		CustomerTransactions customerTransactions = new CustomerTransactions(bufferedReader, delegate);
		customerTransactions.showCustomerMenu();
	}

	private void handleClerkOption(){
		int choice = INVALID_INPUT;
		String location, city;

		while (choice != 3) {
			System.out.println();
			System.out.println("1. Rent Vehicle");
			System.out.println("2. Return Vehicle");
			System.out.println("3. Create daily rental report");
			System.out.println("4. Create daily branch rental report");
			System.out.println("5. Create daily return report");
			System.out.println("6. Create daily branch return report");
			System.out.println("7. Quit");
			System.out.print("Please choose one of the above 7 options: ");

			choice = readInteger(false);

			System.out.println(" ");

			if (choice != INVALID_INPUT) {
				switch (choice) {
					case 1:
						handleMakeRental();
						break;
					case 2:
						handleReturnVehicle();
						break;
					case 3:
						delegate.dailyRental();
						break;
					case 4:
						System.out.println("Please enter the branch location");
						location = readLine();
						System.out.println("Please enter the branch city");
						city = readLine();
						delegate.dailyBranchRental(location, city);
						break;
					case 5:
						delegate.dailyReturn();
						break;
					case 6:
                        System.out.println("Please enter the branch location");
                        location = readLine();
                        System.out.println("Please enter the branch city");
                        city = readLine();
                        delegate.dailyBranchReturn(location, city);
                        break;
					case 7:
						handleQuitOption();
						break;
					default:
						System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
						break;
				}
			}
		}
	}


	private void handleMakeRental() {
		String rid = null;
		Timestamp fromDateTime = null;
		Timestamp toDateTime = null;
		while (rid == null || rid.length() <= 0) {
			System.out.print("Is there a reservation? If so, provide the reservation ID. Else, say 'No' : ");
			rid = readLine().trim();
		}

		if (rid.equalsIgnoreCase("no")) {
			while (fromDateTime == null) {
				System.out.print("When will the rental begin? (enter as YYYY-MM-DD HH:mm): ");
				try {
					// java.util.Date tempDate = new SimpleDateFormat("DD/MM/YYYY HH:mm").parse();
					fromDateTime = Timestamp.valueOf(readLine()+":00.00");
				} catch (Exception err) {
					System.out.println("Invalid Date input. Please try again.");
					fromDateTime = null;
				}
			}
			while (toDateTime == null) {
				System.out.print("When will the rental end? (enter as YYYY-MM-DD HH:mm) : ");
				try {
					//java.util.Date tempDate = new SimpleDateFormat("DD/MM/YYYY HH:mm").parse(readLine().trim());
					toDateTime = Timestamp.valueOf(readLine()+":00.00");
				} catch (Exception err) {
					System.out.println("Invalid Date input. Please try again.");
					toDateTime = null;
				}
			}
		}

		String vlicense = null;
		while (vlicense == null || vlicense.length() <= 0) {
			System.out.print("Please enter the rental vehicle's license plate: ");
			vlicense = readLine().trim();
		}

		if (!rid.equalsIgnoreCase("no")) {
			//if (matchesVType(vlicense, ))
			// TODO check if vehicle license plate matches reservation vehicle type
		}

		int odometer = INVALID_INPUT;
		while (odometer == INVALID_INPUT) {
			System.out.print("Please enter the vehicle's odometer current value (in km): ");
			odometer = readInteger(true);
		}

		String dlicense = null;
		while (dlicense == null || dlicense.length() <= 0) {
			System.out.print("Please enter the customer's driver's license number: ");
			dlicense = readLine().trim();
		}

		String cardName = null;
		while (cardName == null || cardName.length() <= 0) {
			System.out.print("Please enter the customer's credit card number: ");
			cardName = readLine().trim();
		}

		String cardNo = null;
		while (cardNo == null || cardNo.length() <= 0) {
			System.out.print("Please enter the customer's credit card full name: ");
			cardNo = readLine().trim();
		}

		String cardExp = null;
		while (cardExp == null || cardExp.length() <= 0) {
			System.out.print("Please enter the customer's credit card expiration date (as MM/YY): ");
			cardExp = readLine().trim();
		}

		CreditCard card = new CreditCard(cardNo, cardName, cardExp);
		if (rid.equalsIgnoreCase("no")) {
			delegate.makeRental(vlicense, dlicense, odometer, card, null, fromDateTime, toDateTime);
		} else {
			delegate.makeRental(vlicense, dlicense, odometer, card, rid, fromDateTime, toDateTime);
		}
	}

	private void handleReturnVehicle() {
		String rid = null;
		while (rid == null || rid.length() <= 0) {
			System.out.print("Please enter the rental id number: ");
			rid = readLine().trim();
		}

		Timestamp retDateTime = null;
		while (retDateTime == null) {
			System.out.print("When was the vehicle returned? (enter as YYYY-MM-DD HH:mm): ");
			try {
				retDateTime = Timestamp.valueOf(readLine()+":00.00");
			} catch (Exception err) {
				System.out.println("Invalid Date input. Please try again.");
				retDateTime = null;
			}
		}

		int odometer = INVALID_INPUT;
		while (odometer == INVALID_INPUT) {
			System.out.print("Please enter the vehicle's odometer returning value (in km): ");
			odometer = readInteger(true);
		}

		String fullTank = null;
		while (fullTank == null || fullTank.length() <= 0) {
			System.out.print("Did the vehicle return with a full tank? Enter 'yes' or 'no': ");
			fullTank = readLine().trim();
		}
		boolean full = fullTank.equalsIgnoreCase("yes");

		delegate.returnVehicle(rid, retDateTime, odometer, full);
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

	//	private void handleDeleteOption() {
//		int branchId = INVALID_INPUT;
//		while (branchId == INVALID_INPUT) {
//			System.out.print("Please enter the branch ID you wish to delete: ");
//			branchId = readInteger(false);
//			if (branchId != INVALID_INPUT) {
//				delegate.deleteBranch(branchId);
//			}
//		}
//	}
//
//	private void handleInsertOption() {
//		int id = INVALID_INPUT;
//		while (id == INVALID_INPUT) {
//			System.out.print("Please enter the branch ID you wish to insert: ");
//			id = readInteger(false);
//		}
//
//		String name = null;
//		while (name == null || name.length() <= 0) {
//			System.out.print("Please enter the branch name you wish to insert: ");
//			name = readLine().trim();
//		}
//
//		// branch address is allowed to be null so we don't need to repeatedly ask for the address
//		System.out.print("Please enter the branch address you wish to insert: ");
//		String address = readLine().trim();
//		if (address.length() == 0) {
//			address = null;
//		}
//
//		String city = null;
//		while (city == null || city.length() <= 0) {
//			System.out.print("Please enter the branch city you wish to insert: ");
//			city = readLine().trim();
//		}
//
//		int phoneNumber = INVALID_INPUT;
//		while (phoneNumber == INVALID_INPUT) {
//			System.out.print("Please enter the branch phone number you wish to insert: ");
//			phoneNumber = readInteger(true);
//		}
//
//		BranchModel model = new BranchModel(address,
//											city,
//											id,
//											name,
//											phoneNumber);
//		delegate.insertBranch(model);
//	}

//	private void handleUpdateOption() {
//		int id = INVALID_INPUT;
//		while (id == INVALID_INPUT) {
//			System.out.print("Please enter the branch ID you wish to update: ");
//			id = readInteger(false);
//		}
//
//		String name = null;
//		while (name == null || name.length() <= 0) {
//			System.out.print("Please enter the branch name you wish to update: ");
//			name = readLine().trim();
//		}
//
//		delegate.updateBranch(id, name);
//	}
}
