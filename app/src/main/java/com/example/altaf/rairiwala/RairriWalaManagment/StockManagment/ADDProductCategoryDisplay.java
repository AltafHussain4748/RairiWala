package com.example.altaf.rairiwala.RairriWalaManagment.StockManagment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.Category;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AltafHussain on 3/5/2018.
 */

public class ADDProductCategoryDisplay extends Fragment {
    List<Category> category_List;
    ProgressDialog progressDialog;
    GridView androidListView;
    List<Category> sqliteDb;
    View view;
    DatabaseHandling databaseHandling;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.seller_category_add_product, container, false);
// get the reference of Button
        androidListView = view.findViewById(R.id.grid_view_image_text);
        // androidGridView.setAdapter(adapterViewAndroid);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Category category = sqliteDb.get(i);
                Intent intent = new Intent(getActivity(), SellerAddProduct.class);
                intent.putExtra("CategoryId", category.getCategroy_id());
                intent.putExtra("CATNAME", category.getCategroy_name());
                startActivity(intent);
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        category_List = new ArrayList<>();
        sqliteDb = new ArrayList<>();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        databaseHandling = new DatabaseHandling(getActivity());
        sqliteDb = databaseHandling.getAllCategories();
        if (sqliteDb.size() > 0) {
            Category category = sqliteDb.get(0);
            CategoryListView adapter = new CategoryListView(getActivity(), (ArrayList<Category>) sqliteDb);
            androidListView.setAdapter(adapter);
        } else {
            loadCategories();
        }

// get the reference of Button
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Categories");
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
                            //creating adapter object and setting it to recyclerview
                            sqliteDb = category_List;
                            for (Category category : category_List) {
                                databaseHandling.insertCategories(category);
                            }
                            CategoryListView adapter = new CategoryListView(getActivity(), (ArrayList<Category>) category_List);
                            androidListView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "No Product", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error while loading the products", Toast.LENGTH_SHORT).show();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }


}
