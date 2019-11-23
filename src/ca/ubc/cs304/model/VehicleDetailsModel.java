package ca.ubc.cs304.model;

public class VehicleDetailsModel {
    private final String make;
    private final String model;
    private final int year;
    private final String color;
    private final String vtname;
    private final String location;
    private final String city;

    public String getLocation() {
        return location;
    }

    public String getCity() {
        return city;
    }

    public VehicleDetailsModel(String make, String model, int year, String color, String vtname, String location, String city) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.vtname = vtname;
        this.location = location;
        this.city = city;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public String getColor() {
        return color;
    }

    public String getVtname() {
        return vtname;
    }

    @Override
    public String toString() {
        return "VehicleDetailsModel{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", vtname='" + vtname + '\'' +
                '}';
    }
}
