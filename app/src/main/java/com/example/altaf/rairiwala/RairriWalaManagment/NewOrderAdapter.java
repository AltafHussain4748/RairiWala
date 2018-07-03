package com.example.altaf.rairiwala.RairriWalaManagment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.StockManagment.SellerEditProduct;
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

public class NewOrderAdapter extends RecyclerView.Adapter<NewOrderAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Order> orderLists;
    private Context context = null;
    int pos = 0;

    public NewOrderAdapter(Context mCtx, List<Order> orderLists) {
        this.mCtx = mCtx;
        this.orderLists = orderLists;

    }

    @Override
    public com.example.altaf.rairiwala.RairriWalaManagment.NewOrderAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.seller_new_order_recyclerview, null);
        return new com.example.altaf.rairiwala.RairriWalaManagment.NewOrderAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final com.example.altaf.rairiwala.RairriWalaManagment.NewOrderAdapter.ProductViewHolder holder, final int position) {
        final Order order = orderLists.get(position);
        holder.textViewname.setText("  " + order.getCustomerAddress().getName());
        holder.textViewnumber.setText("  " + order.getCustomerAddress().getHouseName());
        holder.time.setText("  " + order.getOrder_time());
        holder.totalBill.setText("Rs: " + order.getTotalbill());
        if (order.getOrder_status().equals("NEW")) {
            //option menu code
            holder.option_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//creating a popup menu
                    PopupMenu popup = new PopupMenu(mCtx, holder.option_menu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.report_customer_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.report_customer:
                                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mCtx);
                                    dialogBuilder
                                            .withTitle("Report Customer")                                  //.withTitle(null)  no title
                                            .withTitleColor("#FFFFFF")                                  //def
                                            .withDividerColor("#11000000")                              //def
                                            .withMessage("This customer will not be able to send you orders again?")                     //.withMessage(null)  no Msg
                                            .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                                            .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
                                            .withIcon(mCtx.getResources().getDrawable(R.drawable.delete))
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
                                                   // Toast.makeText(mCtx, "Seller Id:" + order.getVendor_id() + "  Customer Id" + order.getCustomer_id(), Toast.LENGTH_SHORT).show();
                                                    reportCustomer(order.getVendor_id(),order.getCustomer_id());
                                                    dialogBuilder.dismiss();
                                                }
                                            })
                                            .show();
                                    break;

                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });
        }
        holder.order_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, OrderDetail.class);
                Gson gson = new Gson();
                String orderString = gson.toJson(order);
                intent.putExtra("order", orderString);
                mCtx.startActivity(intent);

            }
        });
        if (order.getOrder_status().equals("Confirmed")) {
            holder.btn.setVisibility(View.VISIBLE);
            holder.reject_order.setVisibility(View.GONE);
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();
                    String orderString = gson.toJson(orderLists.get(position));
                    Intent intent = new Intent(mCtx, SellerAssignDeliverPerson.class);
                    intent.putExtra("order", orderString);
                    mCtx.startActivity(intent);
                }
            });

        } else {
            holder.reject_order.setVisibility(View.VISIBLE);
            holder.btn.setVisibility(View.GONE);
            holder.reject_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mCtx);
                    dialogBuilder
                            .withTitle("Reject Order")                                  //.withTitle(null)  no title
                            .withTitleColor("#FFFFFF")                                  //def
                            .withDividerColor("#11000000")                              //def
                            .withMessage("Do you want to reject order?")                     //.withMessage(null)  no Msg
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
                                    pos = position;
                                    deleteOrder(order.getOrder_id(), order.getCustomer_id(), order.getVendor_id());
                                    dialogBuilder.dismiss();
                                }
                            })
                            .show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewnumber, time, totalBill, option_menu;
        Button btn, reject_order, order_items;


        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.sender_name);
            textViewnumber = itemView.findViewById(R.id.number_of_items);
            time = itemView.findViewById(R.id.order_time);
            btn = itemView.findViewById(R.id.assign_delivery_person);
            reject_order = itemView.findViewById(R.id.reject_order);
            order_items = itemView.findViewById(R.id.order_items);
            totalBill = itemView.findViewById(R.id.order_bill);
            option_menu = itemView.findViewById(R.id.textViewOptions);

        }
    }

    private void deleteOrder(final int order_id, final int customer_id, final int vendor_id) {
        Toast.makeText(mCtx, "Deleteing.....", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.REJECTORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false) {

                                orderLists.remove(pos);

                                notifyDataSetChanged();
                                notifyItemRemoved(pos);
                            } else {
                                Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mCtx, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("order_id", String.valueOf(order_id));
                params.put("customer_id", String.valueOf(customer_id));
                params.put("vendor_id", String.valueOf(vendor_id));
                return params;
            }
        };
        RequestHandler.getInstance(mCtx).addToRequestQueue(stringRequest);
    }

    private void reportCustomer(final int vendor_id, final int customer_id) {
        Toast.makeText(mCtx, "Reporting Customer.....", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.REPORTCUSTOMER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false)
                                Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mCtx, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("customer_id", String.valueOf(customer_id));
                params.put("vendor_id", String.valueOf(vendor_id));
                return params;
            }
        };
        RequestHandler.getInstance(mCtx).addToRequestQueue(stringRequest);
    }
}


