package com.example.altaf.rairiwala.AccountManagment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.Models.Customer;
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
        }
        Customer customer = SharedPrefManager.getInstance(this).getCustomer();
        Vendor vendor = SharedPrefManager.getInstance(AppStartUpPage.this).getSeller();
        if (customer != null) {
            if (customer.getRule().equals("CUSTOMER")) {
                startActivity(new Intent(AppStartUpPage.this, CustomerHomePage.class));
                this.finish();
            }
        } else if (vendor != null) {
            if (vendor.getRule().equals("SELLER")) {
                startActivity(new Intent(AppStartUpPage.this, SellerHomePage.class));
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
    }

}
