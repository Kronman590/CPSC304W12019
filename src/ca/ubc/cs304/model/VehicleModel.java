package ca.ubc.cs304.model;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class VehicleModel {
	private final String vlicense;
	private final String make;
	private final String model;
	private final int year;
	private final String color;
	private final int odometer;
	private final String status;
	private final String vtname;
	private final String location;
	private final String city;

	public VehicleModel(String vlicense, VehicleDetailsModel details, int odometer,
						String status, VehicleTypeModel vtype, String location, String city) {
		this.vlicense = vlicense;
		this.make = details.getMake();
		this.model = details.getModel();
		this.year = details.getYear();
		this.color = details.getColor();
		this.odometer = odometer;
		this.status = status;
		this.vtname = vtype.getVtname();
		this.location = location;
		this.city = city;
	}

	public String getVlicense() {
		return vlicense;
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	public String getColor() {
		return color;
	}

	public int getOdometer() {
		return odometer;
	}

	public String getStatus() {
		return status;
	}

	public int getYear() {
		return year;
	}

	public String getVtname() {
		return vtname;
	}

	public String getLocation() {
		return location;
	}

	public String getCity() {
		return city;
	}
}
