package com.example.altaf.rairiwala.CustomerManagment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NearestVendorsMap extends FragmentActivity implements OnMapReadyCallback {
    List<Vendor> vendorList;
    private GoogleMap mMap;
    LocationManager locationManager;
    public FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_nearest_vendors_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        vendorList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        final String jsonString = bundle.getString("vendorList");
        Gson gson = new Gson();
        Type listOfproductType = new TypeToken<List<Vendor>>() {
        }.getType();
        vendorList = gson.fromJson(jsonString, listOfproductType);

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
                                            mMap.addMarker(new MarkerOptions().position(latLng).title("MyLocation").icon(BitmapDescriptorFactory.fromResource(R.drawable.name)));
                                            for (Vendor vendor : vendorList) {
                                                mMap.addMarker(new MarkerOptions().position(new LatLng(vendor.getLatitude(), vendor.getLongitude())).title(vendor.getName()));
                                            }

                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                        } catch (Exception e) {
                                            Toast.makeText(NearestVendorsMap.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

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
            Toast.makeText(NearestVendorsMap.this, "Not granted", Toast.LENGTH_SHORT).show();
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
                                        mMap.addMarker(new MarkerOptions().position(latLng).title("MyLocation"));
                                        for (Vendor vendor : vendorList) {
                                            mMap.addMarker(new MarkerOptions().position(new LatLng(vendor.getLatitude(), vendor.getLongitude())).title(vendor.getName()));
                                        }

                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                    } catch (Exception e) {
                                        Toast.makeText(NearestVendorsMap.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });

        } else {
            Toast.makeText(NearestVendorsMap.this, "Not granted", Toast.LENGTH_SHORT).show();
        }

    }


}
