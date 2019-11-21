package ca.ubc.cs304.model;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class BranchModel {
	private final String location;
	private final String city;
	private final int id;
	private final String name;	
	private final int phoneNumber;
	
	public BranchModel(String location, String city, int id, String name, int phoneNumber) {
		this.location = location;
		this.city = city;
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public String getLocation() {
		return location;
	}

	public String getCity() {
		return city;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}
}
