package com.example.altaf.rairiwala.CustomerManagment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.Models.ProductDetails;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductList extends AppCompatActivity {
    List<Product> productList;
    //the recyclerview
    RecyclerView recyclerView;
    Button carts;
    TextView itemcart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_product_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        final String type = bundle.getString("Cat");
        final int vendorid = bundle.getInt("vendorid");
        carts = findViewById(R.id.add_cart);
        itemcart = findViewById(R.id.badge_notification_1);
        recyclerView = findViewById(R.id.product_list_customer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        loadProducts(vendorid, type);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int d = item.getItemId();
        if (d == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProducts(final int vendor_id, final String type) {
        /*
        * Creating a String Request
        * The request type is GET defined by first parameter
        * The URL is defined in the second parameter
        * Then we have a Response Listener and a Error Listener
        * In response listener we will get the JSON response as a String
        * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_PRODUCTS_CUSTOMER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                Product pro = new Product();
                                ProductDetails productDetails = new ProductDetails();
                                productDetails.setPrice(product.getInt("price"));
                                productDetails.setQuantity(product.getInt("quantity"));
                                productDetails.setVendor_id(product.getInt("vendor_id"));
                                pro.setProductDetails(productDetails);
                                pro.setProduct_name(product.getString("product_name"));
                                pro.setProduct_image(product.getString("image"));
                                pro.setProduct_id(product.getInt("productid"));
                                pro.setProduct_type(product.getString("quantity_type"));
                                productList.add(pro);

                            }

                            ProductAdapter adapter = new ProductAdapter(ProductList.this, productList, itemcart, carts);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProductList.this, "No Products", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ProductList.this, "Error while loading the products", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("category", type);
                params.put("vendor_id", String.valueOf(vendor_id));

                return params;
            }

        };
        ;

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
