package com.example.altaf.rairiwala.RairriWalaManagment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.OrderDetail;
import com.google.gson.Gson;

import java.util.List;


/**
 * Created by AltafHussain on 12/31/2017.
 */

public class NewOrderAdapter extends RecyclerView.Adapter<NewOrderAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Order> orderLists;
    private Context context = null;


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
    public void onBindViewHolder(com.example.altaf.rairiwala.RairriWalaManagment.NewOrderAdapter.ProductViewHolder holder, final int position) {
        final Order order = orderLists.get(position);
        holder.textViewname.setText("  " + order.getCustomerAddress().getName());
        holder.textViewnumber.setText("  " + order.getCustomerAddress().getHouseName());
        holder.time.setText("  " + order.getOrder_time());
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
            holder.confirm_order.setVisibility(View.GONE);
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();
                    String orderString = gson.toJson(orderLists.get(position));
                    Intent intent=new Intent(mCtx, SellerAssignDeliverPerson.class);
                    intent.putExtra("order",orderString);
                    mCtx.startActivity(intent);
                }
            });

        } else {
            holder.confirm_order.setVisibility(View.VISIBLE);
            holder.btn.setVisibility(View.GONE);
            holder.confirm_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mCtx, OrderDetail.class);
                    Gson gson = new Gson();
                    String orderString = gson.toJson(order);
                    intent.putExtra("order", orderString);
                    mCtx.startActivity(intent);

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewnumber, time;
        Button btn, confirm_order, order_items;
        ;


        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.sender_name);
            textViewnumber = itemView.findViewById(R.id.number_of_items);
            time = itemView.findViewById(R.id.order_time);
            btn = itemView.findViewById(R.id.assign_delivery_person);
            confirm_order = itemView.findViewById(R.id.confirm_order);
            order_items = itemView.findViewById(R.id.order_items);

        }
    }
}

