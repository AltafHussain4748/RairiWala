package com.example.altaf.rairiwala.CustomerManagment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceOrder extends AppCompatActivity implements OnMapReadyCallback {
    List<Product> products;
    private GoogleMap mMap;
    double latitude = 0.0;
    double longtude = 0.0;
    LocationManager locationManager;
    public FusedLocationProviderClient mFusedLocationClient;
    EditText house_number, street_name;
    Button placeorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.altaf.rairiwala.R.layout.customer_place_order);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Bundle bundle = getIntent().getExtras();
        final String jsonString = bundle.getString("PRODUCT_LIST");
        Gson gson = new Gson();
        Type listOfproductType = new TypeToken<List<Product>>() {
        }.getType();
        products = gson.fromJson(jsonString, listOfproductType);
        house_number = findViewById(R.id.house_name);
        street_name = findViewById(R.id.street_name);
        placeorder = findViewById(R.id.placeorder);
        placeorder.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String street = street_name.getText().toString();
                String house_nu = house_number.getText().toString();
                if (street.length() >= 3 && house_nu.length() >= 3) {
                    if (latitude != 0.0 && longtude != 0.0) {
                     /*   Toast.makeText(PlaceOrder.this, "" + products.size() + "\n" + street + "\n" + house_nu + "\n" +
                                SharedPrefManager.getInstance(PlaceOrder.this).getCustomer().getCustomer_id() + "\n" + latitude + "\n" + longtude, Toast.LENGTH_SHORT).show();*/
                        Gson gson = new Gson();
                        Order order = new Order();
                        order.setProductArrayList(products);
                        String product = gson.toJson(products);
                        CustomerAddress customerAddress = new CustomerAddress();
                        customerAddress.setHouseName(house_nu);
                        customerAddress.setStreetName(street);
                        customerAddress.setLatiitude(latitude);
                        customerAddress.setLongitude(longtude);
                        customerAddress.setName(SharedPrefManager.getInstance(PlaceOrder.this).getCustomer().getName());
                        order.setCustomerAddress(customerAddress);
                        order.setCustomer_id(SharedPrefManager.getInstance(PlaceOrder.this).getCustomer().getCustomer_id());
                        order.setVendor_id(products.get(0).getProductDetails().getVendor_id());
                        order.setOrder_status("NEW");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        order.setOrder_time(dateFormat.format(date));
                        final String jsonString = gson.toJson(order);
                        //DIALOGUE
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrder.this);
                        builder.setTitle("Place Order");  // GPS not found
                        builder.setMessage("Want to send Order?"); // Want to enable?
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                              /*  Intent intent=new Intent(PlaceOrder.this, OrderDetail.class);
                                intent.putExtra("order",jsonString);
                                startActivity(intent);*/
                                sendOrder(jsonString, products.get(0).getProductDetails().getVendor_id());
                            }
                        });
                        builder.setNegativeButton("No", null);
                        builder.create().show();

                        //   END OF DIALOGUE


                        // Toast.makeText(PlaceOrder.this, "" + SharedPrefManagerFirebase.getInstance(PlaceOrder.this).getToken(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlaceOrder.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PlaceOrder.this, "Required fields are missing", Toast.LENGTH_SHORT).show();
                }


            }
        });

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

        //marker grable
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Toast.makeText(AddSellerExtraInformation.this, "Latitude:" + marker.getPosition().latitude + "\n" + "Longitude:" + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();
                latitude = marker.getPosition().latitude;
                longtude = marker.getPosition().latitude;
            }
        });
        // Add a marker in Sydney and move the camera
        //start getting latitude
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //start getting user current location by fused location
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ask for permission

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longtude = location.getLongitude();
                                    LatLng sydney = new LatLng(latitude, longtude);
                                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in meri locationy").draggable(true));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
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

        //end of lat long
        //end of lat long

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_cometo_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        } else if (d == R.id.home_return) {
            startActivity(new Intent(PlaceOrder.this, CustomerHomePage.class));
        }
        return super.onOptionsItemSelected(item);
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
                                    LatLng sydney = new LatLng(latitude, longtude);
                                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in meri locationy").draggable(true));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
                                }
                            }
                        });

        } else {
            Toast.makeText(PlaceOrder.this, "Not granted", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendOrder(final String order, final int vendor_id) {
        if (order != null && vendor_id != 0) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.PlaceOrder,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.getBoolean("error") == false) {

                                    Toast.makeText(PlaceOrder.this, "Order Sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PlaceOrder.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlaceOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    params.put("order", order);
                    params.put("vendor_id", String.valueOf(vendor_id));
                    return params;
                }
            };
            //adding our stringrequest to queue
            Volley.newRequestQueue(this).add(stringRequest);
        }
    }

}
