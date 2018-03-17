package com.example.altaf.rairiwala.RairriWalaManagment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.AccountManagment.CheckInterNet;
import com.example.altaf.rairiwala.AccountManagment.ConnectToInternet;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SaveToken;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SellerHomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        saveToken();
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
        MenuItem swithcItem = menu.findItem(R.id.show_status);
        swithcItem.setActionView(R.layout.show_protected_switch);
        final Switch sw = menu.findItem(R.id.show_status).getActionView().findViewById(R.id.shop_status);
        String shop_status = null;
        int id = 0;
        id = SharedPrefManager.getInstance(this).getSeller().getVendor_id();
        if (id != 0) {

            shop_status = SharedPrefManager.getInstance(this).getSeller().getShop_status().toString();
        }
        if (shop_status != null) {
            if (shop_status.equals("Close")) {
                sw.setChecked(false);

            } else if (shop_status.equals("Open")) {
                sw.setChecked(true);
            } else {
                sw.setChecked(false);
            }
        } else {
            sw.setChecked(false);
        }


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Toast.makeText(SellerHomePage.this, "Open", Toast.LENGTH_SHORT).show();
                    setShopStatus("Open");
                } else {
                    Toast.makeText(SellerHomePage.this, "Close", Toast.LENGTH_SHORT).show();
                    setShopStatus("Close");
                }
            }
        });


        return true;
    }

    //set the shop sattus function
    private void setShopStatus(final String status) {
        final int vendor_id = SharedPrefManager.getInstance(this).getSeller().getVendor_id();
        if (vendor_id <= 0 && status == null) {
            Toast.makeText(this, "Some Error", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_SHOP_STATUS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                //converting the string to json array object
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("error") == false) {
                                    Vendor vendor = SharedPrefManager.getInstance(SellerHomePage.this).getSeller();
                                    String status = jsonObject.getString("status");
                                    vendor.setShop_status(status);
                                    SharedPrefManager.getInstance(SellerHomePage.this).addSellerToPref(vendor);
                                    Toast.makeText(SellerHomePage.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SellerHomePage.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(SellerHomePage.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(SellerHomePage.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("shop_status", status);
                    params.put("vendor_id", String.valueOf(vendor_id));

                    return params;
                }

            };
            ;

            //adding our stringrequest to queue
            RequestHandler.getInstance(SellerHomePage.this).addToRequestQueue(stringRequest);
        }
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
        } else if (id == R.id.show_status) {
            Switch status = findViewById(R.id.shop_status);
            Toast.makeText(SellerHomePage.this, "   Changed", Toast.LENGTH_SHORT).show();
            status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Toast.makeText(SellerHomePage.this, "   Changed", Toast.LENGTH_SHORT).show();
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //checking internet permission
        if (!new CheckInterNet(SellerHomePage.this).isNetworkAvailable()) {
            startActivity(new Intent(SellerHomePage.this, ConnectToInternet.class));
            this.finish();
        } else {
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
                startActivity(new Intent(SellerHomePage.this, SellerNewOrderList.class));
                //  fragment = new NewOrders();
            } else if (id == R.id.assignedorder) {
                //  fragment = new AssignedOrders();
            } else if (id == R.id.account_details) {
                fragment = new FragmentAccountDetail();
            } else if (id == R.id.add_delivery_person) {
                if (vendor.getVendor_id() <= 0) {
                    Toast.makeText(this, "Please add location details first", Toast.LENGTH_SHORT).show();
                } else {

                    fragment = new DeliveryPersonManagment();
                }
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

    public void saveToken() {
        if (SharedPrefManager.getInstance(SellerHomePage.this).getPersonId() != 0) {
            if (!SharedPrefManagerFirebase.getInstance(SellerHomePage.this).getToken().equals(SharedPrefManagerFirebase.getInstance(SellerHomePage.this).getTokenUpdated()) && SharedPrefManagerFirebase.getInstance(SellerHomePage.this).getToken() != "no") {
                new SaveToken(SellerHomePage.this).saveToken();
            }
        }
    }
}