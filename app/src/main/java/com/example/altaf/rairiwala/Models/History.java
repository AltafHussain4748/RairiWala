package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 4/27/2018.
 */

public class History {
    int order_id;
    int price;
    int vendorId;
    String dateTime;
    String name;
    String phoneNumber;
    int customerid;

    public int getOrder_id() {
        return order_id;
    }

    public int getPrice() {
        return price;
    }

    public int getVendorId() {
        return vendorId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getCustomerid() {
        return customerid;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCustomerid(int customerid) {
        this.customerid = customerid;
    }
}
