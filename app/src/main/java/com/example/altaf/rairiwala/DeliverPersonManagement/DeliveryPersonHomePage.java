package com.example.altaf.rairiwala.DeliverPersonManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.AccountManagment.CheckInterNet;
import com.example.altaf.rairiwala.AccountManagment.ConnectToInternet;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;
import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.Delivery_Person_Adapter;
import com.example.altaf.rairiwala.RairriWalaManagment.NewOrderAdapter;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerHomePage;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerNewOrderList;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SaveToken;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryPersonHomePage extends AppCompatActivity {
    TextView txtViewCount;
    int count = 0;
    List<Order> ordersList;
    RecyclerView recyclerView;
    AssignedOrderAdapter assignedOrderAdapter;
    ProgressBar progressBar;
    TextView message;

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
        getSupportActionBar().setTitle("Home");
        txtViewCount = findViewById(R.id.txtCount);
        recyclerView = findViewById(R.id.assign_order_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersList = new ArrayList<>();
        message = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progressBar);
        fillOrders(SharedPrefManager.getInstance(this).getDeliveryPerson().getDelivery_person_id());
        saveToken();
    }

    public void fillOrders(final int delivery_person_id) {
        if (delivery_person_id > 0) {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.DeliveryPersonOrders,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                //converting the string to json array object
                                JSONArray array = new JSONArray(response);
                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject order = array.getJSONObject(i);
                                    CustomerAddress customerAddress = new CustomerAddress();
                                    Order newOrder = new Order();
                                    customerAddress.setName(order.getString("Name"));
                                    customerAddress.setLatiitude(order.getDouble("Latitude"));
                                    customerAddress.setLongitude(order.getDouble("Longitude"));
                                    customerAddress.setStreetName(order.getString("StreetName"));
                                    customerAddress.setHouseName(order.getString("House_Number"));
                                    newOrder.setCustomerAddress(customerAddress);
                                    newOrder.setVendor_id(order.getInt("Vendor_Id"));
                                    newOrder.setCustomer_id(order.getInt("Customer_Id"));
                                    newOrder.setOrder_status(order.getString("Order_Status"));
                                    newOrder.setOrder_time(order.getString("DateTime"));
                                    newOrder.setOrder_id(order.getInt("Order_Id"));
                                    newOrder.setDeliveryperson_id(order.getInt("DeliverPerson"));
                                    ordersList.add(newOrder);


                                }

                                assignedOrderAdapter = new AssignedOrderAdapter(DeliveryPersonHomePage.this, ordersList);
                                recyclerView.setAdapter(assignedOrderAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                message.setVisibility(View.VISIBLE);
                                message.setText("No New Orders");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            message.setVisibility(View.VISIBLE);
                            message.setText("Error");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("delivery_person_id", String.valueOf(delivery_person_id));

                    return params;
                }

            };


            //adding our stringrequest to queue
            Volley.newRequestQueue(this).add(stringRequest);
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
        getMenuInflater().inflate(R.menu.delivery_person, menu);
        final View notificaitons = menu.findItem(R.id.actionNotifications).getActionView();

        txtViewCount = (TextView) notificaitons.findViewById(R.id.txtCount);
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
                //    TODO
                Toast.makeText(DeliveryPersonHomePage.this, "" + txtViewCount.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

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

    public void saveToken() {
        if (SharedPrefManager.getInstance(DeliveryPersonHomePage.this).getPersonId() != 0) {
            if (!SharedPrefManagerFirebase.getInstance(DeliveryPersonHomePage.this).getToken().equals(SharedPrefManagerFirebase.getInstance(DeliveryPersonHomePage.this).getTokenUpdated()) && SharedPrefManagerFirebase.getInstance(DeliveryPersonHomePage.this).getToken() != "no") {
                new SaveToken(DeliveryPersonHomePage.this).saveToken();
            }
        } else {
            Toast.makeText(this, "Some error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("newOrderAssign"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                int notif = Integer.parseInt(txtViewCount.getText().toString());
                notif = notif + 1;
                txtViewCount.setText(Integer.toString(notif));
                txtViewCount.setVisibility(View.VISIBLE);


            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        SharedPrefManagerFirebase.getInstance(this).saveActivityState(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPrefManagerFirebase.getInstance(this).saveActivityState(false);
    }


}
