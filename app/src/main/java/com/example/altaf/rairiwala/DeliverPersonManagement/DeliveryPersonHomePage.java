package com.example.altaf.rairiwala.DeliverPersonManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.altaf.rairiwala.AccountManagment.CheckInterNet;
import com.example.altaf.rairiwala.AccountManagment.ConnectToInternet;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;
import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

public class DeliveryPersonHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_person_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!new CheckInterNet(DeliveryPersonHomePage.this).isNetworkAvailable()) {
            startActivity(new Intent(DeliveryPersonHomePage.this, ConnectToInternet.class));
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPrefManager.getInstance(DeliveryPersonHomePage.this).logOut();
            startActivity(new Intent(this, UserLogin.class));
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
