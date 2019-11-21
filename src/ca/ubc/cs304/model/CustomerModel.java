package ca.ubc.cs304.model;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class CustomerModel {
	private final int cellphone;
	private final String name;
	private final String address;
	private final String dlicense;

	public CustomerModel(int cellphone, String name, String address, String dlicense) {
		this.cellphone = cellphone;
		this.name = name;
		this.address = address;
		this.dlicense = dlicense;
	}

	public int getCellphone() {
		return cellphone;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getDlicense() {
		return dlicense;
	}
}
