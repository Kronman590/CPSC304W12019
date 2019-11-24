package ca.ubc.cs304.model;

public class LocationCityModel {
    private final String location;
    private final String city;

    public LocationCityModel(String location, String city) {
        this.location = location;
        this.city = city;
    }

    public String getLocation() { return location; }

    public String getCity() {
        return city;
    }
}
