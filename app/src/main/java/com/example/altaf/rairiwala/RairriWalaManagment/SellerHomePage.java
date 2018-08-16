package com.example.altaf.rairiwala.RairriWalaManagment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.AccountManagment.AccountDetail;
import com.example.altaf.rairiwala.AccountManagment.AppStartUpPage;
import com.example.altaf.rairiwala.AccountManagment.CheckInterNet;
import com.example.altaf.rairiwala.AccountManagment.ConnectToInternet;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;
import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.Models.Notifications;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.PerformanceMonitering.VendorReviewList;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.StockManagment.ADDProductCategoryDisplay;
import com.example.altaf.rairiwala.RairriWalaManagment.StockManagment.StockDetailsFragment;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.NotificationFragment;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SaveToken;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class SellerHomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView txtViewCount;
    List<Notifications> notificationsList;
    int vendor_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtViewCount = findViewById(R.id.notificationcount);

        notificationsList = new ArrayList<>();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        saveToken();
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment v
        try {
            Bundle bundle = getIntent().getExtras();
            String fragmenttoLaunch = bundle.getString("stockDetail");
            if (fragmenttoLaunch != null && fragmenttoLaunch.equals("stockDetail")) {
                fragmentTransaction.replace(R.id.frameLayout, new StockDetailsFragment());
            } else {
                fragmentTransaction.replace(R.id.frameLayout, new StockDetailsFragment());
            }
            fragmentTransaction.commit(); // save the changes
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        //broadcast reciever
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("speedExceeded"));
        if (SharedPrefManager.getInstance(this).getSeller() != null) {
            vendor_id = SharedPrefManager.getInstance(this).getSeller().getVendor_id();
            if (vendor_id > 0) {
                SaveToken saveToken = new SaveToken(this);
                saveToken.checkSTock();
            }
        }

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.usernameAppBar);
        navUsername.setText(SharedPrefManager.getInstance(this).getSeller().getName());
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
        final View notificaitons = menu.findItem(R.id.actionNotifications).getActionView();
        txtViewCount = (TextView) notificaitons.findViewById(R.id.notificationcount);
        final DatabaseHandling databaseHandling = new DatabaseHandling(SellerHomePage.this);
        int count = 0;
        count = SharedPrefManager.getInstance(this).getSeller().getVendor_id();
        if (count != 0) {
            notificationsList = databaseHandling.getAllNotes(count);
            txtViewCount.setText(Integer.toString(notificationsList.size()));
        }


        if (txtViewCount.getText().equals("0")) {
            txtViewCount.setVisibility(View.GONE);
        }
        txtViewCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        notificaitons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(txtViewCount.getText().toString()) > 0) {
                   /* startActivity(new Intent(SellerHomePage.this, SellerNewOrderList.class));
                    txtViewCount.setText("0");
                    txtViewCount.setVisibility(View.GONE);*/
                   /*notificationsList=notificationsList = databaseHandling.getAllNotes();
                   Notifications notificationss=notificationsList.get(0);
                    Toast.makeText(SellerHomePage.this, notificationss.getTag(), Toast.LENGTH_SHORT).show();*/
                    Bundle bundle = new Bundle();
                    bundle.putString("rule", "vendor");
                    Fragment fragment = new NotificationFragment();
                    FragmentManager fm = getFragmentManager();
                    fragment.setArguments(bundle);
                    // create a FragmentTransaction to begin the transaction and replace the Fragment
                    android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    // replace the FrameLayout with new Fragment
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.commit();

                } else {
                    Toast.makeText(SellerHomePage.this, "No new Order", Toast.LENGTH_SHORT).show();
                }
                //    TODO

            }
        });
