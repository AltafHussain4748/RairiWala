package com.example.altaf.rairiwala.AccountManagment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class CheckInterNet {
    Context context=null;
   public CheckInterNet(Context context){
        this.context=context;
    }
   public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
