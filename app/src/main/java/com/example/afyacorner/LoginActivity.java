package com.example.afyacorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.afyacorner.Models.User;
import com.example.afyacorner.Cookies.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputphoneNumber, inputPassword;
    private TextView adminLink, notAdminLink, forgetPasswordLink;
    private Button loginBtn;
    private ProgressDialog loadingBar;
    private CheckBox checkBoxRememberMe;
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPassword = findViewById(R.id.login_password_input);
        inputphoneNumber = findViewById(R.id.login_phone_number_input);
        loginBtn = findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);
        forgetPasswordLink = findViewById(R.id.forgotten_password_login);

        checkBoxRememberMe = findViewById(R.id.remember_me_checkbox);
        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });



    }

    private void LoginUser() {
        String phoneNumber = inputphoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (phoneNumber == null || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Please give a Phone number ...", Toast.LENGTH_SHORT).show();
        }else if (password == null || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please give Password ...", Toast.LENGTH_SHORT).show();
        }else{
            // if everyThing is alright
            // display the loading bar
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait cause we are checking your credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phoneNumber, password);

        }
    }

    private void AllowAccessToAccount(String phoneNumber, String password) {

        if (checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey, phoneNumber);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDbName).child(phoneNumber).exists()){
                    // the user can login
                    User userData = snapshot.child(parentDbName).child(phoneNumber).getValue(User.class);
                    if (userData.getPhone().equalsIgnoreCase(phoneNumber)){
                        if (userData.getPassword().equalsIgnoreCase(password)){

                            if (parentDbName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                Prevalent.userGroup = "Admins";
                                startActivity(intent);
                            }else if (parentDbName.equals("Patient")){
                                Toast.makeText(LoginActivity.this, "Logged in Successfully!!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                Prevalent.userGroup = "Users";
                                startActivity(intent);
                            }else if (parentDbName.equals("Doctor")){
                                Toast.makeText(LoginActivity.this, "Logged in Successfully!!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                Prevalent.userGroup = "Users";
                                startActivity(intent);
                            }
                        }else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Wrong Credentials ... Password not correct", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "Credentials errors!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this,
                            "Either you create a new account, either you check your credentials",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}