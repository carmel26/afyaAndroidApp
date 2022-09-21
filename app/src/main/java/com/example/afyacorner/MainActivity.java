package com.example.afyacorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private TextView app_slogan;
    private Button  loginBtn;
    private final String parentDbName = "Users";
    private ProgressDialog loadingBar;
    private TextView sellerBegin, joinNowBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app_slogan = findViewById(R.id.app_slogan);
        app_slogan.setPaintFlags(app_slogan.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        joinNowBtn = findViewById(R.id.main_join_now_btn);
        loginBtn = findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);


        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        String userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.userPasswordKey);

        if (userPhoneKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)) {

                loadingBar.setTitle("Already logged in");
                loadingBar.setMessage("Please wait.....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                AllowAccessToTheUser(userPhoneKey, userPasswordKey);
            }
        }
    }

    private void AllowAccessToTheUser(final String phoneNumber, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phoneNumber).exists()) {
                    // the user can login
                    User userData = snapshot.child(parentDbName).child(phoneNumber).getValue(User.class);
                    if (userData.getPhone().equalsIgnoreCase(phoneNumber)) {
                        if (userData.getPassword().equalsIgnoreCase(password)) {
                            Toast.makeText(MainActivity.this, "Please wait you are already Logged in!!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
//                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                            Prevalent.currentOnlineUser = userData;
//                            startActivity(intent);
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Wrong Credentials ... Password not correct", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Credentials errors!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this,
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