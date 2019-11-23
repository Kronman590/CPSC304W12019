package ca.ubc.cs304.model;
import java.sql.Timestamp;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class ReservationModel {
	private final String confNo;
	private final String vtname;
	private final String dlicense;
	private final Timestamp fromDateTime;
	private final Timestamp toDateTime;

	//for creation
	public ReservationModel(VehicleTypeModel vtype, CustomerModel customer,
							Timestamp fromDateTime, Timestamp toDateTime) {
		this.confNo = generateAlphaNumericString(10);
		this.vtname = vtype.getVtname();
		this.dlicense = customer.getDlicense();
		this.fromDateTime = fromDateTime;
		this.toDateTime = toDateTime;
	}

	//for retrieval
	public ReservationModel(String confNo, String vtname, String dlicense,
							Timestamp fromDateTime, Timestamp toDateTime) {
		this.confNo = confNo;
		this.vtname = vtname;
		this.dlicense = dlicense;
		this.fromDateTime = fromDateTime;
		this.toDateTime = toDateTime;
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

	public Timestamp getFromDateTime() {
		return fromDateTime;
	}

	public Timestamp getToDateTime() {
		return toDateTime;
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
