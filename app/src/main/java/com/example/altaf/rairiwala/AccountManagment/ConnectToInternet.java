package com.example.altaf.rairiwala.AccountManagment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.DeliverPersonManagement.DeliveryPersonHomePage;
import com.example.altaf.rairiwala.Models.Customer;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerHomePage;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

public class ConnectToInternet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_connect_to_internet);
    }

    public void openConnection(View view) {
        CheckInterNet checkInterNet = new CheckInterNet(this);
        if (checkInterNet.isNetworkAvailable()) {
            Customer customer = SharedPrefManager.getInstance(this).getCustomer();
            Vendor vendor = SharedPrefManager.getInstance(this).getSeller();
            DeliveryPerson deliveryPerson = SharedPrefManager.getInstance(this).getDeliveryPerson();
            if (customer != null) {
                if (customer.getRule().equals("CUSTOMER")) {
                    startActivity(new Intent(this, CustomerHomePage.class));
                    this.finish();
                }
            } else if (vendor != null) {
                if (vendor.getRule().equals("SELLER")) {
                    Intent intent = new Intent(this, SellerHomePage.class);
                    intent.putExtra("stockDetail", "mainPage");

                    startActivity(intent);
                    this.finish();
                }

            } else if (deliveryPerson != null) {
                if (deliveryPerson.getRule().equals("DP")) {
                    startActivity(new Intent(this, DeliveryPersonHomePage.class));
                    this.finish();
                }
            }
        }
      /*  startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        ConnectToInternet.this.finish();*/
    }
}
