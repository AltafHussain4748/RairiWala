package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class Customer extends  Person {
    private  int customer_id;
    CustomerAddress customerAddress;

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
    }

    public CustomerAddress getCustomerAddress() {
        return customerAddress;
    }
}
