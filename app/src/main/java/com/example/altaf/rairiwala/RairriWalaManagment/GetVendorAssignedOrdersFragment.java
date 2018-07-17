package com.example.altaf.rairiwala.RairriWalaManagment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class GetVendorAssignedOrdersFragment extends Fragment {
    List<Order> orderList;
    View view;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.vendor_assigned_orders_view, container, false);
        recyclerView = view.findViewById(R.id.assignedorders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        message = view.findViewById(R.id.error_message);
        getOrders(SharedPrefManager.getInstance(getActivity()).getSeller().getVendor_id());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("AssignedOrders");
    }

    public void getOrders(final int vendor_id) {
        if (vendor_id >= 0) {
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.VendorAssignedOrders,
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
                                    newOrder.setTotalbill(order.getInt("totalBill"));
                                    if (order.getInt("DeliverPerson") != 0) {
                                        newOrder.setDeliveryperson_id(order.getInt("DeliverPerson"));
                                    } else {
                                        newOrder.setDeliveryperson_id(0);
                                    }

                                    newOrder.setDeliveryperson_id(0);
                                    orderList.add(newOrder);


                                }

                                VendorAssignedOrdersAdapter newOrderAdapter = new VendorAssignedOrdersAdapter(getActivity(), orderList);
                                recyclerView.setAdapter(newOrderAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                                message.setText("No orders Yet");
                                message.setVisibility(View.VISIBLE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            message.setText("Error while loading the orderss");
                            message.setVisibility(View.VISIBLE);

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