package com.example.altaf.rairiwala.Singelton;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.CustomerManagment.OrderItems;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AltafHussain on 3/11/2018.
 */

public class SaveToken {
    Context context;

    public SaveToken(Context context) {
        this.context = context;
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
}
