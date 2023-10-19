package com.example.myplacement;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class LoginActivity extends AppCompatActivity {
    Button register,forget;
    EditText password;
    ImageView showHide;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    Button login_btn;
    EditText email;
    Button phone_btn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!isFinishing())
            checkLocationPermission();
        password=findViewById(R.id.password);
        email=findViewById(R.id.email);
        showHide=findViewById(R.id.showhide);
        phone_btn=findViewById(R.id.phone_btn);
        Button register=findViewById(R.id.register_btn);
        login_btn=findViewById(R.id.login_btn);
        sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
        CheckLogin();
        editor=sharedPreferences.edit();
        Button forget=findViewById(R.id.forget_pass);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,Forgot_Password_Activity.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile_num=email.getText().toString();
                String Password=password.getText().toString();
                if (mobile_num.isEmpty() ||Password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Fill Blank Details", Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child("student-details").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(mobile_num)) {
                                String getPassword = snapshot.child(mobile_num).child("Password").getValue().toString();
                                if (Password.equals(getPassword)) {
                                    editor.putString("mobile", mobile_num);
                                    editor.commit();
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,SendOTP.class);
                startActivity(intent);
            }
        });

    }
    public void ShowHide(View view) {
        if(password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            showHide.setImageResource(R.drawable.lock_item_gray);
        }else{
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showHide.setImageResource(R.drawable.eye_for_toggle);
        }
    }
    public void CheckLogin(){
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        if(sharedPreferences.contains("mobile")) {
            String mobileNumber = sharedPreferences.getString("mobile", "");
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 99: {
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                    }
                }
            }
        }
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(getApplicationContext()).setTitle("Location Permission Needed").setMessage("Location Permission needed").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},99);
                            }
                        })
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},99);
            }
            return false;
        }else{
            return true;
        }
    }
}