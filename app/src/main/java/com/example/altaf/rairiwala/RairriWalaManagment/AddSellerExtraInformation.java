package com.example.altaf.rairiwala.RairriWalaManagment;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddSellerExtraInformation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    double latitude = 0.0;
    double longitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    Button addLocation;
    EditText shopname;
    Vendor vendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.altaf.rairiwala.R.layout.seller_add_extra_information);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addLocation = findViewById(R.id.add_extra_information);
        shopname = findViewById(R.id.shop_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("Add Location");

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //marker drag listener
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                latitude = marker.getPosition().latitude;
                longitude = marker.getPosition().longitude;
                Toast.makeText(AddSellerExtraInformation.this, "Latitude:" + marker.getPosition().latitude + "\n" + "Longitude:" + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();

            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        vendor = SharedPrefManager.getInstance(this).getSeller();
        if (vendor.getLatitude() != 0.0 && vendor.getLongitude() != 0.0) {
            latitude = vendor.getLatitude();
            longitude = vendor.getLongitude();
            LatLng userCurrentLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(userCurrentLocation).title("My Location").draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, 17));
        } else {
            //check wether the gps is on or not
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
            } else {
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
                                        //  Toast.makeText(AddSellerExtraInformation.this, "Latitude:" + location.getLatitude() + "\n" + "Longitude" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                                        LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(userCurrentLocation).title("My Location").draggable(true));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, 17));
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();

                                    }
                                }
                            });
                }
                //end of lat long
                //end of getting user current location

                // Add a marker in Sydney and move the camera

            }
            //check wether the gps is on or not

        }
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude != 0.0 && longitude != 0.0) {

                    addLocations(vendor.getPerson_id(), latitude, longitude);
                }

            }
        });

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
                                    Toast.makeText(AddSellerExtraInformation.this, "Latitude:" + location.getLatitude() + "\n" + "Longitude" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                                    LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(userCurrentLocation).title("My Location").draggable(true));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, 18));
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        });

        } else {
            Toast.makeText(AddSellerExtraInformation.this, "Not granted", Toast.LENGTH_SHORT).show();
        }

    }

    public void addLocations(final int person_id, final double lat, final double longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            //Toast.makeText(AddSellerExtraInformation.this, "" + shopname.getText() + lat.latitude + "  \n" + lat.longitude, Toast.LENGTH_SHORT).show();
            //Volley request code
            //Request Code
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    Constants.URL_SELLER_ADD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);

                                if (obj.getBoolean("error") == false) {

                                    vendor.setLatitude(latitude);
                                    vendor.setLongitude(longitude);
                                    vendor.setShop_name(shopname.getText().toString());
                                    vendor.setVendor_id(obj.getInt("vendorid"));
                                    vendor.setShop_status(obj.getString("shop_status"));
                                    if (SharedPrefManager.getInstance(AddSellerExtraInformation.this).addSellerToPref(vendor)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                obj.getString("message"),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Some Error",
                                                Toast.LENGTH_LONG
                                        ).show();
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
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
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
                    params.put("shopname", shopname.getText().toString());
                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longitude));
                    params.put("personid", String.valueOf(person_id));
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 10, 1.0f));
            RequestHandler.getInstance(AddSellerExtraInformation.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(this, "Location not added", Toast.LENGTH_SHORT).show();
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

}
