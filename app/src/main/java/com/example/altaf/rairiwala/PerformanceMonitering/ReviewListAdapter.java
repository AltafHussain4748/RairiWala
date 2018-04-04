package com.example.altaf.rairiwala.PerformanceMonitering;

/**
 * Created by AltafHussain on 3/30/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.altaf.rairiwala.CustomerManagment.OrderItems;
import com.example.altaf.rairiwala.Models.CustomerAddress;
import com.example.altaf.rairiwala.Models.FeedBack;
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

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<FeedBack> feedBackList;
    private Context context = null;


    public ReviewListAdapter(Context mCtx, List<FeedBack> feedBackList) {
        this.mCtx = mCtx;
        this.feedBackList = feedBackList;

    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.vendor_reviewlist_recyclerview, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final FeedBack feedBack = feedBackList.get(position);
        holder.name.setText("  " +feedBack.getPoster_name());
        holder.des.setText("  " +feedBack.getDescription());
        holder.time.setText("  " +feedBack.getDate());
        holder.ratingBar.setRating((float) feedBack.getStars());



    }

    @Override
    public int getItemCount() {
        return feedBackList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView name, des, time;
        RatingBar ratingBar;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            name = itemView.findViewById(R.id.posted_by);
            des = itemView.findViewById(R.id.des);
            time = itemView.findViewById(R.id.datetime);
            ratingBar = itemView.findViewById(R.id.stars);

        }
    }
}


