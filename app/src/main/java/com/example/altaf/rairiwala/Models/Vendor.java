package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class Vendor extends Person {
    private int vendor_id;
    private String shop_name;
    private double latitude;
    private double longitude;
    private double distance;
    private String shop_status;

    public void setShop_status(String shop_status) {
        this.shop_status = shop_status;
    }

    public String getShop_status() {
        return shop_status;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
