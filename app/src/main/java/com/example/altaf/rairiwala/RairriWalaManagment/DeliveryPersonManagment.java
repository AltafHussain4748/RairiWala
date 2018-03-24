package com.example.altaf.rairiwala.RairriWalaManagment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.Models.Vendor;
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

/**
 * Created by AltafHussain on 3/5/2018.
 */

public class DeliveryPersonManagment extends Fragment {
    List<DeliveryPerson> deliveryPersonList;
    View view;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.seller_deliver_person_management, container, false);
        recyclerView = view.findViewById(R.id.dp_list_recyle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        deliveryPersonList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_delivery_person);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Seller_Add_Deliver_Persons.class));
            }
        });
// get the reference of Button
        loadDeliverPersons(SharedPrefManager.getInstance(getActivity()).getSeller().getVendor_id());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Deliver Persons");
    }

    public void loadDeliverPersons(final int vendor_id) {
        if (vendor_id >= 0) {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SELLER_GET_DP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                //converting the string to json array object
                                JSONArray array = new JSONArray(response);
                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject dp = array.getJSONObject(i);
                                    DeliveryPerson deliveryPerson = new DeliveryPerson();
                                    deliveryPerson.setName(dp.getString("Person_Name"));
                                    deliveryPerson.setPerson_id(dp.getInt("Person_Id"));
                                    deliveryPerson.setDelivery_person_id(dp.getInt("Delivery_PId"));
                                    deliveryPerson.setPin(dp.getString("Person_Password"));
                                    deliveryPerson.setRule(dp.getString("Rule"));
                                    deliveryPerson.setStatus(dp.getString("Account_Status"));
                                    deliveryPerson.setPerson_phone_number(dp.getString("Person_Phone_Number"));
                                    deliveryPerson.setIsFree(dp.getString("isfree"));
                                    deliveryPersonList.add(deliveryPerson);

                                }

                                Delivery_Person_Adapter newOrderAdapter = new Delivery_Person_Adapter(getActivity(), deliveryPersonList);
                                recyclerView.setAdapter(newOrderAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Error while loading the Delivery Persons", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("vendor_id", String.valueOf(vendor_id));

                    return params;
                }

            };


            //adding our stringrequest to queue
            Volley.newRequestQueue(getActivity()).add(stringRequest);
        }

    }
}

