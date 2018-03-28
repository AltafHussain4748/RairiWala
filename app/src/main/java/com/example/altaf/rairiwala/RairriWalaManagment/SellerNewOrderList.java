package com.example.altaf.rairiwala.RairriWalaManagment;

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
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.example.altaf.rairiwala.CustomerManagment.ProductAdapter;
import com.example.altaf.rairiwala.CustomerManagment.ProductList;
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.Models.OrderDetails;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.Models.ProductDetails;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerNewOrderList extends AppCompatActivity {

    List<Order> ordersList;
    RecyclerView recyclerView;
    NewOrderAdapter newOrderAdapter;
    ProgressBar progressBar;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_new_order_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = findViewById(R.id.neworders_recyle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersList = new ArrayList<>();
        message = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.progressBar);
        fillOrders(SharedPrefManager.getInstance(this).getSeller().getVendor_id());
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("speedExceeded"));

    }

    public void fillOrders(final int vendor_id) {
        if (vendor_id >= 0) {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SELLER_NEW_ORDERS,
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
                                    newOrder.setDeliveryperson_id(0);
                                    ordersList.add(newOrder);


                                }

                                newOrderAdapter = new NewOrderAdapter(SellerNewOrderList.this, ordersList);
                                recyclerView.setAdapter(newOrderAdapter);

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
                    params.put("vendor_id", String.valueOf(vendor_id));

                    return params;
                }

            };


            //adding our stringrequest to queue
            Volley.newRequestQueue(this).add(stringRequest);
        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                try {
                    String ordersString = intent.getStringExtra("currentSpeed");
                    JSONObject orderObject = new JSONObject(ordersString);
                    CustomerAddress customerAddress;
                    Gson gson = new Gson();
                    Type listOfproductType = new TypeToken<CustomerAddress>() {
                    }.getType();
                    customerAddress = gson.fromJson(orderObject.getString("customerAddress"), listOfproductType);
                    Order newOrder = new Order();
                    newOrder.setCustomerAddress(customerAddress);
                    newOrder.setVendor_id(orderObject.getInt("vendor_id"));
                    newOrder.setCustomer_id(orderObject.getInt("customer_id"));
                    newOrder.setOrder_status(orderObject.getString("order_status"));
                    newOrder.setOrder_time(orderObject.getString("order_time"));
                    newOrder.setOrder_id(orderObject.getInt("order_id"));
                    if (ordersList.size() == 0) {
                        ordersList.add(newOrder);
                        newOrderAdapter = new NewOrderAdapter(SellerNewOrderList.this, ordersList);
                        recyclerView.setAdapter(newOrderAdapter);
                        message.setVisibility(View.GONE);

                        synchronized (ordersList) {
                            newOrderAdapter.notify();
                        }
                    } else {
                        ordersList.add(0, newOrder);
                        newOrderAdapter.notifyItemInserted(0);
                        recyclerView.scrollToPosition(0);
                    }

                } catch (Exception e) {
               /* Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                message.setVisibility(View.VISIBLE);
                message.setText(e.getMessage() + "error");*/
                }

            }

            //  ... react to local broadcast message
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        SharedPrefManagerFirebase.getInstance(this).saveStateActivityNewOrderListSeller(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPrefManagerFirebase.getInstance(this).saveStateActivityNewOrderListSeller(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
