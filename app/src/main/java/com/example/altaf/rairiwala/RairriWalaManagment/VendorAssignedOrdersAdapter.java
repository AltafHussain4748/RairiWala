package com.example.altaf.rairiwala.RairriWalaManagment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altaf.rairiwala.Models.Order;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.OrderDetail;
import com.google.gson.Gson;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        final Order order = orderLists.get(position);
        holder.textViewname.setText("  " + order.getCustomerAddress().getName());
        holder.textViewnumber.setText("  " + order.getCustomerAddress().getHouseName());
        holder.time.setText("  " + order.getOrder_time());
        holder.totalBill.setText("Rs: " + order.getTotalbill());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mCtx, OrderDetail.class);
                Gson gson = new Gson();
                String orderString = gson.toJson(order);
                intent.putExtra("order", orderString);
                intent.putExtra("rule","seller");
                mCtx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewnumber, time, totalBill;
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
        }
    }


}

