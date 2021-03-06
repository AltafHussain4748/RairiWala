package com.example.altaf.rairiwala.AccountManagment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.DeliverPersonManagement.DeliveryPersonHomePage;
import com.example.altaf.rairiwala.Models.Customer;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerHomePage;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

public class AppStartUpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_start_up_page);
        //checking internet permission
        if (!new CheckInterNet(AppStartUpPage.this).isNetworkAvailable()) {
            startActivity(new Intent(AppStartUpPage.this, ConnectToInternet.class));
            this.finish();
        } else if (InternetConnection.checkConnection(this) == false) {
            startActivity(new Intent(AppStartUpPage.this, ConnectToInternet.class));
            this.finish();
        } else {
            Customer customer = SharedPrefManager.getInstance(this).getCustomer();
            Vendor vendor = SharedPrefManager.getInstance(AppStartUpPage.this).getSeller();
            DeliveryPerson deliveryPerson = SharedPrefManager.getInstance(this).getDeliveryPerson();
            if (customer != null) {
                if (customer.getRule().equals("CUSTOMER")) {
                    startActivity(new Intent(AppStartUpPage.this, CustomerHomePage.class));
                    this.finish();
                }
            } else if (vendor != null) {
                if (vendor.getRule().equals("SELLER")) {
                    Intent intent = new Intent(AppStartUpPage.this, SellerHomePage.class);
                    intent.putExtra("stockDetail", "mainPage");

                    startActivity(intent);
                    this.finish();
                }

            } else if (deliveryPerson != null) {
                if (deliveryPerson.getRule().equals("DP")) {
                    startActivity(new Intent(AppStartUpPage.this, DeliveryPersonHomePage.class));
                    this.finish();
                }
            }
            ////CHECK WHICH TYPE OF SELLER IT IS
            findViewById(R.id.join_as_customer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AppStartUpPage.this, UserRegister.class);
                    intent.putExtra("TYPE", "CUSTOMER");
                    startActivity(intent);
                    AppStartUpPage.this.finish();
                }
            });
            findViewById(R.id.join_as_vendor).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AppStartUpPage.this, UserRegister.class);
                    intent.putExtra("TYPE", "SELLER");
                    startActivity(intent);
                }
            });
            TextView txt = findViewById(R.id.loginHaveAccount);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(AppStartUpPage.this, UserLogin.class));
                    AppStartUpPage.this.finish();
                }
            });
        }

    }

}
