package com.example.altaf.rairiwala.CustomerManagment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearestVendor extends AppCompatActivity {
    String type;
    List<Vendor> vendorList;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    double latitude = 0.0;
    double longtude = 0.0;
    LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.altaf.rairiwala.R.layout.customer_nearest_vendor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        message = findViewById(R.id.error_message);
        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("CAT");
        vendorList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.vendor_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//check if gps enabled
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //start getting user current location by fused location
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ask for permission

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longtude = location.getLongitude();
                                    loadVendors();
                                } else {
                                    Toast.makeText(NearestVendor.this, "Problem while getting location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS NOT FOUND");  // GPS not found
            builder.setMessage("Want to enable"); // Want to enable?
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
            return;
        }

        //floating actiuon button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.viewVendorOnGoogoleMap);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vendorList.size() > 0) {
                    Intent intent = new Intent(NearestVendor.this, NearestVendorsMap.class);
                    Gson gson = new Gson();
                    String vendors = gson.toJson(vendorList);
                    intent.putExtra("vendorList", vendors);
                    startActivity(intent);
                } else {
                    Toast.makeText(NearestVendor.this, "No Vendors", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //end of floating action button

    }

    private void loadVendors() {
        if (latitude != 0.0 && longtude != 0.0) {
            if (type != null) {
                //Toast.makeText(this, "" + latLng.latitude + "\n" + latLng.longitude + "\n" + type, Toast.LENGTH_SHORT).show();
                progressDialog.setMessage("Loading Sellers...");
                // progressDialog.setMessage("Registering user...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                /*
                 * Creating a String Request
                 * The request type is GET defined by first parameter
                 * The URL is defined in the second parameter
                 * Then we have a Response Listener and a Error Listener
                 * In response listener we will get the JSON response as a String
                 * */
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_NEAREST_VENDORS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    progressDialog.dismiss();
                                    JSONArray array = new JSONArray(response);

                                    if (array.length() == 0) {
                                        Toast.makeText(NearestVendor.this, " No Sellers", Toast.LENGTH_SHORT).show();

                                    } else {

                                        //traversing through all the object
                                        for (int i = 0; i < array.length(); i++) {

                                            //getting product object from json array
                                            JSONObject vendor = array.getJSONObject(i);
                                            if (Double.parseDouble(vendor.getString("distance")) < 5.0) {
                                                Vendor v = new Vendor();
                                                v.setVendor_id(Integer.parseInt(vendor.getString("Vendor_Id")));
                                                v.setName(vendor.getString("Name"));
                                                v.setPerson_phone_number(vendor.getString("Phone_Number"));
                                                v.setDistance(Double.parseDouble(vendor.getString("distance")));
                                                v.setLatitude(vendor.getDouble("Latitude"));
                                                v.setLongitude(vendor.getDouble("Longitude"));
                                                vendorList.add(v);
                                            }

                                        }

                                        //creating adapter object and setting it to recyclerview
                                        NearestVendorAdapter adapter = new NearestVendorAdapter(NearestVendor.this, vendorList, type);
                                        recyclerView.setAdapter(adapter);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    message.setVisibility(View.VISIBLE);
                                    message.setText("No Sellers Selling this product");
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                message.setVisibility(View.VISIBLE);
                                message.setText(error.getMessage());
                                Toast.makeText(NearestVendor.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })

                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put("category", type);
                        params.put("latitude", String.valueOf(latitude));
                        params.put("longitude", String.valueOf(longtude));
                        return params;
                    }
                };

                //adding our stringrequest to queue
                Volley.newRequestQueue(this).add(stringRequest);
            } else {
                Toast.makeText(this, "Some Error", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Turn on Gps", Toast.LENGTH_SHORT).show();
        }
    }

    //Request permisiions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longtude = location.getLongitude();
                                    loadVendors();
                                } else {
                                    Toast.makeText(NearestVendor.this, "Problem while getting location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

        } else {
            Toast.makeText(NearestVendor.this, "Not granted", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
