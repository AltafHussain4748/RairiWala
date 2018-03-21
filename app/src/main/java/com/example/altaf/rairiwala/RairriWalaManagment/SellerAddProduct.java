package com.example.altaf.rairiwala.RairriWalaManagment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerAddProduct extends AppCompatActivity {
    List<Product> productList;
    ProgressDialog progressDialog;
    int cat_id;
    String catType;
    //the recyclerview
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_add_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        cat_id = (Integer) bundle.getInt("CategoryId");
        catType = bundle.getString("CATNAME");
        productList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.product_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loadProducts(cat_id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //load products
    private void loadProducts(final int category_id) {
        progressDialog.setMessage("Loading Products...");
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            //converting the string to json array object

                            JSONArray array = new JSONArray(response);
                            if (array.length() == 0) {
                                Toast.makeText(SellerAddProduct.this, " No Products", Toast.LENGTH_SHORT).show();

                            } else {
                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject product = array.getJSONObject(i);
//                                Toast.makeText(SellerAddProduct.this, product.getInt("product_id"), Toast.LENGTH_SHORT).show();
                                    //adding the product to product list
                                    Product pro = new Product();
                                    pro.setProduct_name(product.getString("product_name"));
                                    pro.setProduct_id(product.getInt("product_id"));
                                    pro.setProduct_image(product.getString("product_image"));
                                    pro.setProduct_type(product.getString("quantity_type"));
                                    productList.add(pro);
                                }


                                //creating adapter object and setting it to recyclerview
                                ProductListAdapter adapter = new ProductListAdapter(SellerAddProduct.this, productList, catType);
                                recyclerView.setAdapter(adapter);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SellerAddProduct.this, "No Products Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(SellerAddProduct.this, "Error while loading the products", Toast.LENGTH_SHORT).show();
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cat_id", String.valueOf(category_id));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

}
