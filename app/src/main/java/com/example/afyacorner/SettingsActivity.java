package com.example.afyacorner;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afyacorner.Home.HomeActivity;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTxtBtn;
    private Button securityQuestionBtn;

    private static String USER_REF = "Users";
    private static String USER_PROFILE_PIC = "Profile pictures";

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference  storageProfilePictureRef;
    private String checker = "";
    private StorageTask uploadTask;

    private TextView amount_account_settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageView = findViewById(R.id.settings_profile_image);
        fullNameEditText = findViewById(R.id.settings_change_full_name);
        userPhoneEditText = findViewById(R.id.settings_change_phone_number);
        addressEditText = findViewById(R.id.settings_change_address);

        amount_account_settings = findViewById(R.id.amount_account_settings);

        profileChangeTextBtn = findViewById(R.id.profile_image_change);
        closeTextBtn = findViewById(R.id.close_account_settings);
        saveTxtBtn = findViewById(R.id.update_account_settings);
        securityQuestionBtn = findViewById(R.id.settings_security_change_question_Btn);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText,amount_account_settings);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child(USER_PROFILE_PIC);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")) {
                    UserInfoSaved();
                }else{
                    UpdateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);

            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);

            }
        });

//        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
//                intent.putExtra("check", "settings");
//                startActivity(intent);
//            }
//        });
    }


    //    for user info without a pic
    private void UpdateOnlyUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(USER_REF);

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("phone", userPhoneEditText.getText().toString());
//                        userMap.put("password", passwordEditText.getText().toString());
//                        userMap.put("name", fullNameEditText.getText().toString());

        reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Account informations Update Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }


    //    for geting image and print it to the USER
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }else{
            Toast.makeText(SettingsActivity.this, "Error Try again please", Toast.LENGTH_SHORT).show();
            startActivity(new Intent( SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }



    //    save other user informations
    private void UserInfoSaved() {

        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Please provide a name...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Please provide an address...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Please Phone number is required...", Toast.LENGTH_SHORT).show();
        }else if (checker.equals("clicked")){
            UploadUserImage();
        }
//      else if (TextUtils.isEmpty(passwordEditText.getText().toString())){
//          Toast.makeText(this, "Please Password is mandatory...", Toast.LENGTH_SHORT).show();
//      }
    }


    private void UploadUserImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait Until we finalize updating your information ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileReference = storageProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }) .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(USER_REF);

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullNameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("phone", userPhoneEditText.getText().toString());
                        userMap.put("image", myUrl);
//                        userMap.put("password", passwordEditText.getText().toString());
//                        userMap.put("name", fullNameEditText.getText().toString());

//                        updating data in the database
                        reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Account informations Update Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(SettingsActivity.this, "Error during updating ...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }else{
            Toast.makeText(SettingsActivity.this, "Image not selected ...", Toast.LENGTH_SHORT).show();
        }
    }


    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditText, EditText userPhoneEditText, EditText addressEditText, TextView amount_account_settings) {

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(USER_REF).child(Prevalent.currentOnlineUser.getPhone());
        System.out.println(userReference);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String password = snapshot.child("password").getValue().toString();
                        String amountCompte = snapshot.child("cash").getValue().toString();

                        if (snapshot.child("address").exists()){
                            String address = snapshot.child("address").getValue().toString();
                            addressEditText.setText(address);
                        }

//                        String status = snapshot.child("status").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        amount_account_settings.setText(ChangeCurrent(amountCompte));

                    }else{
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String password = snapshot.child("password").getValue().toString();
//                        String amountCompte = snapshot.child("cash").getValue().toString();

//                        String status = snapshot.child("status").getValue().toString();
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
//                        amount_account_settings.setText(ChangeCurrent(amountCompte));

                        if (snapshot.child("address").exists()){
                            String address = snapshot.child("address").getValue().toString();
                            addressEditText.setText(address);
                        }
                    }
                }else {
                    System.out.println("No data existing ... ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String ChangeCurrent(String amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("TZS"));
        String price = format.format(Integer.parseInt(amount, 10));
        return price;
    }
}