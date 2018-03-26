package com.example.altaf.rairiwala.CustomerManagment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.AccountManagment.CheckInterNet;
import com.example.altaf.rairiwala.AccountManagment.ConnectToInternet;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;
import com.example.altaf.rairiwala.Models.Category;
import com.example.altaf.rairiwala.PerformanceMonitering.Rating_Stars_Activity;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.CategoryListView;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomePage extends AppCompatActivity {
    List<Category> category_List;
    ProgressDialog progressDialog;
    GridView androidListView;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!new CheckInterNet(CustomerHomePage.this).isNetworkAvailable()) {
            startActivity(new Intent(CustomerHomePage.this, ConnectToInternet.class));
            this.finish();
        }
        // get the reference of Button
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        message = findViewById(R.id.error_message);
        androidListView = findViewById(R.id.grid_view_image_text);
        // androidGridView.setAdapter(adapterViewAndroid);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Category category = category_List.get(i);
                Intent intent = new Intent(CustomerHomePage.this, NearestVendor.class);
                intent.putExtra("CAT", category.getCategroy_name());
                startActivity(intent);

            }
        });
        progressDialog = new ProgressDialog(this);
        category_List = new ArrayList<>();
        //end of sqlite databse handler

        FirebaseMessaging.getInstance().subscribeToTopic("rairiwala");
        FirebaseInstanceId.getInstance().getToken();
        loadCategories();


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPrefManager.getInstance(CustomerHomePage.this).logOut();
            startActivity(new Intent(this, UserLogin.class));
            this.finish();
            return true;
        } else if (id == R.id.customer_orders) {
            startActivity(new Intent(CustomerHomePage.this, CustomerOrderList.class));
        } else if (id == R.id.rate) {
            startActivity(new Intent(CustomerHomePage.this, Rating_Stars_Activity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadCategories() {
        progressDialog.setMessage("Loading Catgoreis...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETCATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();

                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
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
                            CategoryListView adapter = new CategoryListView(CustomerHomePage.this, (ArrayList<Category>) category_List);
                            androidListView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            message.setVisibility(View.VISIBLE);
                            Toast.makeText(CustomerHomePage.this, "No Product", Toast.LENGTH_SHORT).show();
                            message.setText("No Products");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        message.setVisibility(View.VISIBLE);
                        message.setText("Error while loading the categories");
                        Toast.makeText(CustomerHomePage.this, "Error while loading the products", Toast.LENGTH_SHORT).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
