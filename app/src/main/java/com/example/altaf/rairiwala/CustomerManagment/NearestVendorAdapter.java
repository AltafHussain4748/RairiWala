package com.example.altaf.rairiwala.CustomerManagment;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.PerformanceMonitering.VendorReviewList;
import com.example.altaf.rairiwala.R;

import java.util.List;


public class NearestVendorAdapter extends RecyclerView.Adapter<NearestVendorAdapter.SellerViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;
    String type;
    //we are storing all the products in a list
    private List<Vendor> productList;

    //getting the context and product list with constructor
    public NearestVendorAdapter(Context mCtx, List<Vendor> productList, String type) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.type = type;
    }

    @Override
    public SellerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.customer_vendor_list_adapter, null);
        return new SellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SellerViewHolder holder, int position) {

        final Vendor v = productList.get(position);
        if (v != null) {
            holder.name.setText(v.getName());
            holder.phone.setText(v.getPerson_phone_number());
            String distance = Double.toString(v.getDistance());
            holder.distance.setText("Distance: " + distance.substring(0, 6) + "KM");
            holder.ratingdetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mCtx, VendorReviewList.class);
                    intent.putExtra("vendor_id", v.getVendor_id());
                    mCtx.startActivity(intent);
                }
            });
            holder.viewproducts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mCtx, ProductList.class);
                    intent.putExtra("Cat", type);
                    intent.putExtra("vendorid", v.getVendor_id());
                    mCtx.startActivity(intent);
                }
            });
        } else {
            Toast.makeText(mCtx, "No Seller Found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }


    class SellerViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, distance;
        Button ratingdetails, viewproducts;

        public SellerViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone_number);
            distance = itemView.findViewById(R.id.distance);
            ratingdetails = itemView.findViewById(R.id.viewDetail);
            viewproducts = itemView.findViewById(R.id.viewproducts);

        }
    }
}
