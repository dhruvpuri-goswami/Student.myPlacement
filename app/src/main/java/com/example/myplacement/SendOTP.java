package com.example.myplacement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SendOTP extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        EditText inputMobile = findViewById(R.id.mobile);
        LottieAnimationView lottieAnimationView = findViewById(R.id.loading);
        Button submit = findViewById(R.id.buttonOfOTP);
        ImageView back = findViewById(R.id.backbtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputMobile.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SendOTP.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the number exists in the database
                    databaseReference.child("student-details").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(inputMobile.getText().toString())) {
                                // Number exists, proceed to send OTP
                                sendOtp(inputMobile.getText().toString());
                            } else {
                                // Number does not exist, redirect to registration
                                Toast.makeText(SendOTP.this, "Please register first!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SendOTP.this, RegisterActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SendOTP.this, "Error checking number: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void sendOtp(String number) {
        LottieAnimationView lottieAnimationView = findViewById(R.id.loading);
        Button submit = findViewById(R.id.buttonOfOTP);
        EditText inputMobile = findViewById(R.id.mobile);

        submit.setVisibility(View.INVISIBLE);
        lottieAnimationView.setVisibility(View.VISIBLE);
        inputMobile.setEnabled(false);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number,
                60,
                TimeUnit.SECONDS,
                SendOTP.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        submit.setVisibility(View.VISIBLE);
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        submit.setVisibility(View.VISIBLE);
                        inputMobile.setEnabled(true);
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                        Log.d("error", e.getMessage());
                        Toast.makeText(SendOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        submit.setVisibility(View.VISIBLE);
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplicationContext(), Otp_Validation_Activity.class);
                        intent.putExtra("number", number);
                        intent.putExtra("verificationId", s);
                        startActivity(intent);
                    }
                }
        );
    }
}
