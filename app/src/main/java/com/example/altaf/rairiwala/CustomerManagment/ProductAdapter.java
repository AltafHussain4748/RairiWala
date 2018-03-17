package com.example.altaf.rairiwala.CustomerManagment;

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
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by AltafHussain on 12/31/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Product> productList;
    private Context context = null;
    TextView itemcount;
    Button carts;
    int count = 0;
    ArrayList<Product> addproducts;

    public ProductAdapter(Context mCtx, List<Product> productList, TextView itemcount, Button carts) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.itemcount = itemcount;
        addproducts = new ArrayList<>();
        this.carts = carts;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.product_list_recyclerview, null);
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

        holder.textViewname.setText("Name :" + product.getProduct_name());
        holder.textViewprice.setText("Price :" + product.getProductDetails().getPrice() + "/" + product.getProduct_type());


        final String txt = holder.textViewprice.getText().toString();
        final EditText valuesstring = holder.value;
        //Selection of quantity
        holder.nag.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String v = valuesstring.getText().toString();
                int val = Integer.parseInt(v);
                if (val == 0) {
                    val = val - 1;

                    valuesstring.setText(Integer.toString(0));
                } else {
                    val = val - 1;

                    valuesstring.setText(Integer.toString(val));
                }


            }
        });
        holder.pos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String v1 = valuesstring.getText().toString();
                int val1 = Integer.parseInt(v1);
                val1 = val1 + 1;
                valuesstring.setText(Integer.toString(val1));
            }
        });//end of quantity selection
        holder.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAdded = false;
                //  Toast.makeText(mCtx, "Price" + txt + "\n" + "Kg Selected" + valuesstring.getText().toString(), Toast.LENGTH_SHORT).show();
                for (Product pro : addproducts) {

                    if (pro.getProduct_id() == product.getProduct_id()) {
                        isAdded = true;
                    }
                }

                if (isAdded == false) {
                    String product_names = null;
                    if (Integer.parseInt(holder.value.getText().toString()) <= 0) {
                        Toast.makeText(mCtx, "Please Select Quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        if (product.getProductDetails().getQuantity() >= Integer.parseInt(holder.value.getText().toString())) {
                            product.getProductDetails().setQuantity(Integer.parseInt(holder.value.getText().toString()));
                            addproducts.add(product);
                            count = count + 1;
                            itemcount.setText(Integer.toString(count));
                        } else {
                            Toast.makeText(mCtx, "" + "Quantity exceeded" + product.getProduct_name() + "\n", Toast.LENGTH_SHORT).show();
                        }
                    }


                }

            }
        });
        //add to cart button click listener

        carts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addproducts.size() <= 0) {

                    Toast.makeText(mCtx, "No Item selected", Toast.LENGTH_SHORT).show();

                } else {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(addproducts);
                    Intent intent = new Intent(mCtx, OrderItems.class);
                    intent.putExtra("CART", jsonString);
                    mCtx.startActivity(intent);
                }

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
        ImageButton cart;
        EditText value;
        Button nag, pos;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            textViewname = itemView.findViewById(R.id.product_name);
            textViewprice = itemView.findViewById(R.id.product_price);
            productimage = itemView.findViewById(R.id.product_image);
            cart = itemView.findViewById(R.id.product_btn);
            value = itemView.findViewById(R.id.value);
            pos = itemView.findViewById(R.id.positive);
            nag = itemView.findViewById(R.id.negative);


        }
    }
}
