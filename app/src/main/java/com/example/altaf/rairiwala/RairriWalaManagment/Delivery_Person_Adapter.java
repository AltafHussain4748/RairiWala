package com.example.altaf.rairiwala.RairriWalaManagment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.altaf.rairiwala.Models.DeliveryPerson;
import com.example.altaf.rairiwala.R;

import java.util.List;




public class Delivery_Person_Adapter extends RecyclerView.Adapter<Delivery_Person_Adapter.SellerViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    private List<DeliveryPerson> dpList;

    //getting the context and product list with constructor
    public Delivery_Person_Adapter(Context mCtx, List<DeliveryPerson> dpList) {
        this.mCtx = mCtx;
        this.dpList = dpList;

    }

    @Override
    public Delivery_Person_Adapter.SellerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.seller_delivery_person_recyclerview, null);
        return new Delivery_Person_Adapter.SellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Delivery_Person_Adapter.SellerViewHolder holder, int position) {

        DeliveryPerson deliveryPerson = dpList.get(position);
        holder.name.setText("  "+deliveryPerson.getName());
        holder.phone.setText("  "+deliveryPerson.getPerson_phone_number());
        holder.account_status.setText("  "+deliveryPerson.getStatus());
    }


    @Override
    public int getItemCount() {
        return dpList.size();
    }


    class SellerViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, account_status;

        public SellerViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.dp_name);
            phone = itemView.findViewById(R.id.dp_phone_number);
            account_status = itemView.findViewById(R.id.dp_account_status);


        }
    }
}

