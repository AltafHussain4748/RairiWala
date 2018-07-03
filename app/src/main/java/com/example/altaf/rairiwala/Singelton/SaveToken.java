package com.example.altaf.rairiwala.Singelton;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.AccountManagment.AccountConfirmation;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.Models.ProductDetails;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerHomePage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by AltafHussain on 3/11/2018.
 */

public class SaveToken {
    Context context;
    List<Product> productList;

    public SaveToken(Context context) {
        this.context = context;
        productList = new ArrayList<>();
    }

    public boolean saveToken() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.TokenSaving,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false) {
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                SharedPrefManagerFirebase.getInstance(context).saveUpdatedToken(jsonObject.getString("token")); // Toast.makeText(MyFirebaseInstanceIDService.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Toast.makeText(getApplicationContext(), "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("person_id", String.valueOf(SharedPrefManager.getInstance(context).getPersonId()));
                params.put("token", SharedPrefManagerFirebase.getInstance(context).getToken());
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

        return true;
    }

    public void checkSTock() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.NOTIFYSELLER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {

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
                            boolean isShort = false;
                            for (Product product : productList) {
                                if (product.getProductDetails().getQuantity() <= 10) {
                                    isShort = true;
                                }
                            }
                            if (isShort) {
                                Intent i = new Intent(context, SellerHomePage.class);
                                //   i.putExtra("order", orderString);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                        .setAutoCancel(true)
                                        .setContentTitle("Lack of stock")
                                        .setContentText("Some products have lack of stock")
                                        .setSmallIcon(R.drawable.cartt)
                                        .setContentIntent(pendingIntent)
                                        .setDefaults(Notification.DEFAULT_VIBRATE);

                                NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                                manager.notify(0, builder.build());
                                Toast.makeText(context, "Notification", Toast.LENGTH_SHORT).show();
                            }
                            //  stopService(intent);
                        } catch (JSONException e) {
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", String.valueOf(SharedPrefManager.getInstance(context).getSeller().getVendor_id()));
                return params;
            }

        };
        //adding our stringrequest to queue
        Volley.newRequestQueue(context).add(stringRequest);

    }

    public void userRegister(final String name1, final String pin1, final String rule1, final String phonenumber1) {
        if (name1 != null && pin1 != null && rule1 != null && phonenumber1 != null) {
            ///start string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.REGISTER_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.getBoolean("error") == false) {
                                    context.startActivity(new Intent(context, UserLogin.class));

                                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("phone", phonenumber1);
                    params.put("name", name1);
                    params.put("rule", rule1);
                    params.put("pin", pin1);


                    return params;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
            // end string request
        }

    }
}
