package ca.ubc.cs304.model;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class ReservationModel {
	private final String confNo;
	private final String vtname;
	private final String dlicense;
	private final String fromDate;
	private final String fromTime;
	private final String toDate;
	private final String toTime;

	//for creation
	public ReservationModel(VehicleTypeModel vtype, CustomerModel customer,
							String fromDate, String fromTime, String toDate, String toTime) {
		this.confNo = generateAlphaNumericString(10);
		this.vtname = vtype.getVtname();
		this.dlicense = customer.getDlicense();
		this.fromDate = fromDate;
		this.fromTime = fromTime;
		this.toDate = toDate;
		this.toTime = toTime;
	}

	//for retrieval
	public ReservationModel(String confNo, String vtname, String dlicense,
							String fromDate, String fromTime, String toDate,
							String toTime) {
		this.confNo = confNo;
		this.vtname = vtname;
		this.dlicense = dlicense;
		this.fromDate = fromDate;
		this.fromTime = fromTime;
		this.toDate = toDate;
		this.toTime = toTime;
	}

	public String getconfNo() {
		return confNo;
	}

	public String getVtname() {
		return vtname;
	}

	public String getDlicense() {
		return dlicense;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getFromTime() {
		return fromTime;
	}

	public String getToDate() {
		return toDate;
	}

	public String getToTime() {
		return toTime;
	}

	private String generateAlphaNumericString(int n) {

		// chose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "0123456789"
				+ "abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index
					= (int)(AlphaNumericString.length()
					* Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString
					.charAt(index));
		}

		return sb.toString();
	}
}
