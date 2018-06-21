package com.example.altaf.rairiwala.AccountManagment;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class UserRegister extends AppCompatActivity {
    private static final String TAG = "PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    EditText phone_number, pin, name;
    Button btn;
    boolean isExists = true;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_register);
        Bundle bundle = getIntent().getExtras();
        final String type = bundle.getString("TYPE");
        progressDialog = new ProgressDialog(UserRegister.this);

        phone_number = findViewById(R.id.sign_up_phone_number);
        btn = findViewById(R.id.signup_btn);
        pin = findViewById(R.id.sign_up_pin);
        name = findViewById(R.id.sign_up_name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone_number.getText().toString().length() == 10) {
                    progressDialog.setMessage("Registering user...");
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
                                            Toast.makeText(UserRegister.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        } else if (jsonObject.getString("message").equals("DONOTEXISTS")) {
                                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                    "+92" + phone_number.getText().toString(),
                                                    120,
                                                    java.util.concurrent.TimeUnit.SECONDS,
                                                    UserRegister.this,
                                                    mCallbacks);
                                        } else {
                                            Toast.makeText(UserRegister.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getApplicationContext(), "There was some error.Please try again....", Toast.LENGTH_LONG).show();

                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("phone", "+92" + phone_number.getText().toString());

                            return params;
                        }
                    };
                    RequestHandler.getInstance(UserRegister.this).addToRequestQueue(stringRequest);
                    //end of string request


                } else {
                    Toast.makeText(UserRegister.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //already have an account then siugn in
        findViewById(R.id.already_account_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserRegister.this, UserLogin.class));
                UserRegister.this.finish();
            }
        });

        //FireBase Auth Phone Number verification
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
              /*  progressDialog.dismiss();
                Toast.makeText(UserRegister.this, "VerificationComplete" + "\n" + phone_number.getText() + name.getText() + pin.getText(), Toast.LENGTH_SHORT).show();
                // Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;*/

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
// Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(UserRegister.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(UserRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                progressDialog.dismiss();
                // Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(UserRegister.this, "Verification code has been send on your number", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Intent intent = new Intent(UserRegister.this, AccountConfirmation.class);
                intent.putExtra("ID", mVerificationId);
                intent.putExtra("Rule", type);
                intent.putExtra("phone", "+92" + phone_number.getText().toString());
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("pin", pin.getText().toString());
                intent.putExtra("TYPE",type);
                //   intent.putExtra("name", "");
                startActivity(intent);
                UserRegister.this.finish();
            }

        };
        //End of phone number verification
    }


}
