package com.example.altaf.rairiwala.CustomerManagment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.altaf.rairiwala.Models.History;
import com.example.altaf.rairiwala.R;

import java.util.List;

public class CustomerBuyingHistoryAdapter extends RecyclerView.Adapter<CustomerBuyingHistoryAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<History> orders;


    public CustomerBuyingHistoryAdapter(Context mCtx, List<History> productLists) {
        this.mCtx = mCtx;
        this.orders = productLists;

    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.buyinghistoryrecyclerview, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        History order = orders.get(position);
        holder.sellerName.setText(order.getName());
        holder.price.setText("Rs:" + order.getPrice());
        holder.phoneNumber.setText(order.getPhoneNumber());
        holder.time.setText(order.getDateTime());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView sellerName, phoneNumber, price, time;


        public ProductViewHolder(View itemView) {
            super(itemView);
            sellerName = itemView.findViewById(R.id.sellername);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            price = itemView.findViewById(R.id.price);
            time = itemView.findViewById(R.id.datetime);

        }
    }


}

