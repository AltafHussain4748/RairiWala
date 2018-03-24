package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class DeliveryPerson extends Person {
    private int delivery_person_id;
    private int vendor_id;
    private  String isFree;

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {

        this.isFree = isFree;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setDelivery_person_id(int delivery_person_id) {
        this.delivery_person_id = delivery_person_id;
    }

    public int getDelivery_person_id() {
        return delivery_person_id;
    }
}
