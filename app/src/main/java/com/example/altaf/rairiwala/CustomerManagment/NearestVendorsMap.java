package com.example.altaf.rairiwala.CustomerManagment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class NearestVendorsMap extends FragmentActivity implements OnMapReadyCallback {
    List<Vendor> vendorList;
    private GoogleMap mMap;
    LocationManager locationManager;
    public FusedLocationProviderClient mFusedLocationClient;
    String cat;
    double latitude = 0.0;
    double longtude = 0.0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_nearest_vendors_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        vendorList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        Bundle bundle = getIntent().getExtras();
        final String jsonString = bundle.getString("vendorList");
        cat = bundle.getString("Category");
        Gson gson = new Gson();
        Type listOfproductType = new TypeToken<List<Vendor>>() {
        }.getType();
        vendorList = gson.fromJson(jsonString, listOfproductType);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        //check marker dragable or not
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Toast.makeText(PlaceOrder.this, "Latitude:" + marker.getPosition().latitude + "\n" + "Longitude:" + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();
                latitude = marker.getPosition().latitude;
                longtude = marker.getPosition().longitude;
                loadVendors();
            }
        });
        try {
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

                                        try {
                                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            mMap.addMarker(new MarkerOptions().position(latLng).title("MyLocation").title("My Location").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).rotation((float) 90.0));
                                            for (Vendor vendor : vendorList) {
                                                mMap.addMarker(new MarkerOptions().position(new LatLng(vendor.getLatitude(), vendor.getLongitude())).title(vendor.getName()));
                                            }

                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                        } catch (Exception e) {
                                            Toast.makeText(NearestVendorsMap.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(NearestVendorsMap.this, "Error while getting location please try again", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            Toast.makeText(NearestVendorsMap.this, "Location permission Not granted", Toast.LENGTH_SHORT).show();
        }


    }

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
                                    try {
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title("MyLocation").title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).rotation((float) 90.0));
                                        for (Vendor vendor : vendorList) {
                                            mMap.addMarker(new MarkerOptions().position(new LatLng(vendor.getLatitude(), vendor.getLongitude())).title(vendor.getName()));
                                        }

                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                    } catch (Exception e) {
                                        Toast.makeText(NearestVendorsMap.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(NearestVendorsMap.this, "Error while getting location please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

        } else {
            Toast.makeText(NearestVendorsMap.this, "Not granted", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void loadVendors() {
        if (latitude != 0.0 && longtude != 0.0) {
            if (cat != null) {
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
                                    List<Vendor> vendorList1 = new ArrayList<>();
                                    progressDialog.dismiss();
                                    JSONArray array = new JSONArray(response);

                                    if (array.length() == 0) {
                                        Toast.makeText(NearestVendorsMap.this, " No Sellers", Toast.LENGTH_SHORT).show();

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
                                                vendorList1.add(v);
                                            }

                                        }
                                        try {
                                            Toast.makeText(NearestVendorsMap.this, "Nears Vendors are " + vendorList1.size(), Toast.LENGTH_SHORT).show();

                                            mMap.clear();

                                            LatLng latLng = new LatLng(latitude, longtude);
                                            mMap.addMarker(new MarkerOptions().position(latLng).title("MyLocation").title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).rotation((float) 90.0).draggable(true));
                                            if(vendorList1.size()>0){
                                                for (Vendor vendor : vendorList1) {
                                                    mMap.addMarker(new MarkerOptions().position(new LatLng(vendor.getLatitude(), vendor.getLongitude())).title(vendor.getName()));
                                                }
                                            }else{
                                                Toast.makeText(NearestVendorsMap.this, "No nearby sellers", Toast.LENGTH_SHORT).show();
                                            }
                                            LatLng latLng1 = new LatLng(latitude, longtude);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));
                                        } catch (Exception e) {
                                            Toast.makeText(NearestVendorsMap.this, "" + e.getMessage()+"Error please try...", Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(NearestVendorsMap.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();

                                Toast.makeText(NearestVendorsMap.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })

                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put("category", cat);
                        params.put("latitude", String.valueOf(latitude));
                        params.put("longitude", String.valueOf(longtude));
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                10*000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );
                //adding our stringrequest to queue
                Volley.newRequestQueue(NearestVendorsMap.this).add(stringRequest);
            } else {
                Toast.makeText(NearestVendorsMap.this, "Some Error", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(NearestVendorsMap.this, "Turn on Gps", Toast.LENGTH_SHORT).show();
        }
    }


}
