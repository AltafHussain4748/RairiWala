package com.example.altaf.rairiwala.AccountManagment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.altaf.rairiwala.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;

public class VerifyCodeFragment extends Fragment {
    View view;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    Button verification;
    EditText otp;
    TextView timer;
    Handler handler;
    int count = 120;
    String phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.verify_code_for_reset_password, container, false);
        otp = view.findViewById(R.id.otp);

        final String verificationId = getArguments().getString("ID");
        phoneNumber = getArguments().getString("PHONE");

        verification = view.findViewById(R.id.verify);
        timer = view.findViewById(R.id.timer);
        mAuth = FirebaseAuth.getInstance();
        handler = new Handler(getActivity().getMainLooper());
        verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.getText().toString());
                // [END verify_with_code]
                signInWithPhoneAuthCredential(credential);


            }
        });
        thread();
        return view;
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
                        FragmentManager fm = getFragmentManager();
                        android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, new CodeSendingResetPassword());
                        fragmentTransaction.commit(); // save the changes
                    }
                    count--;
                }
            }
        }).start();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Enter Verification Code");
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Log.d(TAG, "signInWithCredential:success");
                            Intent intent = new Intent(getActivity(), UpdatePassword.class);
                            intent.putExtra("PHONE", phoneNumber);
                            startActivity(intent);
                            Toast.makeText(getActivity(), "Verification Done", Toast.LENGTH_SHORT).show();

                            // ...
                        } else {
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getActivity(), " Invalid Verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
