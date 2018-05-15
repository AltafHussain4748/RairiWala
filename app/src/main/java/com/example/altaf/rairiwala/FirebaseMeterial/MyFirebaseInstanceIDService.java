package com.example.altaf.rairiwala.FirebaseMeterial;

import android.util.Log;

import com.example.altaf.rairiwala.Singelton.SharedPrefManagerFirebase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by AltafHussain on 3/11/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //calling the method store token and passing token
        storeToken(refreshedToken);
    }

    private void storeToken(final String token) {
        //we will save the token in sharedpreferences later
      //  final int person_id=SharedPrefManager.getInstance(MyFirebaseInstanceIDService.this).getCustomer().getPerson_id();
        SharedPrefManagerFirebase.getInstance(MyFirebaseInstanceIDService.this).saveToken(token);
    }
}
