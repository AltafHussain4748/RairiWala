package com.example.altaf.rairiwala.RairriWalaManagment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.altaf.rairiwala.AccountManagment.AppStartUpPage;
import com.example.altaf.rairiwala.AccountManagment.CheckInterNet;
import com.example.altaf.rairiwala.AccountManagment.ConnectToInternet;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

public class SellerHomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seller_home_page, menu);
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
            SharedPrefManager.getInstance(SellerHomePage.this).logOut();
            startActivity(new Intent(this, UserLogin.class));
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //checking internet permission
        if (!new CheckInterNet(SellerHomePage.this).isNetworkAvailable()) {
            startActivity(new Intent(SellerHomePage.this, ConnectToInternet.class));
        }else{
            Vendor vendor = SharedPrefManager.getInstance(this).getSeller();
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.extra_information) {
                startActivity(new Intent(SellerHomePage.this, AddSellerExtraInformation.class));
                // fragment = new AddSellerExtraInformation();
            } else if (id == R.id.add_product) {
                if (vendor.getVendor_id() <= 0) {
                    Toast.makeText(this, "Please add location details first", Toast.LENGTH_SHORT).show();
                } else {

                    fragment = new ADDProductCategoryDisplay();
                }

            } else if (id == R.id.view_stock) {
                // fragment = new ViewStock();
            } else if (id == R.id.selling_history) {
                // fragment = new ViewSellingHistory();
            } else if (id == R.id.new_order) {
                //  fragment = new NewOrders();
            } else if (id == R.id.assignedorder) {
                //  fragment = new AssignedOrders();
            } else if (id == R.id.account_details) {
                fragment = new FragmentAccountDetail();
            }

//replace the current fragment
            if (fragment != null) {
                FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
                android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit(); // save the changes
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
