package com.example.altaf.rairiwala.RairriWalaManagment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.altaf.rairiwala.CustomerManagment.OrderItems;
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.Models.OrderDetails;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.R;
import com.google.gson.Gson;

import java.util.ArrayList;
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
    public void onBindViewHolder(com.example.altaf.rairiwala.RairriWalaManagment.NewOrderAdapter.ProductViewHolder holder, int position) {
        final Order order = orderLists.get(position);
        holder.textViewname.setText("Name:" + order.getCustomerAddress().getName());
        holder.textViewnumber.setText("House Name:" + order.getCustomerAddress().getHouseName());
        holder.time.setText("Order Time:"+order.getOrder_time());


    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewnumber,time;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.sender_name);
            textViewnumber = itemView.findViewById(R.id.number_of_items);
            time=itemView.findViewById(R.id.order_time);


        }
    }
}

