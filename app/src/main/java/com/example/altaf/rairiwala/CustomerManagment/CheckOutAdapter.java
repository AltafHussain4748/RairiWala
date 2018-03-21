package com.example.altaf.rairiwala.CustomerManagment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.R;
import com.google.gson.Gson;

import java.util.List;


/**
 * Created by AltafHussain on 12/31/2017.
 */

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Product> productList;
    private Context context = null;
    Button orders;

    public CheckOutAdapter(Context mCtx, List<Product> productLists, Button orders) {
        this.mCtx = mCtx;
        this.productList = productLists;
        this.orders = orders;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.checkout, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        final Product product = productList.get(position);
        Glide.with(context)
                .load(product.getProduct_image())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.productimage);
        int totalprice = product.getProductDetails().getQuantity() * product.getProductDetails().getPrice();
        holder.textViewname.setText(mCtx.getString(R.string.name) + product.getProduct_name());
        holder.textViewprice.setText("Total Price :" + totalprice);
        holder.value.setText(":" + product.getProductDetails().getQuantity() + "\t" + product.getProduct_type());
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(productList);
                Intent intent = new Intent(mCtx, PlaceOrder.class);
                intent.putExtra("PRODUCT_LIST", jsonString);
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewprice;
        ImageView productimage;
        TextView value;
        public RelativeLayout viewBackground, viewForeground;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.checkout_product_name);
            textViewprice = itemView.findViewById(R.id.checkout_product_price);
            productimage = itemView.findViewById(R.id.checkout_product_image);
            value = itemView.findViewById(R.id.checkout_value);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

        }
    }

    public void removeItem(int position) {
        productList.remove(position);

        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Product item, int position) {
        productList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

}
