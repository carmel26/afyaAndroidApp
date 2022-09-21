package com.example.afyacorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.rey.material.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountBtn;
    private EditText inputName, inputPhoneNumber,addressInput,emailInput,
            inputPassword, inputValidatePassword;
    private CheckBox sellerOrBuyer;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //        adding district
        String[] arraySpinner = new String[] {
                "patient", "doctor",  "admin"
        };
        Spinner s = (Spinner) findViewById(R.id.district_select);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        createAccountBtn = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_username_input);
        inputPhoneNumber = findViewById(R.id.register_phone_number_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputValidatePassword = findViewById(R.id.register_validate_password_input);
        emailInput = findViewById(R.id.register_email_input);
        addressInput = findViewById(R.id.register_Address_input);

        loadingBar = new ProgressDialog(this);


        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!inputValidatePassword.getText().toString().equalsIgnoreCase(inputPassword.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Please provide similar password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!inputValidatePassword.getText().toString().equals(inputPassword.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Please provide similar password", Toast.LENGTH_SHORT).show();
                    return;
                }
                createAccountFunction();
            }
        });
    }

    private void createAccountFunction() {
        String phoneNumber = inputPhoneNumber.getText().toString();
        String name = inputName.getText().toString();
        String password = inputPassword.getText().toString();
        String address = addressInput.getText().toString();
        String email = emailInput.getText().toString();
        String sellerOrNot = "";

        if (name == null || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please give a name ...", Toast.LENGTH_SHORT).show();
        }else if (phoneNumber == null || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Please give an Email ...", Toast.LENGTH_SHORT).show();
        }else if (password == null || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please give Password ...", Toast.LENGTH_SHORT).show();
        }else if (email == null || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please provide your Email ...", Toast.LENGTH_SHORT).show();
        }else{
            // if everyThing is alright
            // display the loading bar
            loadingBar.setTitle("Creation of new Account");
            loadingBar.setMessage("Please wait cause we are creating your Account.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(name, phoneNumber, password, email, address, sellerOrNot);
        }
    }

    private void validatePhoneNumber(String nameSave, String phoneNumberSave, String passwordSave, String emailSave, String addressSave, String SellerOrNotSave) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users")).child(phoneNumberSave).exists()) {
                    // creation of the user
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phoneNumberSave);
                    userdataMap.put("password", passwordSave);
                    userdataMap.put("name", nameSave);
                    userdataMap.put("email", emailSave);
                    userdataMap.put("address", addressSave);
                    userdataMap.put("typeUser", SellerOrNotSave);
                    userdataMap.put("dataOperation", timeStamp);
                    userdataMap.put("statusOfUser", "active");

                    rootRef.child("Users").child(phoneNumberSave).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Congratulations, Your account was successfully created!", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }else{
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network error please try again...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(RegisterActivity.this, "This "+phoneNumberSave+ "Already Exist... please provide another", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}