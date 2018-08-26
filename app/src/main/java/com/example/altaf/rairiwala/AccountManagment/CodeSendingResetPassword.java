package com.example.altaf.rairiwala.AccountManagment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CodeSendingResetPassword extends Fragment {
    View view;
    TextView verifyNumber;
    Button button;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    boolean isVerified = false;
    String phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.send_forget_verification_code, container, false);

        progressDialog = new ProgressDialog(getActivity());
        verifyNumber = view.findViewById(R.id.phoneNumberForgetPassword);
        button = view.findViewById(R.id.buttonVerification);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              phoneNumber = "+92" + verifyNumber.getText().toString();
                //start of phone number verification
                if (phoneNumber.length() == 13) {
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    //String request start
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Constants.IS_USER_EXISTS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        // Toast.makeText(AccountCustomerRegister.this, status, Toast.LENGTH_SHORT).show();
                                        if (jsonObject.getString("message").equals("EXISTS")) {
                                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                    phoneNumber,
                                                    120,
                                                    java.util.concurrent.TimeUnit.SECONDS,
                                                    getActivity(),
                                                    mCallbacks);
                                            progressDialog.setMessage("Sending verification code...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                                        } else if (jsonObject.getString("message").equals("DONOTEXISTS")) {
                                            Toast.makeText(getActivity(), "User Not Registered", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.hide();
                                    Toast.makeText(getActivity(), "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("phone", phoneNumber);

                            return params;
                        }
                    };
                    RequestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
                    //end of string request


                } else {
                    Toast.makeText(getActivity(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                }
                //end of phone number verification
            }
        });
        //FireBase Auth Phone Number verification
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
              /*  Toast.makeText(UserRegister.this, "VerificationComplete" + "\n" + phone_number.getText() + name.getText() + pin.getText(), Toast.LENGTH_SHORT).show();
                // Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;*/
                isVerified = true;

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
// Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(getActivity(), "Verification Failed", Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                progressDialog.dismiss();
                // Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(getActivity(), "Verification code has been send on your number", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                if (isVerified) {
                    startActivity(new Intent(getActivity(), UpdatePassword.class));
                } else {
                    Fragment fragment = new VerifyCodeFragment();
                    Bundle args = new Bundle();
                    FragmentManager fm = getFragmentManager();
                    android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    args.putString("ID", verificationId);
                    args.putString("PHONE", phoneNumber);
                    fragment.setArguments(args);
                    fragmentTransaction.commit(); // save the changes
                    isVerified=false;
                }


            }

        };
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Reset Pin");
    }


}
