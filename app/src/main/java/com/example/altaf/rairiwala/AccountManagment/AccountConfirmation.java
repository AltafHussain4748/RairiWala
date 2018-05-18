package com.example.altaf.rairiwala.AccountManagment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.altaf.rairiwala.R;
import com.example.altaf.rairiwala.Singelton.Constants;
import com.example.altaf.rairiwala.Singelton.RequestHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountConfirmation extends AppCompatActivity {
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    Button verification;
    EditText otp;
    private String mVerificationId = "full";
    String name, phonenumber, pin, rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_confirmation);
        otp = findViewById(R.id.otp);
        Bundle bundle = getIntent().getExtras();
        final String verificationId = bundle.getString("ID");
        name = bundle.getString("name");
        phonenumber = bundle.getString("phone");
        rule = bundle.getString("Rule");
        pin = bundle.getString("pin");
        verification = findViewById(R.id.verify);
        mAuth = FirebaseAuth.getInstance();
        verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.getText().toString());
                // [END verify_with_code]
                signInWithPhoneAuthCredential(credential);


            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Log.d(TAG, "signInWithCredential:success");
                            // startActivity(new Intent(AccountConfirmation.this, UserLogin.class));
                            Toast.makeText(AccountConfirmation.this, "Verification Done", Toast.LENGTH_SHORT).show();
                            userRegister();
                            // ...
                        } else {
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(AccountConfirmation.this, "Invalid Verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void userRegister() {
        if (name != null && pin != null && rule != null && phonenumber != null) {
            ///start string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.REGISTER_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.getBoolean("error") == false) {
                                    startActivity(new Intent(AccountConfirmation.this, UserLogin.class));
                                    AccountConfirmation.this.finish();
                                    Toast.makeText(AccountConfirmation.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AccountConfirmation.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("phone", phonenumber);
                    params.put("name", name);
                    params.put("rule", rule);
                    params.put("pin", pin);


                    return params;
                }
            };
            RequestHandler.getInstance(AccountConfirmation.this).addToRequestQueue(stringRequest);
            // end string request
        }

    }
}
