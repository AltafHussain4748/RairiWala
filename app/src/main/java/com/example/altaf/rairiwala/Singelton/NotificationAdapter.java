package com.example.altaf.rairiwala.Singelton;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.altaf.rairiwala.Models.Notifications;
import com.example.altaf.rairiwala.R;

import java.util.List;


/**
 * Created by AltafHussain on 12/31/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Notifications> notifications;
    private Context context = null;


    public NotificationAdapter(Context mCtx, List<Notifications> orderLists) {
        this.mCtx = mCtx;
        this.notifications = orderLists;

    }

    @Override
    public NotificationAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.notification_list, null);
        return new NotificationAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ProductViewHolder holder, final int position) {
        final Notifications order = notifications.get(position);
        holder.title.setText(order.getTitle());
        holder.message.setText(order.getMessage());


    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView title, message;


        public ProductViewHolder(View itemView) {
            super(itemView);
            context = (Context) itemView.getContext();
            title = itemView.findViewById(R.id.title_list);
            message = itemView.findViewById(R.id.message_list);

        }
    }
}

