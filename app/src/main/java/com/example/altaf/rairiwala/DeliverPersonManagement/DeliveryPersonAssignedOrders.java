package com.example.altaf.rairiwala.DeliverPersonManagement;


import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;
import android.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by AltafHussain on 3/5/2018.
 */

public class DeliveryPersonAssignedOrders extends Fragment {
    View view;
    List<Order> ordersList;
    RecyclerView recyclerView;
    AssignedOrderAdapter assignedOrderAdapter;
    ProgressBar progressBar;
    TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.delivery_person_order_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.assign_order_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ordersList = new ArrayList<>();
        message = view.findViewById(R.id.error_message);
        progressBar = view.findViewById(R.id.progressBar);
        fillOrders(SharedPrefManager.getInstance(getActivity()).getDeliveryPerson().getDelivery_person_id());

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("AccountDetails");
    }

    public void fillOrders(final int delivery_person_id) {
        if (delivery_person_id > 0) {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.DeliveryPersonOrders,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBar.setVisibility(View.GONE);
                                //converting the string to json array object
                                JSONArray array = new JSONArray(response);
                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject order = array.getJSONObject(i);
                                    CustomerAddress customerAddress = new CustomerAddress();
                                    Order newOrder = new Order();
                                    customerAddress.setName(order.getString("Name"));
                                    customerAddress.setLatiitude(order.getDouble("Latitude"));
                                    customerAddress.setLongitude(order.getDouble("Longitude"));
                                    customerAddress.setStreetName(order.getString("StreetName"));
                                    customerAddress.setHouseName(order.getString("House_Number"));
                                    newOrder.setCustomerAddress(customerAddress);
                                    newOrder.setVendor_id(order.getInt("Vendor_Id"));
                                    newOrder.setCustomer_id(order.getInt("Customer_Id"));
                                    newOrder.setOrder_status(order.getString("Order_Status"));
                                    newOrder.setOrder_time(order.getString("DateTime"));
                                    newOrder.setOrder_id(order.getInt("Order_Id"));
                                    newOrder.setDeliveryperson_id(order.getInt("DeliverPerson"));
                                    ordersList.add(newOrder);

                                }
                                ordersList.stream().distinct().collect(Collectors.toList());
                                assignedOrderAdapter = new AssignedOrderAdapter(getActivity(), ordersList);
                                recyclerView.setAdapter(assignedOrderAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                message.setVisibility(View.VISIBLE);
                                message.setText("No New Orders");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            message.setVisibility(View.VISIBLE);
                            message.setText("Error");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("delivery_person_id", String.valueOf(delivery_person_id));

                    return params;
                }

            };


            //adding our stringrequest to queue
            Volley.newRequestQueue(getActivity()).add(stringRequest);
        }

    }

}

