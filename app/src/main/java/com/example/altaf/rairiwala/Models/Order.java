package com.example.altaf.rairiwala.Models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class Order {
 private  CustomerAddress customerAddress;
 private  OrderDetails orderDetails;
 private ArrayList<Product> product;
 private  int orderId;
 private  String orderStatus;
 private Date order_date_time;

    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void setProduct(ArrayList<Product> product) {
        this.product = product;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrder_date_time(Date order_date_time) {
        this.order_date_time = order_date_time;
    }

    public CustomerAddress getCustomerAddress() {
        return customerAddress;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public ArrayList<Product> getProduct() {
        return product;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Date getOrder_date_time() {
        return order_date_time;
    }
}
