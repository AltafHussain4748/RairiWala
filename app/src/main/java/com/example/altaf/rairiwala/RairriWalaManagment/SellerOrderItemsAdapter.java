package com.example.altaf.rairiwala.RairriWalaManagment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.R;


import java.util.List;


/**
 * Created by AltafHussain on 12/31/2017.
 */

public class SellerOrderItemsAdapter extends RecyclerView.Adapter<SellerOrderItemsAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Product> productList;
    private Context context = null;


    public SellerOrderItemsAdapter(Context mCtx, List<Product> productLists) {
        this.mCtx = mCtx;
        this.productList = productLists;

    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.seller_order_items_recyclerview, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        int count = 0;
        if (product != null) {
            if (holder.name != null && holder.price != null && holder.image != null && holder.value1 != null) {
                Glide.with(context)
                        .load(product.getProduct_image())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(holder.image);
                int totalprice = product.getProductDetails().getQuantity() * product.getProductDetails().getPrice();
                holder.name.setText("Name :"+product.getProduct_name());
                holder.price.setText("Total Price :"+totalprice);
                holder.value1.setText(":"+product.getProductDetails().getQuantity());
            } else {
                count++;
            }
        }
        if (count > 0) {
            Toast.makeText(mCtx, "Some problem with UI", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView name, price;
        ImageView image;
        TextView value1;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            name = itemView.findViewById(R.id.seller_order_product_name);
            price = itemView.findViewById(R.id.seller_order_product_price);
            image = itemView.findViewById(R.id.seller_order_product_image);
            value1 = itemView.findViewById(R.id.seller_order_value);


        }
    }


}

