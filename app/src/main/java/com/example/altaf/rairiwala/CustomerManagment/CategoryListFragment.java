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
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.Category;
import com.example.altaf.rairiwala.Models.Notifications;
import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import android.app.Fragment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AltafHussain on 3/5/2018.
 */

public class CategoryListFragment
        extends Fragment {
    View view;
    List<Category> category_List;
    List<Category> sqliteDb;
    ProgressDialog progressDialog;
    GridView androidListView;
    TextView message;
    TextView txtViewCount;
    DatabaseHandling databaseHandling;
    List<Notifications> notificationsList;
    String type;
    List<Vendor> vendorList;
    RecyclerView recyclerView;
    double latitude = 0.0;
    double longtude = 0.0;
    LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.category_list_fragment, container, false);
        message = view.findViewById(R.id.error_message);
        androidListView = view.findViewById(R.id.grid_view_image_text);
        // androidGridView.setAdapter(adapterViewAndroid);
        final String user = getArguments().getString("VALUE");
        progressDialog = new ProgressDialog(getActivity());
        category_List = new ArrayList<>();
        sqliteDb = new ArrayList<>();
        databaseHandling = new DatabaseHandling(getActivity());
        sqliteDb = databaseHandling.getAllCategories();
        notificationsList = new ArrayList<>();
        vendorList=new ArrayList<>();
        if (sqliteDb.size() > 0) {
            CategoryListView adapter = new CategoryListView(getActivity(), (ArrayList<Category>) sqliteDb);
            androidListView.setAdapter(adapter);
            loadCategories();
        } else {
            loadCategories();
        }
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Category category;
                if (sqliteDb.size() > 0) {
                    category = sqliteDb.get(i);
                } else {
                    category = category_List.get(i);
                }
                if (user.equals("getNearstVendors")) {
                   type=category.getCategroy_name().toString();
                   loadVendors();
                } else if(user.equals("customerSide")) {
                    Intent intent = new Intent(getActivity(), NearestVendor.class);
                    intent.putExtra("CAT", category.getCategroy_name());
                    startActivity(intent);
                }


            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //start getting user current location by fused location
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ask for permission

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longtude = location.getLongitude();
                                   // loadVendors();
                                }
                            }
                        }).addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("GPS NOT FOUND");  // GPS not found
            builder.setMessage("Want to enable"); // Want to enable?
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
        }

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
    }

    public void loadCategories() {
        progressDialog.setMessage("Loading Categories...");
        if (sqliteDb.size() > 0) {
            progressDialog.dismiss();
        } else {
            progressDialog.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETCATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();

                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            category_List.clear();
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                Category category = new Category();
                                //adding the product to product list
                                category.setCategroy_id(product.getInt("category_id"));
                                category.setCategroy_name(product.getString("category_name"));
                                category.setCategroy_image(product.getString("category_image"));
                                category_List.add(category);
                            }
                            //creating adapter object and setting it to recyclerview
                            if (sqliteDb.size() == 0) {
                                CategoryListView adapter = new CategoryListView(getActivity(), category_List);
                                androidListView.setAdapter(adapter);
                                databaseHandling.deleteAllCategories();
                                for (Category category : category_List) {
                                    databaseHandling.insertCategories(category);
                                }
                            } else if (category_List.size() != sqliteDb.size()) {
                                databaseHandling.deleteAllCategories();
                                for (Category category : category_List) {
                                    databaseHandling.insertCategories(category);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (sqliteDb.size() == 0) {

                                message.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "No Category", Toast.LENGTH_SHORT).show();
                                message.setText("No Category");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (sqliteDb.size() <= 0) {
                            progressDialog.dismiss();
                            message.setVisibility(View.VISIBLE);
                            message.setText("Error while loading the categories");
                        }

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longtude = location.getLongitude();
                                //    loadVendors();
                                } else {
                                    Toast.makeText(getActivity(), "Problem while getting location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        } else {
            Toast.makeText(getActivity(), "Permission Not granted", Toast.LENGTH_SHORT).show();

        }
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
                                        Toast.makeText(getActivity(), " No Sellers", Toast.LENGTH_SHORT).show();

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

                                        Intent intent = new Intent(getActivity(), NearestVendorsMap.class);
                                        Gson gson = new Gson();
                                        String vendors = gson.toJson(vendorList);
                                        intent.putExtra("vendorList", vendors);
                                        startActivity(intent);
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
                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Volley.newRequestQueue(getActivity()).add(stringRequest);
            } else {
                Toast.makeText(getActivity(), "Some Error", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "Turn on Gps", Toast.LENGTH_SHORT).show();
        }
    }


}
