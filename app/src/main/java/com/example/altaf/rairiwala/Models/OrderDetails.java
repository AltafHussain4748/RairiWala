package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class OrderDetails {
    private  int quantity;
    private  int totlabill;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotlabill(int totlabill) {
        this.totlabill = totlabill;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotlabill() {
        return totlabill;
    }
}
