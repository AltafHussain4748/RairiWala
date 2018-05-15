package com.example.altaf.rairiwala.CustomerManagment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;

import java.util.List;


/**
 * Created by AltafHussain on 12/31/2017.
 */

public class CustomerOrderListAdapter extends RecyclerView.Adapter<CustomerOrderListAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Order> orderLists;
    private Context context = null;


    public CustomerOrderListAdapter(Context mCtx, List<Order> orderLists) {
        this.mCtx = mCtx;
        this.orderLists = orderLists;

    }

    @Override
    public CustomerOrderListAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.customer_order_list_recyclerview, null);
        return new CustomerOrderListAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerOrderListAdapter.ProductViewHolder holder, int position) {
        final Order order = orderLists.get(position);
        holder.textViewname.setText("Name:" + order.getCustomerAddress().getName());
        holder.textViewnumber.setText("House Name:" + order.getCustomerAddress().getHouseName());
        holder.time.setText("Order Time:" + order.getOrder_time());
        holder.order_status.setText("Status: " + order.getOrder_status());
        if (order.getOrder_status().equals("Confirmed")) {
            holder.order_status.setTextColor(Color.GREEN);
        } else {
            holder.order_status.setTextColor(Color.RED);
        }


    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewnumber, time, order_status;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.sender_name);
            textViewnumber = itemView.findViewById(R.id.number_of_items);
            time = itemView.findViewById(R.id.order_time);
            order_status = itemView.findViewById(R.id.order_status);

        }
    }
}

