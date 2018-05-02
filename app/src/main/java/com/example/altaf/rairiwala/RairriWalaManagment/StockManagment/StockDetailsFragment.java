package com.example.altaf.rairiwala.RairriWalaManagment.StockManagment;

import android.app.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.CustomerManagment.ProductAdapter;
import com.example.altaf.rairiwala.CustomerManagment.ProductList;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.Models.ProductDetails;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StockDetailsFragment extends Fragment {
    View view;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView message;
    List<Product> productList;
    boolean isScrolling = false;
    int currentItems, totalItems, scrollItems;
    LinearLayoutManager linearLayoutManager;
    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.stock_managment_stock_details, container, false);
        recyclerView = view.findViewById(R.id.stock_detail);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        productList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        message = view.findViewById(R.id.error_message);
        loadProducts(SharedPrefManager.getInstance(getActivity()).getSeller().getVendor_id());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (currentItems + scrollItems == totalItems && isScrolling) {
                    isScrolling = false;
                    loadProducts(SharedPrefManager.getInstance(getActivity()).getSeller().getVendor_id());
                }
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Stock");
    }

    private void loadProducts(final int vendor_id) {
        progressBar.setVisibility(View.VISIBLE);
        /*
        * Creating a String Request
        * The request type is GET defined by first parameter
        * The URL is defined in the second parameter
        * Then we have a Response Listener and a Error Listener
        * In response listener we will get the JSON response as a String
        * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GETVENDORSTOCK,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.GONE);
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

                            StockDetailsAdatpter adapter = new StockDetailsAdatpter(getActivity(), productList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.scrollToPosition(count);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            message.setText("No Products");
                            if (productList.size() > 0) {
                                message.setVisibility(View.GONE);
                            } else {
                                message.setVisibility(View.VISIBLE);
                            }


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.setText("Error while loading the products");
                        progressBar.setVisibility(View.GONE);
                        if (productList.size() > 0) {
                            message.setVisibility(View.GONE);
                        } else {
                            message.setVisibility(View.VISIBLE);
                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", String.valueOf(vendor_id));
                if (productList.size() == 0) {
                    count = productList.size();
                    params.put("start", String.valueOf(productList.size()));
                } else {
                    count = productList.size() - 1;
                    params.put("start", String.valueOf(productList.size()));
                }


                return params;
            }

        };
        ;

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}