//end of notification management
        final MenuItem swithcItem = menu.findItem(R.id.show_status);
        swithcItem.setActionView(R.layout.show_protected_switch);
        final Switch sw = menu.findItem(R.id.show_status).getActionView().findViewById(R.id.shop_status);
        final TextView status = menu.findItem(R.id.show_status).getActionView().findViewById(R.id.isoff);
        String shop_status = null;
        int id = 0;
        id = SharedPrefManager.getInstance(this).getSeller().getVendor_id();
        if (id != 0) {

            shop_status = SharedPrefManager.getInstance(this).getSeller().getShop_status().toString();
        }
        if (shop_status != null) {
            //will try thread on it that will wait for 3 seconds
            if (shop_status.equals("Close")) {
                sw.setChecked(false);
                status.setText("Off");
            } else if (shop_status.equals("Open")) {
                sw.setChecked(true);
                status.setText("On");
            } else {
                sw.setChecked(false);
                status.setText("Off");

            }
        } else {
            sw.setChecked(false);
            status.setText("Off");
        }


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (vendor_id != 0) {
                    if (b) {
                        setShopStatus("Open", status);
                    } else {
                        setShopStatus("Close", status);

                    }
                } else {

                    Toast.makeText(SellerHomePage.this, "Please add shop information first", Toast.LENGTH_SHORT).show();
                    sw.setChecked(false);
                    status.setText("Off");
                }

            }
        });


        return true;
    }

    //set the shop sattus function
    private void setShopStatus(final String status, final TextView txtStatus) {
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
                                    if (status.equals("Close")) {
                                        txtStatus.setText("Off");
                                    } else if (status.equals("Open")) {
                                        txtStatus.setText("On");
                                    }
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
            DatabaseHandling handling = new DatabaseHandling(this);
            handling.deleteAllCategories();
            finishAffinity();
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
            vendor_id = vendor.getVendor_id();
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.extra_information) {
                startActivity(new Intent(SellerHomePage.this, AddSellerExtraInformation.class));
                // fragment = new AddSellerExtraInformation();
            } else if (id == R.id.add_product) {
                if (vendor_id <= 0) {
                    Toast.makeText(this, "Please add shop location details first", Toast.LENGTH_SHORT).show();
                } else {

                    fragment = new ADDProductCategoryDisplay();
                }

            } else if (id == R.id.view_stock) {
                if (vendor_id <= 0) {
                    Toast.makeText(this, "Please add shop location details first", Toast.LENGTH_SHORT).show();
                } else {

                    fragment = new StockDetailsFragment();
                }
            } else if (id == R.id.new_order) {
                if (vendor_id <= 0) {
                    Toast.makeText(this, "Please add shop location details first", Toast.LENGTH_SHORT).show();
                } else {

                    startActivity(new Intent(SellerHomePage.this, SellerNewOrderList.class));
                }

                //  fragment = new NewOrders();
            } else if (id == R.id.assignedorder) {
                if (vendor_id <= 0) {
                    Toast.makeText(this, "Please add shop location details first", Toast.LENGTH_SHORT).show();
                } else {

                    fragment = new GetVendorAssignedOrdersFragment();
                }
            } else if (id == R.id.account_details) {
                startActivity(new Intent(SellerHomePage.this, AccountDetail.class));
            } else if (id == R.id.add_delivery_person) {
                if (vendor_id <= 0) {
                    Toast.makeText(this, "Please add shop location details first", Toast.LENGTH_SHORT).show();
                } else {

                    fragment = new DeliveryPersonManagment();
                }
            } else if (id == R.id.selling_history) {
                if (vendor_id <= 0) {
                    Toast.makeText(this, "Please add shop location details first", Toast.LENGTH_SHORT).show();
                } else {

                    startActivity(new Intent(SellerHomePage.this, VendorSellingHistory.class));
                }

                //  fragment = new NewOrders();
            } else if (id == R.id.customer_review) {
                if (vendor_id <= 0) {
                    Toast.makeText(this, "Please add shop location details first", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(SellerHomePage.this, VendorReviewList.class);
                    intent.putExtra("vendor_id", vendor_id);
                    intent.putExtra("role", "seller");
                    startActivity(intent);
                }
            } else if (id == R.id.deActivateSeller) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(SellerHomePage.this);
                dialogBuilder
                        .withTitle("DeActivate Account")                                  //.withTitle(null)  no title
                        .withTitleColor("#FFFFFF")                                  //def
                        .withDividerColor("#11000000")                              //def
                        .withMessage("Do you want to DeActivate Account?")                     //.withMessage(null)  no Msg
                        .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                        .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
                        .withIcon(getResources().getDrawable(R.drawable.delete))
                        .withDuration(700)                                          //def
                        .withEffect(RotateBottom)                                         //def Effectstype.Slidetop
                        .withButton1Text("No")                                      //def gone
                        .withButton2Text("yes")                                  //def gone
                        .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                        //.setCustomView(View or ResId,context)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deactivateAccount(SharedPrefManager.getInstance(SellerHomePage.this).getPersonId(), "vendor");
                                SellerHomePage.this.finish();
                            }
                        })
                        .show();
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                if (intent.getAction() != null) {
                    int count = Integer.parseInt(txtViewCount.getText().toString());
                    count = count + 1;
                    txtViewCount.setText(Integer.toString(count));
                    txtViewCount.setVisibility(View.VISIBLE);
                }


            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        SharedPrefManagerFirebase.getInstance(this).saveActivityStateSellerHomePage(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPrefManagerFirebase.getInstance(this).saveActivityStateSellerHomePage(false);
    }

    public void deactivateAccount(final int id, final String type) {
        if (type != null && id > 0) {
            ///start string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.DeActivateAccount,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.getBoolean("error") == false) {

                                    SharedPrefManager.getInstance(SellerHomePage.this).logOut();
                                    DatabaseHandling handling = new DatabaseHandling(SellerHomePage.this);
                                    handling.deleteAllCategories();
                                    startActivity(new Intent(SellerHomePage.this, AppStartUpPage.class));
                                    finishAffinity();


                                    Toast.makeText(SellerHomePage.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SellerHomePage.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SellerHomePage.this, "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("person_id", String.valueOf(id));
                    params.put("type", type);


                    return params;
                }
            };
            RequestHandler.getInstance(SellerHomePage.this).addToRequestQueue(stringRequest);
            // end string request
        }

    }

}