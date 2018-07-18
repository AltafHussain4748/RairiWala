package com.example.altaf.rairiwala.DeliverPersonManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.PerformanceMonitering.SystemVendorResponseTime;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.OrderDetail;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;


/**
 * Created by AltafHussain on 12/31/2017.
 */

public class AssignedOrderAdapter extends RecyclerView.Adapter<AssignedOrderAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Order> orderLists;
    private Context context = null;


    public AssignedOrderAdapter(Context mCtx, List<Order> orderLists) {
        this.mCtx = mCtx;
        this.orderLists = orderLists;

    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.delivery_person_order_list_recyclerview, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        final Order order = orderLists.get(position);
        holder.textViewname.setText("  " + order.getCustomerAddress().getName());
        holder.textViewnumber.setText("  " + order.getCustomerAddress().getHouseName());
        holder.time.setText("  " + order.getOrder_time());
        holder.totalBill.setText("Rs: " + order.getTotalbill());
        holder.order_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, OrderDetail.class);
                Gson gson = new Gson();
                String orderString = gson.toJson(order);
                intent.putExtra("order", orderString);
                intent.putExtra("rule","dp");
                mCtx.startActivity(intent);

            }
        });
        holder.delivery_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mCtx);
                dialogBuilder
                        .withTitle("Assign Dp")                                  //.withTitle(null)  no title
                        .withTitleColor("#FFFFFF")                                  //def
                        .withDividerColor("#11000000")                              //def
                        .withMessage("Do you want to deliver order??")                     //.withMessage(null)  no Msg
                        .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                        .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
                        .withIcon(mCtx.getResources().getDrawable(R.drawable.user))
                        .withDuration(700)                                          //def
                        .withEffect(RotateBottom)                                         //def Effectstype.Slidetop
                        .withButton1Text("No")                                      //def gone
                        .withButton2Text("yes")                                  //def gone
                        .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                        //.setCustomView(View or ResId,context)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deliverOrder(order.getOrder_id(), order.getVendor_id(), order.getCustomer_id(), position);


                                dialogBuilder.dismiss();
                            }
                        })
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewnumber, time, totalBill;
        Button order_items, delivery_order;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.sender_name);
            textViewnumber = itemView.findViewById(R.id.number_of_items);
            time = itemView.findViewById(R.id.order_time);
            order_items = itemView.findViewById(R.id.order_items);
            delivery_order = itemView.findViewById(R.id.order_delivery);
            totalBill = itemView.findViewById(R.id.order_bill);
        }
    }

    public void deliverOrder(final int order_id, final int vendor_id, final int cutomer_id, final int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.DELIVERORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false) {
                                Toast.makeText(mCtx, "Order delivered", Toast.LENGTH_SHORT).show();
                                orderLists.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, orderLists.size());
                                new SystemVendorResponseTime(mCtx).addNewOrderPlacementTime(vendor_id, order_id, "delivered");
                            } else {
                                Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", String.valueOf(vendor_id));
                params.put("order_id", String.valueOf(order_id));
                params.put("customer_id", String.valueOf(cutomer_id));

                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }
}


