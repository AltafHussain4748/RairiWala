package com.example.altaf.rairiwala.AccountManagment;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.altaf.rairiwala.Singelton.SaveToken;
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
    String name, phonenumber, pin, rule, type;
    TextView timer;
    Handler handler;
    int count = 120;

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
        type = bundle.getString("TYPE");
        verification = findViewById(R.id.verify);
        timer = findViewById(R.id.timer);
        mAuth = FirebaseAuth.getInstance();
        handler = new Handler(getApplicationContext().getMainLooper());
        verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.getText().toString());
                // [END verify_with_code]
                signInWithPhoneAuthCredential(credential);


            }
        });
        thread();
    }

    private void thread() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (count >= 0) {
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        Log.i("TAG", e.getMessage());
                    }
                    Log.i("TAG", "Thread id in while loop: " + Thread.currentThread().getId() + ", Count : " + count);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            timer.setText("Seconds Left " + count);
                        }
                    });
                    if (count == 0) {
                        Intent intent = new Intent(AccountConfirmation.this, UserRegister.class);
                        intent.putExtra("TYPE", type);
                        startActivity(intent);
                        Toast.makeText(AccountConfirmation.this, "Register again", Toast.LENGTH_SHORT).show();
                    }
                    count--;
                }
            }
        }).start();
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
                            new SaveToken(AccountConfirmation.this).userRegister(name,pin,rule,phonenumber);

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


}
