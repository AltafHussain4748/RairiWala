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
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;


public class SellerAssignDeliverPerson extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<DeliveryPerson> deliveryPersonList;
    TextView message;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_assign_deliver_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        final String jsonString = bundle.getString("order");
        final Order order;
        Gson gson = new Gson();
        Type listOfproductType = new TypeToken<Order>() {
        }.getType();
        order = gson.fromJson(jsonString, listOfproductType);
        message = findViewById(R.id.error_message);
        getSupportActionBar().setTitle("Assign");
        recyclerView = findViewById(R.id.assign_dp);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SellerAssignDeliverPerson.this));
        deliveryPersonList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);

        loadDeliverPersons(SharedPrefManager.getInstance(SellerAssignDeliverPerson.this).getSeller().getVendor_id());
        //recyclerView Item click listener
        //inner class
        class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

            private ClickListener clicklistener;
            private GestureDetector gestureDetector;

            public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

                this.clicklistener = clicklistener;
                gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && clicklistener != null) {
                            clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                        }
                    }
                });
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                    clicklistener.onClick(child, rv.getChildAdapterPosition(child));
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        }
        //end of inner class click listener
        //start recycler view click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                final DeliveryPerson deliveryPerson = deliveryPersonList.get(0);
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(SellerAssignDeliverPerson.this);
                dialogBuilder
                        .withTitle("Assign Dp")                                  //.withTitle(null)  no title
                        .withTitleColor("#FFFFFF")                                  //def
                        .withDividerColor("#11000000")                              //def
                        .withMessage("Do you want to assign it?")                     //.withMessage(null)  no Msg
                        .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                        .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
                        .withIcon(getResources().getDrawable(R.drawable.user))
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
                                assignDeliveryPerson(deliveryPerson.getDelivery_person_id(), order.getOrder_id());
                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
            }
        }));
        //end of recycler view item click listener
        //end of recycler view item click listener
    }

    public void loadDeliverPersons(final int vendor_id) {
        if (vendor_id >= 0) {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHECK_DELIVERY_PERSON_FREE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                //converting the string to json array object
                                JSONArray array = new JSONArray(response);
                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject dp = array.getJSONObject(i);
                                    DeliveryPerson deliveryPerson = new DeliveryPerson();
                                    deliveryPerson.setName(dp.getString("Person_Name"));
                                    deliveryPerson.setPerson_id(dp.getInt("Person_Id"));
                                    deliveryPerson.setDelivery_person_id(dp.getInt("Delivery_PId"));
                                    deliveryPerson.setPin(dp.getString("Person_Password"));
                                    deliveryPerson.setRule(dp.getString("Rule"));
                                    deliveryPerson.setStatus(dp.getString("Account_Status"));
                                    deliveryPerson.setPerson_phone_number(dp.getString("Person_Phone_Number"));
                                    deliveryPerson.setIsFree(dp.getString("isfree"));
                                    deliveryPersonList.add(deliveryPerson);

                                }

                                AssignDeliveryPersonAdapter newOrderAdapter = new AssignDeliveryPersonAdapter(SellerAssignDeliverPerson.this, deliveryPersonList);
                                recyclerView.setAdapter(newOrderAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                message.setVisibility(View.VISIBLE);
                                message.setText("No Delivery Person free right now");
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            message.setVisibility(View.VISIBLE);
                            message.setText("Some error");


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
            Volley.newRequestQueue(SellerAssignDeliverPerson.this).add(stringRequest);
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

    //Interface
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    public void assignDeliveryPerson(final int dp_id, final int order_id) {
        Toast.makeText(this, "Seinding....", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.NotifyDeliveryPerson,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            //converting the string to json array object
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error") == false) {
                                Toast.makeText(SellerAssignDeliverPerson.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(SellerAssignDeliverPerson.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SellerAssignDeliverPerson.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SellerAssignDeliverPerson.this, "Internet Error", Toast.LENGTH_SHORT).show();


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("delivery_person_id", String.valueOf(dp_id));
                params.put("order_id", String.valueOf(order_id));

                return params;
            }

        };


        //adding our stringrequest to queue
        Volley.newRequestQueue(SellerAssignDeliverPerson.this).add(stringRequest);
    }

}
