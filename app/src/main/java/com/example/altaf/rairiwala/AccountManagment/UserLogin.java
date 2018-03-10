package com.example.altaf.rairiwala.AccountManagment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.CustomerManagment.CustomerHomePage;
import com.example.altaf.rairiwala.Models.Customer;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerHomePage;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserLogin extends AppCompatActivity {
    EditText phone, pin;
    TextView notAccount;
    Button login;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_login);
        phone = findViewById(R.id.login_phone_number);
        pin = findViewById(R.id.login_up_pin);
        notAccount = findViewById(R.id.donot_have_account);
        login = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
        notAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLogin.this, AppStartUpPage.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pin.getText().length() >= 4 && phone.getText().length() == 10) {
                    loginUser(phone.getText().toString(), pin.getText().toString());
                } else {
                    Toast.makeText(UserLogin.this, "Please fill the fields first", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void loginUser(final String phone, final String pin) {
        progressDialog.setMessage("Loggin In...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //Volley request code
        //Request Code
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.getBoolean("error") == false) {
                                //save vendor to sharedpref manager
                                if (obj.getString("Account_Rule").equals("CUSTOMER")) {

                                    Customer customer = new Customer();
                                    customer.setCustomer_id(Integer.parseInt(obj.getString("customer_id")));
                                    customer.setPin(obj.getString("Person_Password"));
                                    customer.setPerson_phone_number(obj.getString("Person_Phone_Number"));
                                    customer.setName(obj.getString("Person_Name"));
                                    customer.setRule(obj.getString("Account_Rule"));
                                    customer.setStatus(obj.getString("Account_Status"));
                                    customer.setPerson_id(Integer.parseInt(obj.getString("id")));
                                    if (SharedPrefManager.getInstance(UserLogin.this).addCustomerToPref(customer)) {
                                        startActivity(new Intent(UserLogin.this, CustomerHomePage.class));
                                        UserLogin.this.finish();
                                    } else {
                                        Toast.makeText(UserLogin.this, "Some Error", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (obj.getString("Account_Rule").equals("SELLER")) {
                                    //save selller to sharedpref manager
                                    Vendor vendor = new Vendor();
                                    vendor.setPin(obj.getString("Person_Password"));
                                    vendor.setPerson_phone_number(obj.getString("Person_Phone_Number"));
                                    vendor.setName(obj.getString("Person_Name"));
                                    vendor.setRule(obj.getString("Account_Rule"));
                                    vendor.setStatus(obj.getString("Account_Status"));
                                    vendor.setPerson_id(Integer.parseInt(obj.getString("id")));
                                    SharedPrefManager.getInstance(UserLogin.this).addSellerToPref(vendor);
                                    addSellerInfo(vendor.getPerson_id(), vendor);
                                    startActivity(new Intent(UserLogin.this, SellerHomePage.class));
                                    UserLogin.this.finish();
                                }

                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        obj.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //Start of seller info
                    private void addSellerInfo(final int id, final Vendor vendor) {
                        if (id == -1) {
                            Toast.makeText(UserLogin.this, "No Id fOUND", Toast.LENGTH_SHORT).show();
                        } else {
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.POST,
                                    Constants.URL_GetSellerInfo,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response1) {

                                            try {

                                                JSONObject vendor1 = new JSONObject(response1);


                                                if (vendor1.getString("message").equals("FOUND")) {

                                                    vendor.setShop_name(vendor1.getString("shopname"));
                                                    vendor.setLatitude(Double.parseDouble(vendor1.getString("latitude")));
                                                    vendor.setLongitude(Double.parseDouble(vendor1.getString("longitude")));
                                                    vendor.setVendor_id(Integer.parseInt(vendor1.getString("vendor_id")));
                                                    SharedPrefManager.getInstance(UserLogin.this).addSellerToPref(vendor);

                                                } else if (vendor1.getString("message").equals("NORECORD")) {
                                                    vendor.setShop_name("");
                                                    vendor.setLatitude(0.0);
                                                    vendor.setLongitude(0.0);
                                                    vendor.setVendor_id(-1);
                                                    SharedPrefManager.getInstance(UserLogin.this).addSellerToPref(vendor);
                                                }
                                                Toast.makeText(UserLogin.this, "Added", Toast.LENGTH_SHORT).show();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(UserLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(UserLogin.this, "error", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("personId", String.valueOf(id));
                                    return params;
                                }

                            };
                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));
                            RequestHandler.getInstance(UserLogin.this).addToRequestQueue(stringRequest);
                        }


                    }
                    //end of get seller info
                    //end of seller info
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", "+92" + phone);
                params.put("password", pin);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        //end of volley request code
    }
}
