package com.example.altaf.rairiwala.CustomerManagment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.NewOrderAdapter;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerNewOrderList;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerOrderList extends AppCompatActivity {

    List<Order> ordersList;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_order_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = findViewById(R.id.customer_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        message = findViewById(R.id.error_message);
        getOrders(SharedPrefManager.getInstance(this).getCustomer().getCustomer_id());


    }

    public void getOrders(final int customer_id) {
        if (customer_id >= 0) {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_CUSTOMER_ORDERS,
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
                                    customerAddress.setName(SharedPrefManager.getInstance(CustomerOrderList.this).getCustomer().getName());
                                    customerAddress.setLatiitude(order.getDouble("latitude"));
                                    customerAddress.setLongitude(order.getDouble("longitude"));
                                    customerAddress.setStreetName(order.getString("street_name"));
                                    customerAddress.setHouseName(order.getString("house_name"));
                                    newOrder.setCustomerAddress(customerAddress);
                                    newOrder.setVendor_id(order.getInt("vendor_id"));
                                    newOrder.setCustomer_id(order.getInt("customer_id"));
                                    newOrder.setOrder_status(order.getString("order_status"));
                                    newOrder.setOrder_time(order.getString("order_time"));
                                    newOrder.setOrder_id(order.getInt("order_id"));
                                    if (order.getInt("delivery_person_id") != 0) {
                                        newOrder.setDeliveryperson_id(order.getInt("delivery_person_id"));
                                    } else {
                                        newOrder.setDeliveryperson_id(0);
                                    }

                                    newOrder.setDeliveryperson_id(0);
                                    ordersList.add(newOrder);


                                }

                                CustomerOrderListAdapter newOrderAdapter = new CustomerOrderListAdapter(CustomerOrderList.this, ordersList);
                                recyclerView.setAdapter(newOrderAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                message.setText(e.getMessage());
                                message.setVisibility(View.VISIBLE);
                                Toast.makeText(CustomerOrderList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            message.setText("Error while loading the products");
                            message.setVisibility(View.VISIBLE);
                            Toast.makeText(CustomerOrderList.this, "Error while loading the products", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("customer_id", String.valueOf(customer_id));

                    return params;
                }

            };


            //adding our stringrequest to queue
            Volley.newRequestQueue(this).add(stringRequest);
        }

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
