package com.example.altaf.rairiwala.Singelton;

/**
 * Created by AltafHussain on 3/11/2018.
 */

import android.content.Context;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSession;


/**
 * Created by AltafHussain on 3/5/2018.
 */

public class SharedPrefManagerFirebase  {
    private static SharedPrefManagerFirebase mInstance;
    private static Context mCtx;
    private static final String SHARED_PREF_NAMES = "mysharedpref1243";
    private static final String DeviceToken = "token";
    private static final String DeviceTokenUp= "updatedtoken";

    private static final String ISACTIVITYALIVE= "updatedtokFGen";


    private SharedPrefManagerFirebase(Context context) {
        mCtx = context;

    }

    public static synchronized SharedPrefManagerFirebase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManagerFirebase(context);
        }
        return mInstance;
    }


    public boolean saveToken(String token) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DeviceToken, token);
        editor.apply();
        return true;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        String tokens = sharedPreferences.getString(DeviceToken, "no");
        return tokens;
    }

    public boolean saveUpdatedToken(String token) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DeviceTokenUp, token);
        editor.apply();
        return true;
    }

    public String getTokenUpdated() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        String tokens = sharedPreferences.getString(DeviceTokenUp, "no");
        return tokens;
    }
    public boolean saveStateActivityNewOrderListSeller(boolean s){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ISACTIVITYALIVE, s);
        editor.apply();
        return  true;
    }
    public boolean getStateActivityNewOrderListSeller(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        boolean isAlive = sharedPreferences.getBoolean(ISACTIVITYALIVE, false);
        return isAlive;
    }
    public boolean saveActivityStateSellerHomePage(boolean s){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("SellerHomePage", s);
        editor.apply();
        return  true;
    }
    public boolean getActivityStateSellerHomePage(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        boolean isAlive = sharedPreferences.getBoolean("SellerHomePage", false);
        return isAlive;
    }
    public boolean saveActivityStateDeliveryPersonHomePage(boolean s){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("saveActivityStateDeliveryPersonHomePage", s);
        editor.apply();
        return  true;
    }
    public boolean getActivityStateDeliveryPersonHomePage(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        boolean isAlive = sharedPreferences.getBoolean("saveActivityStateDeliveryPersonHomePage", false);
        return isAlive;
    }
    public boolean saveActivityStateCustomerHomePage(boolean s){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("saveActivityStateCustomerPersonHomePage", s);
        editor.apply();
        return  true;
    }
    public boolean getActivityStateCustomerHomePage(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        boolean isAlive = sharedPreferences.getBoolean("saveActivityStateCustomerPersonHomePage", false);
        return isAlive;
    }
}

