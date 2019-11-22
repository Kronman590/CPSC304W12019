package ca.ubc.cs304.model;

import oracle.sql.DATE;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class ReservationModel {
	private final String confNo;
	private final String vtname;
	private final String dlicense;
	private final Date fromDate;
	private final Timestamp fromTime;
	private final Date toDate;
	private final Timestamp toTime;

	//for creation
	public ReservationModel(VehicleTypeModel vtype, CustomerModel customer,
							Date fromDate, Timestamp fromTime, Date toDate, Timestamp toTime) {
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
							Date fromDate, Timestamp fromTime, Date toDate,
							Timestamp toTime) {
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

	public Date getFromDate() {
		return fromDate;
	}

	public Timestamp getFromTime() {
		return fromTime;
	}

	public Date getToDate() {
		return toDate;
	}

	public Timestamp getToTime() {
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
