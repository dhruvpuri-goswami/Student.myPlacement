package com.example.myplacement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    EditText email,fullname,mobile,password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullname=findViewById(R.id.full_name);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.phone_num);
        password=findViewById(R.id.password);
        Button login_btn = findViewById(R.id.login_btn);
        sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Button registerBtn=findViewById(R.id.continue_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_text=email.getText().toString();
                String fullName=fullname.getText().toString();
                String mobile_num=mobile.getText().toString();
                String Password=password.getText().toString();
                final String role;

                if(fullName.isEmpty()||email_text.isEmpty()||mobile_num.isEmpty()||Password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please Fill All Blank Details", Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child("student-details").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(mobile_num)){
                                Toast.makeText(RegisterActivity.this, "Phone is already Registred", Toast.LENGTH_SHORT).show();
                            }else{
                                editor.putString("mobile",mobile_num);
                                databaseReference.child("student-details").child(mobile_num).child("Fullname").setValue(fullName);
                                databaseReference.child("student-details").child(mobile_num).child("Email").setValue(email_text);
                                databaseReference.child("student-details").child(mobile_num).child("Password").setValue(Password);
                                Toast.makeText(RegisterActivity.this, "student-details Registred", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RegisterActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}