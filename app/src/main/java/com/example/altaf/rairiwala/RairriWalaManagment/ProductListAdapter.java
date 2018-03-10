package com.example.altaf.rairiwala.RairriWalaManagment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.altaf.rairiwala.Models.Product;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;
    String CategoryType;
    //we are storing all the products in a list
    private List<Product> productList;

    //getting the context and product list with constructor
    public ProductListAdapter(Context mCtx, List<Product> productList, String Type) {
        this.mCtx = mCtx;
        this.productList = productList;
        CategoryType = Type;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.seller_product_list, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final Product product = productList.get(position);

        //binding the data with the viewholder views
        holder.product_name.setText(product.getProduct_name());
        holder.product_type.setText(product.getProduct_type());

        Glide.with(mCtx)
                .load(product.getProduct_image())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.imageView);
        holder.pro_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        Constants.URL_SELLER_ADD_PRODUCT,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);

                                    if (obj.getBoolean("error") == false) {


                                        Toast.makeText(
                                                mCtx,
                                                "Added",
                                                Toast.LENGTH_LONG
                                        ).show();


                                    } else {
                                        Toast.makeText(
                                                mCtx,
                                                obj.getString("message"),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(
                                        mCtx,
                                        "There was an error",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("vendorid", String.valueOf(SharedPrefManager.getInstance(mCtx).getSeller().getVendor_id()));
                        params.put("productid", String.valueOf(product.getProduct_id()));
                        params.put("price", String.valueOf(holder.price.getText()));
                        params.put("quantity", String.valueOf(holder.quantity.getText()));
                        params.put("category", CategoryType);
                        return params;
                    }

                };

                RequestHandler.getInstance(mCtx).addToRequestQueue(stringRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView product_name, product_type;
        EditText price, quantity;
        ImageView imageView;
        ImageButton pro_add_btn;
        //   RelativeLayout layout;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            pro_add_btn = itemView.findViewById(R.id.product_add_btn);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            product_name = itemView.findViewById(R.id.product_name);
            product_type = itemView.findViewById(R.id.product_type);
            //layout=itemView.findViewById(R.id.mylayout);
        }
    }
}