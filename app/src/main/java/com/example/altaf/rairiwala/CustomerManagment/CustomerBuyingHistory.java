package com.example.altaf.rairiwala.CustomerManagment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.History;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerBuyingHistory extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView message;
    List<History> productList;
    boolean isScrolling = false;
    int currentItems, totalItems, scrollItems;
    LinearLayoutManager linearLayoutManager;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_buying_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = findViewById(R.id.buyinghistory);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        productList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        message = findViewById(R.id.error_message);
        loadProducts(SharedPrefManager.getInstance(CustomerBuyingHistory.this).getCustomer().getCustomer_id());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (currentItems + scrollItems == totalItems && isScrolling) {
                    isScrolling = false;
                    loadProducts(SharedPrefManager.getInstance(CustomerBuyingHistory.this).getCustomer().getCustomer_id());
                }
            }
        });

    }

    private void loadProducts(final int customer_id) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "" + customer_id, Toast.LENGTH_SHORT).show();
        /*
        * Creating a String Request
        * The request type is GET defined by first parameter
        * The URL is defined in the second parameter
        * Then we have a Response Listener and a Error Listener
        * In response listener we will get the JSON response as a String
        * */
        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.POST, Constants.BuyingHistory,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                History history = new History();
                                history.setDateTime(product.getString("DateTime"));
                                history.setName(product.getString("Name"));
                                history.setPrice(product.getInt("totalbill"));
                                history.setPhoneNumber(product.getString("PhoneNumber"));
                                history.setOrder_id(product.getInt("Order_Id"));
                                history.setVendorId(product.getInt("Vendor_Id"));
                                productList.add(history);

                            }
                            CustomerBuyingHistoryAdapter adapter = new CustomerBuyingHistoryAdapter(CustomerBuyingHistory.this, productList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.scrollToPosition(count);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            message.setText(e.getMessage());

                            if (productList.size() > 0) {
                                message.setVisibility(View.GONE);
                            }else{
                                message.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.setText("Error while loading the products");
                        progressBar.setVisibility(View.GONE);
                        if (productList.size() > 0) {
                            message.setVisibility(View.GONE);
                        }else{
                            message.setVisibility(View.VISIBLE);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", String.valueOf(customer_id));
                if (productList.size() == 0) {
                    count = productList.size();
                    params.put("start", String.valueOf(productList.size()));
                } else {
                    count = productList.size() - 1;
                    params.put("start", String.valueOf(productList.size()));
                }


                return params;
            }

        };
        ;

        //adding our stringrequest to queue
        Volley.newRequestQueue(CustomerBuyingHistory.this).add(stringRequest);
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
