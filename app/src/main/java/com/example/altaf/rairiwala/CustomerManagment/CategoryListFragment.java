package com.example.altaf.rairiwala.CustomerManagment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.Category;
import com.example.altaf.rairiwala.Models.Notifications;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.SqliteDatabase.DatabaseHandling;

import android.app.Fragment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.category_list_fragment, container, false);
        message = view.findViewById(R.id.error_message);
        androidListView = view.findViewById(R.id.grid_view_image_text);
        // androidGridView.setAdapter(adapterViewAndroid);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                Category category = category_List.get(i);
                Intent intent = new Intent(getActivity(), NearestVendor.class);
                intent.putExtra("CAT", category.getCategroy_name());
                startActivity(intent);


            }
        });
        progressDialog = new ProgressDialog(getActivity());
        category_List = new ArrayList<>();
        sqliteDb = new ArrayList<>();
        databaseHandling = new DatabaseHandling(getActivity());
        sqliteDb = databaseHandling.getAllCategories();
        notificationsList = new ArrayList<>();
        if (sqliteDb.size() > 0) {
            CategoryListView adapter = new CategoryListView(getActivity(), (ArrayList<Category>) sqliteDb);
            androidListView.setAdapter(adapter);
            loadCategories();
        } else {
            loadCategories();
        }
        //end of sqlite databse handler
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
                            } else if (category_List.size() > sqliteDb.size()) {
                                databaseHandling.deleteAllCategories();
                                for (Category category : category_List) {
                                    databaseHandling.insertCategories(category);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            message.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "No Category", Toast.LENGTH_SHORT).show();
                            message.setText("No Category");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        message.setVisibility(View.VISIBLE);
                        message.setText("Error while loading the categories");

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

}

