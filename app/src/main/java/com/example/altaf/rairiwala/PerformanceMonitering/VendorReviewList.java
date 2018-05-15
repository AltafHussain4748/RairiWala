package com.example.altaf.rairiwala.PerformanceMonitering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.Models.FeedBack;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;

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
    String roleTytpe;
    TextView responseTime, price_reviewcount, quantity_reviewcount, quality_reviewcount;
    RatingBar price, quality, quantity;


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
        roleTytpe = bundle.getString("role");
        recyclerView = findViewById(R.id.review_list);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(VendorReviewList.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        feedBackList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        message = findViewById(R.id.error_message);
        responseTime = findViewById(R.id.responseTime);
        price = findViewById(R.id.priceRatingBar);
        quality = findViewById(R.id.qualityRatingBar);
        quantity = findViewById(R.id.quantityRatingBar);
        price_reviewcount = findViewById(R.id.priceRating);
        quantity_reviewcount = findViewById(R.id.quantityRating);
        quality_reviewcount = findViewById(R.id.qualityRating);
        getResponseTime(vendor_id);
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
                if (dy > 0) {
                    if (currentItems + scrollItems == totalItems && isScrolling) {
                        isScrolling = false;
                        loadReviews(vendor_id);

                    }
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
                            float price_avg = 0, quantity_avg = 0, quality_avg = 0;
                            int count_price = 0, count_quality = 0, count_quantity = 0;
                            for (FeedBack feedBack : feedBackList) {
                                if (feedBack.getType().equals("QUANTITY")) {
                                    quantity_avg = quantity_avg + feedBack.getStars();
                                    count_quantity = count_quantity + 1;
                                } else if (feedBack.getType().equals("PRICE")) {
                                    price_avg = price_avg + feedBack.getStars();
                                    count_price = count_price + 1;
                                } else if (feedBack.getType().equals("QUALITY")) {
                                    quality_avg = quality_avg + feedBack.getStars();
                                    count_quality = count_quality + 1;
                                }
                            }
                            price.setRating(price_avg / count_price);
                            quality.setRating(quality_avg / count_quality);
                            quantity.setRating(quantity_avg / count_quantity);
                            price_reviewcount.setText("Price:  " + count_price);
                            quantity_reviewcount.setText("Quantity: " + count_quantity);
                            quality_reviewcount.setText("Quality: " + count_quality);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            message.setText("No Reviews");
                            if (feedBackList.size() > 0) {
                                message.setVisibility(View.GONE);
                            } else {
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
                        if (feedBackList.size() > 0) {
                            message.setVisibility(View.GONE);
                        } else {
                            message.setVisibility(View.VISIBLE);
                        }
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
                    count = feedBackList.size() - 1;
                    params.put("start", String.valueOf(feedBackList.size()));
                }


                return params;
            }

        };


        //adding our stringrequest to queue

        RequestHandler.getInstance(VendorReviewList.this).addToRequestQueue(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_review_menu, menu);
        MenuItem writeReview = menu.findItem(R.id.add_review);
        if (roleTytpe.equals("seller")) {
            writeReview.setVisible(false);
        }
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

    public void getResponseTime(final int vendor_id) {
        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.POST, Constants.VENDOR_RESPONSE_TIME,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error") == false) {
                                responseTime.setText("Delivery Time: " + jsonObject.getString("message") + "Minutes");
                            } else {
                                responseTime.setText(jsonObject.getString("message"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            responseTime.setText(e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseTime.setText("Error while loading response time");
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
        RequestHandler.getInstance(VendorReviewList.this).addToRequestQueue(stringRequest);
    }

}
