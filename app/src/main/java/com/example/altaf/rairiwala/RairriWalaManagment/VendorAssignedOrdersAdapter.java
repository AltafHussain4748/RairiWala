package com.example.altaf.rairiwala.RairriWalaManagment;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class VendorAssignedOrdersAdapter extends RecyclerView.Adapter<
        VendorAssignedOrdersAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Order> orderLists;
    private Context context = null;


    public VendorAssignedOrdersAdapter(Context mCtx, List<Order> orderLists) {
        this.mCtx = mCtx;
        this.orderLists = orderLists;

    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.vendor_assigned_orders_recyclerview, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final Order order = orderLists.get(position);
        holder.textViewname.setText("  " + order.getCustomerAddress().getName());
        holder.textViewnumber.setText("  " + order.getCustomerAddress().getHouseName());
        holder.time.setText("  " + order.getOrder_time());
        holder.totalBill.setText("Rs: " + order.getTotalbill());
       /* holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
*/
        //option menu code
        holder.option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//creating a popup menu
                PopupMenu popup = new PopupMenu(mCtx, holder.option_menu);
                //inflating menu from xml resource
                popup.inflate(R.menu.assigned_order);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delivery_person_assigned:

                                getDeliveryPerson(order.getDeliveryperson_id(), holder.textViewname);
                                break;
                            case R.id.order_detail_assigned:
                                Intent intent = new Intent(mCtx, OrderDetail.class);
                                Gson gson = new Gson();
                                String orderString = gson.toJson(order);
                                intent.putExtra("order", orderString);
                                intent.putExtra("rule", "seller");
                                mCtx.startActivity(intent);
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

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewnumber, time, totalBill, option_menu;
        LinearLayout linearLayout;
        CircleImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.sender_name);
            textViewnumber = itemView.findViewById(R.id.number_of_items);
            time = itemView.findViewById(R.id.order_time);
            totalBill = itemView.findViewById(R.id.order_bill);
            linearLayout = itemView.findViewById(R.id.ordersrecyclerview);
            imageView = itemView.findViewById(R.id.imageView);
            option_menu = itemView.findViewById(R.id.textViewOptions);
        }
    }

    public boolean getDeliveryPerson(final int deliveryPerson, final TextView txt) {
        Snackbar.make(txt, "****Getting information****", 2000).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.DeliveryPersonForAssignedOrders,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false) {
                                Snackbar.make(txt, "Delivery person name =" + jsonObject.getString("name")
                                        + "\n" + "Delivery person Phone Number =" + jsonObject.getString("pn"), 10000).show();

                            } else {
                                Snackbar.make(txt, jsonObject.getString("message"), 5000).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(txt, error.getMessage().toString(), 4000).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("delivery_person_id", String.valueOf(deliveryPerson));
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

        return true;
    }
}

