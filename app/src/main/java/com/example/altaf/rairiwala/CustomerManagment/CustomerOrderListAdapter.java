package com.example.altaf.rairiwala.CustomerManagment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.RairriWalaManagment.SellerAssignDeliverPerson;
import com.example.altaf.rairiwala.Singelton.OrderDetail;
import com.google.gson.Gson;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public void onBindViewHolder(CustomerOrderListAdapter.ProductViewHolder holder, final int position) {
        final Order order = orderLists.get(position);
        holder.textViewname.setText("Name:" + order.getCustomerAddress().getName());
        holder.textViewnumber.setText("House Name:" + order.getCustomerAddress().getHouseName());
        holder.time.setText("Order Time:" + order.getOrder_time());
        holder.order_status.setText("Status: " + order.getOrder_status());
        if (order.getOrder_status().equals("Confirmed")) {
            holder.order_status.setTextColor(Color.GREEN);
        } else if (order.getOrder_status().equals("Rejected")) {
            holder.order_status.setTextColor(Color.RED);
            Resources resources = mCtx.getResources();
            Drawable image = resources.getDrawable(R.drawable.delete);
            holder.imageView.setImageDrawable(image);
            //
        } else if (order.getOrder_status().equals("ASSIGNED")) {
            holder.order_status.setTextColor(Color.BLUE);
            Resources resources = mCtx.getResources();
            Drawable image = resources.getDrawable(R.drawable.dp);
            holder.imageView.setImageDrawable(image);

        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, OrderDetail.class);
                Gson gson = new Gson();
                String orderString = gson.toJson(order);
                intent.putExtra("order", orderString);
                intent.putExtra("rule","customer_hide");
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewnumber, time, order_status;
        LinearLayout linearLayout;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.sender_name);
            textViewnumber = itemView.findViewById(R.id.number_of_items);
            time = itemView.findViewById(R.id.order_time);
            order_status = itemView.findViewById(R.id.order_status);
            linearLayout = itemView.findViewById(R.id.ordersRecyclerview);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}

