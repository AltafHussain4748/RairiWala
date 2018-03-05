package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class CustomerAddress {
    private  String houseName;
    private  String streetName;
    private  double latiitude;
    private  double longitude;

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setLatiitude(double latiitude) {
        this.latiitude = latiitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getStreetName() {
        return streetName;
    }

    public double getLatiitude() {
        return latiitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
