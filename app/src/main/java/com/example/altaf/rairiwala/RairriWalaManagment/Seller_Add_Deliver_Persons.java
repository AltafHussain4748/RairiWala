package com.example.altaf.rairiwala.RairriWalaManagment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Seller_Add_Deliver_Persons extends AppCompatActivity {
    EditText phone_number, pin, name;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_add_deliver_persons);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        phone_number = findViewById(R.id.dp_sign_up_phone_number);
        btn = findViewById(R.id.dp_signup_btn);
        pin = findViewById(R.id.dp_sign_up_pin);
        name = findViewById(R.id.dp_sign_up_name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone_number.getText().toString().length() == 10 && pin.getText().length() >= 4 && name.getText().length() > 5) {
                    Toast.makeText(Seller_Add_Deliver_Persons.this, "Registring......", Toast.LENGTH_LONG).show();
                    //String request start
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Constants.ADD_DELIVER_PERSON,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getBoolean("error") == false) {
                                            Toast.makeText(Seller_Add_Deliver_Persons.this, jsonObject.getString("message") + "", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(Seller_Add_Deliver_Persons.this, jsonObject.getString("message") + "", Toast.LENGTH_LONG).show();

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(getApplicationContext(), "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("phone", "+92" + phone_number.getText().toString());
                            params.put("pin", pin.getText().toString());
                            params.put("name", name.getText().toString());
                            params.put("rule", "DP");
                            params.put("vendor_id", String.valueOf(SharedPrefManager.getInstance(Seller_Add_Deliver_Persons.this).getSeller().getVendor_id()));

                            return params;
                        }
                    };
                    RequestHandler.getInstance(Seller_Add_Deliver_Persons.this).addToRequestQueue(stringRequest);
                    //end of string request


                } else {
                    Toast.makeText(Seller_Add_Deliver_Persons.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
