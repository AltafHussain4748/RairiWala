package com.example.altaf.rairiwala.Singelton;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSession;

import com.example.altaf.rairiwala.Models.Customer;
import com.example.altaf.rairiwala.Models.Vendor;
import com.google.gson.Gson;

/**
 * Created by AltafHussain on 3/5/2018.
 */

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "mysharedpref12";
    private static final String KEY_CUSTOMER = "customer";
    private static final String KEY_SELLER = "seller";
    private static final String DeviceToken = "token";


    private SharedPrefManager(Context context) {
        mCtx = context;

    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean addCustomerToPref(Customer customer) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        editor.putString(KEY_CUSTOMER, json);
        editor.apply();
        return true;
    }

    public Customer getCustomer() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_CUSTOMER, "");
        Customer obj = gson.fromJson(json, Customer.class);
        return obj;
    }

    public boolean addSellerToPref(Vendor vendor) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(vendor);
        editor.putString(KEY_SELLER, json);
        editor.apply();
        return true;
    }

    public Vendor getSeller() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_SELLER, "");
        Vendor obj = gson.fromJson(json, Vendor.class);
        return obj;
    }

    public boolean logOut() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public boolean saveToken(String token) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DeviceToken, token);
        editor.apply();
        return true;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String tokens = sharedPreferences.getString(DeviceToken, "");
        return tokens;
    }
}
