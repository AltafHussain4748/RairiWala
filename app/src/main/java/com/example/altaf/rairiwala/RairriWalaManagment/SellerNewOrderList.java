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
import android.view.MenuItem;
import android.widget.Toast;

import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.Models.OrderDetails;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SellerNewOrderList extends AppCompatActivity {

    List<Order> ordersList;
    RecyclerView recyclerView;
    NewOrderAdapter newOrderAdapter;

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
        fillOrders();
    }

    public void fillOrders() {

        newOrderAdapter = new NewOrderAdapter(this, ordersList);
        recyclerView.setAdapter(newOrderAdapter);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                String ordersString = intent.getStringExtra("currentSpeed");
                JSONObject orderObject=new JSONObject(ordersString);
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
                ordersList.add(0, newOrder);
                newOrderAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
            } catch (Exception e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            //  ... react to local broadcast message
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("speedExceeded"));

    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
