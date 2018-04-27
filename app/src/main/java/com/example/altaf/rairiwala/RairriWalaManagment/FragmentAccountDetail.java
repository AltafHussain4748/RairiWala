package com.example.altaf.rairiwala.RairriWalaManagment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altaf.rairiwala.Models.Vendor;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.SharedPrefManager;

/**
 * Created by AltafHussain on 3/5/2018.
 */

public class FragmentAccountDetail extends Fragment {

    View view;
    TextView name, phon, pin, shopname, rule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.seller_view_account_detail, container, false);
        Vendor vendor = SharedPrefManager.getInstance(getActivity()).getSeller();
        name = view.findViewById(R.id.name);
        phon = view.findViewById(R.id.phonenumber);
        pin = view.findViewById(R.id.pin);
        shopname = view.findViewById(R.id.shopname);
        rule = view.findViewById(R.id.rule_type);
        if (vendor != null) {
            name.setText(vendor.getName());
            phon.setText(vendor.getPerson_phone_number());
            pin.setText(String.valueOf(vendor.getPin()));
            shopname.setText(vendor.getShop_name());
            rule.setText(vendor.getRule());
        } else {
            name.setHint("No Name");
            phon.setHint("No Phone Number");
            pin.setHint("No Pin Number");
            shopname.setHint("No ShopName " + vendor.getVendor_id());
            rule.setHint("No rule");
        }
// get the reference of Button
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Account");
    }
}
