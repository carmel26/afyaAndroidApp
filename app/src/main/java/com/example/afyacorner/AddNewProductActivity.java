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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.Home.HomeActivity;
import com.example.afyacorner.AdminCategoryActivity;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewProductActivity extends AppCompatActivity {
    private String categoryName, productDescription, productPrice,
            productWeight, productType, productDistrict, saveCurrentDate,
            saveCurrentTime;

    private Button addNewProductButton;
    private EditText inputProductType,inputProductWeight,
            inputProductPrice,inputProductDescription;
    private Spinner inputProductDistrict;
    private ImageView inputProductImage;
    private static final int GalleryPic =1;
    private Uri imageUri;

    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImageReference;
    private DatabaseReference productsRef;


    private static String IMAGESTORAGEPATH = "Products images", PRODUCTPATH="Products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

//        adding district
        String[] arraySpinner = new String[] {
                "district 1", "district 2", "district 3", "district 4", "district 5"
        };
        Spinner s = (Spinner) findViewById(R.id.district_select);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
//        end add district

        categoryName = getIntent().getExtras().get("category").toString();
        productImageReference = FirebaseStorage.getInstance().getReference().child(IMAGESTORAGEPATH);
        productsRef = FirebaseDatabase.getInstance().getReference().child(PRODUCTPATH);
        loadingBar = new ProgressDialog(this);

        addNewProductButton = findViewById(R.id.add_new_product);
        inputProductImage = findViewById(R.id.select_product_image);
        inputProductType = findViewById(R.id.product_type);
        inputProductPrice = findViewById(R.id.product_price);
        inputProductWeight = findViewById(R.id.product_weight);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductDistrict = findViewById(R.id.district_select);

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

    }

    private void ValidateProductData() {
        productDescription = inputProductDescription.getText().toString();
        productWeight = inputProductWeight.getText().toString();
        productType = inputProductType.getText().toString();
        productDistrict = inputProductDistrict.getSelectedItem().toString();
        productPrice = inputProductPrice.getText().toString();

        if (imageUri == null){
            Toast.makeText(this, "Product Image is Mandatory...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productDescription)){
            Toast.makeText(this, "Please product description is Mandatory...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productWeight)){
            Toast.makeText(this, "Please product weight is Mandatory...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productPrice)){
            Toast.makeText(this, "Please product price is Mandatory...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(productDistrict)){
            Toast.makeText(this, "Please product district is Mandatory...", Toast.LENGTH_SHORT).show();
        }else{
            StoreImageInformation();
        }
    }

    private void StoreImageInformation() {

        loadingBar.setTitle("Add new Animal");
        loadingBar.setMessage("Please wait until we are adding new Animal...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss ");
        saveCurrentTime =  currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = productImageReference
                .child(imageUri.getLastPathSegment()+productRandomKey+".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddNewProductActivity.this, "Product Image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();

                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AddNewProductActivity.this,
                                    "Got the Product image Successfully", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToTheDataBase();
                        }
                    }
                });
            }
        });


    }

    private void SaveProductInfoToTheDataBase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("prodId", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", productDescription);
        productMap.put("district", productDistrict);
        productMap.put("image", downloadImageUrl);
        productMap.put("weight", productWeight);
        productMap.put("category", categoryName);
        productMap.put("price", productPrice);
        productMap.put("selectedStatus", "Not Selected");
        productMap.put("type", productType.toUpperCase());
        productMap.put("productStatus", "Not Approved");
        productMap.put("sellerName", Prevalent.currentOnlineUser.getName());
        productMap.put("sellerEmail", Prevalent.currentOnlineUser.getEmail());
        productMap.put("sellerId", Prevalent.currentOnlineUser.getPhone());

        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(AddNewProductActivity.this, HomeActivity.class) ;
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(AddNewProductActivity.this, "Product is Added Successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPic && resultCode== RESULT_OK && data != null) {
            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);
        }
    }
}