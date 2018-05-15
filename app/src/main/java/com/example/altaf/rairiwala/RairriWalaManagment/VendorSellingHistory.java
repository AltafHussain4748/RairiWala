package com.example.altaf.rairiwala.RairriWalaManagment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.DatePicker;
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
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class VendorSellingHistory extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView message;
    List<History> productList;
    boolean isScrolling = false;
    int currentItems, totalItems, scrollItems;
    LinearLayoutManager linearLayoutManager;
    int count = 0;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_selling_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("Sellings");
        recyclerView = findViewById(R.id.sellinghistory);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        productList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        message = findViewById(R.id.error_message);
        loadSellerHistory(SharedPrefManager.getInstance(VendorSellingHistory.this).getSeller().getVendor_id());
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
                    loadSellerHistory(SharedPrefManager.getInstance(VendorSellingHistory.this).getSeller().getVendor_id());
                }
            }
        });

    }

    private void loadSellerHistory(final int vendor_id) {
        progressBar.setVisibility(View.VISIBLE);
        /*
        * Creating a String Request
        * The request type is GET defined by first parameter
        * The URL is defined in the second parameter
        * Then we have a Response Listener and a Error Listener
        * In response listener we will get the JSON response as a String
        * */
        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.POST, Constants.VENDORSELLINGHISTORY,
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
                                productList.add(history);

                            }
                            VendorSellingHistoryAdapter adapter = new VendorSellingHistoryAdapter(VendorSellingHistory.this, productList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.scrollToPosition(count);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            message.setText(e.getMessage());

                            if (productList.size() > 0) {
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
                        message.setText("Error while loading the History");
                        progressBar.setVisibility(View.GONE);
                        if (productList.size() > 0) {
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
        Volley.newRequestQueue(VendorSellingHistory.this).add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dailyincome, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        } else if (d == R.id.dailyincome) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            loadDailyIncome(year + "-" + (monthOfYear+1) + "-" + dayOfMonth, SharedPrefManager.getInstance(VendorSellingHistory.this).getSeller().getVendor_id());
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDailyIncome(final String date, final int vendor_id) {
        StringRequest stringRequest;
        Toast.makeText(this, "Getting daily income", Toast.LENGTH_SHORT).show();
        stringRequest = new StringRequest(Request.Method.POST, Constants.VENDOR_DAILY_INCOME,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error") == false) {
                                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(VendorSellingHistory.this);
                                dialogBuilder
                                        .withTitle("Daily Income")                                  //.withTitle(null)  no title
                                        .withTitleColor("#FFFFFF")                                  //def
                                        .withDividerColor("#11000000")                              //def
                                        .withMessage("Daily Income Rs:"+jsonObject.getString("message"))                     //.withMessage(null)  no Msg
                                        .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                                        .withDialogColor("#4CAF50")                               //def  | withDialogColor(int resid)
                                        .withIcon(getResources().getDrawable(R.drawable.dailyincome))
                                        .withDuration(700)                                          //def
                                        .withEffect(RotateBottom)                                         //def Effectstype.Slidetop
                                        .withButton1Text("Cancel")                                      //def gone
                                        //def gone
                                        .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                                        //.setCustomView(View or ResId,context)
                                        .setButton1Click(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogBuilder.dismiss();
                                            }
                                        }).show();


                            }else{
                                Toast.makeText(VendorSellingHistory.this, jsonObject.getString("message")+"", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(VendorSellingHistory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VendorSellingHistory.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", String.valueOf(vendor_id));
                params.put("datetime", date);


                return params;
            }

        };


        //adding our stringrequest to queue
        Volley.newRequestQueue(VendorSellingHistory.this).add(stringRequest);
    }
}

