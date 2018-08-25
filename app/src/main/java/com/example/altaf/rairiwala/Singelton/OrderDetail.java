package com.example.altaf.rairiwala.Singelton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.CustomerManagment.PlaceOrder;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.Models.ProductDetails;
import com.example.altaf.rairiwala.DeliverPersonManagement.OrderDetailGoogleMap;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerNewOrderList;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerOrderItemsAdapter;
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

public class OrderDetail extends AppCompatActivity {

    List<Product> productList;
    Button confirm;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        try {
            confirm = findViewById(R.id.order);
            progressBar = findViewById(R.id.progressBar);
            recyclerView = findViewById(R.id.order_details);
            recyclerView.setHasFixedSize(true);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            productList = new ArrayList<>();
            Bundle bundle = getIntent().getExtras();
            final String jsonString = bundle.getString("order");
            final String rule = bundle.getString("rule");
            final Order order;
            Gson gson = new Gson();
            Type listOfproductType = new TypeToken<Order>() {
            }.getType();
            order = gson.fromJson(jsonString, listOfproductType);
            if (order.getOrder_status().equals("Confirmed") && (rule.equals("seller"))) {
                confirm.setVisibility(View.GONE);
            } else if ((order.getOrder_status().equals("ASSIGNED") || order.getOrder_status().equals("NEW") || order.getOrder_status().equals("Confirmed")) && (rule.equals("seller") || rule.equals("dp"))) {


                confirm.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(OrderDetail.this, OrderDetailGoogleMap.class);
                        intent.putExtra("order", jsonString);
                        startActivity(intent);
                    }
                });


            }
            if (rule.equals("customer_hide")) {
                confirm.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            }
            if (order.getProductArrayList() == null) {
                loadOrderItems(order.getVendor_id(), order.getOrder_id());
            }
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(OrderDetail.this);
                    dialogBuilder
                            .withTitle("Confirm Order")                                  //.withTitle(null)  no title
                            .withTitleColor("#FFFFFF")                                  //def
                            .withDividerColor("#11000000")                                 //def
                            .withMessage("Do you want to confirm order?")                     //.withMessage(null)  no Msg
                            .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                            .withDialogColor("#4CAF50")                                //def  | withDialogColor(int resid)
                            .withIcon(getResources().getDrawable(R.drawable.confirm))
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
                                    Gson gson = new Gson();
                                    String orderItems = gson.toJson(productList);
                                    confirmOrder(order.getOrder_id(), SharedPrefManager.getInstance(OrderDetail.this).getSeller().getVendor_id(), orderItems, order.getCustomer_id());

                                    dialogBuilder.dismiss();
                                }
                            })
                            .show();


                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void confirmOrder(final int order_id, final int vendor_id, final String orderItems, final int customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.CONFIRM_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false) {
                                Toast.makeText(OrderDetail.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(OrderDetail.this, SellerNewOrderList.class));
                                OrderDetail.this.finish();
                            } else {
                                Toast.makeText(OrderDetail.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                confirm.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            confirm.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "There was some error.Please try again....", Toast.LENGTH_LONG).show();
                        confirm.setVisibility(View.GONE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", String.valueOf(vendor_id));
                params.put("customer_id", String.valueOf(customer_id));
                params.put("orderitems", String.valueOf(orderItems));
                params.put("order_id", String.valueOf(order_id));
                return params;
            }
        };
        RequestHandler.getInstance(OrderDetail.this).addToRequestQueue(stringRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadOrderItems(final int vendor_id, final int order_id) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SELLER_ORDER_ITEMS,
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
                                Product pro = new Product();
                                ProductDetails productDetails = new ProductDetails();
                                productDetails.setPrice(product.getInt("price"));
                                productDetails.setQuantity(product.getInt("quantity"));
                                productDetails.setVendor_id(product.getInt("vendor_id"));
                                pro.setProductDetails(productDetails);
                                pro.setProduct_name(product.getString("product_name"));
                                pro.setProduct_image(product.getString("image"));
                                pro.setProduct_id(product.getInt("productid"));
                                pro.setProduct_type(product.getString("quantity_type"));
                                productList.add(pro);

                            }


                            SellerOrderItemsAdapter adapter = new SellerOrderItemsAdapter(OrderDetail.this, productList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            confirm.setVisibility(View.GONE);
                            Toast.makeText(OrderDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        confirm.setVisibility(View.GONE);
                        Toast.makeText(OrderDetail.this, "Error while loading the products", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(order_id));
                params.put("vendor_id", String.valueOf(vendor_id));

                return params;
            }

        };
        ;

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}



