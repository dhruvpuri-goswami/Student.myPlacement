package com.example.myplacement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Forgot_Password_Activity extends AppCompatActivity {

    Button fsubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        fsubmit=findViewById(R.id.forget_submit);
        fsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Forgot_Password_Activity.this,Otp_Validation_Activity.class);
                startActivity(intent);
            }
        });

    }
}