package com.example.myplacement;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Otp_Validation_Activity extends AppCompatActivity {

    Button btn_otp;
    EditText inputCode1,inputCode2,inputCode3,inputCode4,inputCode5,inputCode6;
    Intent intent;
    String verficationId;
    private OTP_Receiver otp_receiver;

    LottieAnimationView lottieAnimationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);
        intent=getIntent();
        lottieAnimationView = findViewById(R.id.loading);
        TextView mobileContainer=findViewById(R.id.phone_num_container);
        String number=intent.getStringExtra("number");
        verficationId=intent.getStringExtra("verificationId");
        mobileContainer.setText(String.format("+91-%s",number));
        inputCode1=findViewById(R.id.otp_block_1);
        inputCode2=findViewById(R.id.otp_block_2);
        inputCode3=findViewById(R.id.otp_block_3);
        inputCode4=findViewById(R.id.otp_block_4);
        inputCode5=findViewById(R.id.otp_block5);
        inputCode6=findViewById(R.id.otp_block6);
        btn_otp=findViewById(R.id.otp_submit);

        setupOTP();

        ImageView back = findViewById(R.id.backbtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btn_otp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(inputCode1.getText().toString().trim().isEmpty()||inputCode2.getText().toString().trim().isEmpty()||inputCode3.getText().toString().trim().isEmpty()||inputCode4.getText().toString().trim().isEmpty()||inputCode5.getText().toString().trim().isEmpty()||inputCode6.getText().toString().trim().isEmpty()){
                    Toast.makeText(Otp_Validation_Activity.this, "Please Enter Valid Otp", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code=inputCode1.getText().toString()+inputCode2.getText().toString()+inputCode3.getText().toString()+inputCode4.getText().toString()+inputCode5.getText().toString()+inputCode6.getText().toString();
                if(verficationId!=null){
                    btn_otp.setVisibility(View.INVISIBLE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(verficationId,code);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            btn_otp.setVisibility(View.VISIBLE);
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            if(task.isSuccessful()){
                                String number=task.getResult().getUser().getPhoneNumber();
                                String login=number.substring(3);
                                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                                String role=databaseReference.child("users").child(login).child("Role").toString();
                                if(role.equals("Walker")){
                                    Toast.makeText(Otp_Validation_Activity.this, "Walker", Toast.LENGTH_SHORT).show();
                                }
                                Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent1);
                            }else {
                                Toast.makeText(Otp_Validation_Activity.this, "Code Was Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        autoOtpReceiver();
    }

    private void autoOtpReceiver() {
        otp_receiver =new OTP_Receiver();
        this.registerReceiver(otp_receiver,new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
        otp_receiver.initListener(new OTP_Receiver.OtpReceiverListener() {
            @Override
            public void onOtpSuccess(String otp) {
                int o1=Character.getNumericValue(otp.charAt(0));
                int o2=Character.getNumericValue(otp.charAt(1));
                int o3=Character.getNumericValue(otp.charAt(2));
                int o4=Character.getNumericValue(otp.charAt(3));
                int o5=Character.getNumericValue(otp.charAt(4));
                int o6=Character.getNumericValue(otp.charAt(5));

                inputCode1.setText(String.valueOf(o1));
                inputCode2.setText(String.valueOf(o2));
                inputCode3.setText(String.valueOf(o3));
                inputCode4.setText(String.valueOf(o4));
                inputCode5.setText(String.valueOf(o5));
                inputCode6.setText(String.valueOf(o6));
                Log.d("OTP",otp);
            }

            @Override
            public void onOtpTimeout() {
                Toast.makeText(Otp_Validation_Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOTP(){
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (otp_receiver !=null){
            unregisterReceiver(otp_receiver);
        }
    }
}