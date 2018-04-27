package com.example.altaf.rairiwala.PerformanceMonitering;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.FeedBack;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.Models.ProductDetails;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.StockManagment.StockDetailsAdatpter;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendorReviewList extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView message;
    List<FeedBack> feedBackList;
    boolean isScrolling = false;
    int currentItems, totalItems, scrollItems;
    LinearLayoutManager linearLayoutManager;
    int count = 0;
    int vendor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_vendor_review_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        vendor_id = bundle.getInt("vendor_id");
        recyclerView = findViewById(R.id.review_list);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(VendorReviewList.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        feedBackList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        message = findViewById(R.id.error_message);
        loadReviews(vendor_id);
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
                    loadReviews(vendor_id);
                }
            }
        });

    }

    private void loadReviews(final int vendor_id) {
        progressBar.setVisibility(View.VISIBLE);
        /*
        * Creating a String Request
        * The request type is GET defined by first parameter
        * The URL is defined in the second parameter
        * Then we have a Response Listener and a Error Listener
        * In response listener we will get the JSON response as a String
        * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GETREVIEWS,
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
                                JSONObject fb = array.getJSONObject(i);
                                FeedBack feedBack = new FeedBack();
                                feedBack.setPoster_name(fb.getString("customer_name"));
                                feedBack.setDate(fb.getString("date"));
                                feedBack.setDescription(fb.getString("des"));
                                feedBack.setStars(fb.getInt("stars"));
                                feedBack.setType(fb.getString("type"));
                                feedBack.setVendor_id(fb.getInt("vendor_id"));
                                feedBackList.add(feedBack);

                            }

                            ReviewListAdapter adapter = new ReviewListAdapter(VendorReviewList.this, feedBackList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.scrollToPosition(count);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            message.setText("No Products");
                            message.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.setText("Error while loading the products");
                        progressBar.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", String.valueOf(vendor_id));
                if (feedBackList.size() == 0) {
                    count = feedBackList.size();
                    params.put("start", String.valueOf(feedBackList.size()));
                } else {
                    count = feedBackList.size()-1;
                    params.put("start", String.valueOf(feedBackList.size()));
                }


                return params;
            }

        };
        ;

        //adding our stringrequest to queue
        Volley.newRequestQueue(VendorReviewList.this).add(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_review_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        } else if (d == R.id.add_review) {
            Intent intent = new Intent(VendorReviewList.this, Rating_Stars_Activity.class);
            intent.putExtra("vendor_id", vendor_id);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}
