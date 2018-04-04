package com.example.altaf.rairiwala.RairriWalaManagment.StockManagment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;


/**
 * Created by AltafHussain on 12/31/2017.
 */

public class StockDetailsAdatpter extends RecyclerView.Adapter<StockDetailsAdatpter.ProductViewHolder> {
    private Context mCtx;
    private List<Product> productList;
    int positions = 0;

    public StockDetailsAdatpter(Context mCtx, List<Product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;

    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.stock_managment_stock_detail_recyclerview, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        positions = position;
        final Product product = productList.get(position);
        Glide.with(mCtx)
                .load(product.getProduct_image())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.productimage);

        holder.textViewname.setText("Name :" + product.getProduct_name());
        holder.textViewprice.setText("Price :" + product.getProductDetails().getPrice() + "/" + product.getProduct_type());
        holder.textViewQuantity.setText("Quantity :" + product.getProductDetails().getQuantity());
        if (product.getProductDetails().getQuantity() <= 10) {
            holder.textViewQuantity.setTextColor(Color.RED);
        } else {
            holder.textViewQuantity.setTextColor(Color.GREEN);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mCtx);
                dialogBuilder
                        .withTitle("Delete product")                                  //.withTitle(null)  no title
                        .withTitleColor("#FFFFFF")                                  //def
                        .withDividerColor("#11000000")                              //def
                        .withMessage("Do you want to Delete it?")                     //.withMessage(null)  no Msg
                        .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                        .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
                        .withIcon(mCtx.getResources().getDrawable(R.drawable.delete))
                        .withDuration(700)                                          //def
                        .withEffect(RotateBottom)                                         //def Effectstype.Slidetop
                        .withButton1Text("No")                                      //def gone
                        .withButton2Text("yes")                                  //def gone
                        .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                        //.setCustomView(View or ResId,context)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteProduct(product.getProductDetails().getVendor_id(), product.getProduct_id());
                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewname, textViewprice, textViewQuantity;
        ImageView productimage;
        ImageButton edit, delete;


        public ProductViewHolder(View itemView) {
            super(itemView);
            productimage = itemView.findViewById(R.id.product_image);
            textViewname = itemView.findViewById(R.id.product_name);
            textViewprice = itemView.findViewById(R.id.product_price);
            textViewQuantity = itemView.findViewById(R.id.product_quantity);
            edit = itemView.findViewById(R.id.product_edit);
            delete = itemView.findViewById(R.id.product_delete);

        }
    }

    public void deleteProduct(final int vendor_id, final int product_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.DELETEPRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("error") == false) {
                                Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                productList.remove(positions);
                                notifyItemRemoved(positions);
                            } else {
                                Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Toast.makeText(getApplicationContext(), "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", String.valueOf(vendor_id));
                params.put("product_id", String.valueOf(product_id));
                return params;
            }
        };
        RequestHandler.getInstance(mCtx).addToRequestQueue(stringRequest);

    }
}


