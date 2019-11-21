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
	
	public ReservationModel(VehicleType vtype, Customer customer,
							String fromDate, String fromTime, String toDate, String toTime) {
		this.confNo = randomnumber;
		this.vtname = vtype.vtname;
		this.dlicense = customer.dlicense;
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
}
