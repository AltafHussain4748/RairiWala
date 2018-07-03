package com.example.altaf.rairiwala.AccountManagment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdatePassword extends AppCompatActivity {
    EditText pin, confirmpin;
    Button update;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pin = findViewById(R.id.pin);
        confirmpin = findViewById(R.id.confirmpin);
        update = findViewById(R.id.updatePin);
        Bundle bundle = getIntent().getExtras();
        phoneNumber = bundle.getString("PHONE");
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pin.getText().toString().equals(confirmpin.getText().toString()) && pin.getText().toString() != null && confirmpin.getText().toString() != null) {
                    resetPassword(pin.getText().toString(), phoneNumber);
                } else {
                    Toast.makeText(UpdatePassword.this, "Pin do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void resetPassword(final String pin1, final String phone1) {
        Toast.makeText(this, "Updating pin......", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.RESETPASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false) {
                                startActivity(new Intent(UpdatePassword.this, UserLogin.class));
                                UpdatePassword.this.finish();

                            } else {
                                Toast.makeText(UpdatePassword.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("pin", pin1);
                params.put("phone", phone1);
                return params;
            }
        };
        RequestHandler.getInstance(UpdatePassword.this).addToRequestQueue(stringRequest);
    }
}
