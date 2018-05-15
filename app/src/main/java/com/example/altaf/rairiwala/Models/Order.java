package com.example.altaf.rairiwala.Models;

import java.util.List;

/**
 * Created by AltafHussain on 3/12/2018.
 */

public class Order {
    private CustomerAddress customerAddress;
    private List<Product> productArrayList;
    private OrderDetails orderDetails;
    private int order_id;
    private int customer_id;
    private int vendor_id;
    private int deliveryperson_id;
    private  String order_status;
    private String  order_time;
    private  int totalbill;

    public void setTotalbill(int totalbill) {
        this.totalbill = totalbill;
    }

    public int getTotalbill() {
        return totalbill;
    }

    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setProductArrayList(List<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setDeliveryperson_id(int deliveryperson_id) {
        this.deliveryperson_id = deliveryperson_id;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public void setOrder_time(String  order_time) {
        this.order_time = order_time;
    }

    public CustomerAddress getCustomerAddress() {
        return customerAddress;
    }

    public List<Product> getProductArrayList() {
        return productArrayList;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public int getDeliveryperson_id() {
        return deliveryperson_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getOrder_time() {
        return order_time;
    }
}
