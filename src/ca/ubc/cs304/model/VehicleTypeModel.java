package ca.ubc.cs304.model;

public class VehicleTypeModel {
    private final String vtname;
    private final String features;
    private final double hrate;
    private final double drate;
    private final double wrate;
    private final double hirate;
    private final double dirate;
    private final double wirate;
    private final double krate;

    public VehicleTypeModel(String vtname, String features, double hrate, double drate, double wrate, double hirate, double dirate, double wirate, double krate) {
        this.vtname = vtname;
        this.features = features;
        this.hrate = hrate;
        this.drate = drate;
        this.wrate = wrate;
        this.hirate = hirate;
        this.dirate = dirate;
        this.wirate = wirate;
        this.krate = krate;
    }

    public String getVtname() {
        return vtname;
    }

    public String getFeatures() {
        return features;
    }

    public double getHrate() {
        return hrate;
    }

    public double getDrate() {
        return drate;
    }

    public double getWrate() {
        return wrate;
    }

    public double getHirate() {
        return hirate;
    }

    public double getDirate() {
        return dirate;
    }

    public double getWirate() {
        return wirate;
    }

    public double getKrate() {
        return krate;
    }
}
