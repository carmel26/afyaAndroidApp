package com.example.afyacorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.afyacorner.Home.HomeActivity;
import com.example.afyacorner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductActivity extends AppCompatActivity {

    private Button applyChangesBtn, deleteBtn, approveBtn;
    private EditText typeDrug, priceDrug, weigthDrug,descriptionDrug;
    private ImageView drugImage;

    private String productId = "";
    private DatabaseReference productReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);

        productId = getIntent().getStringExtra("pid");
        productReference = FirebaseDatabase.getInstance().getReference()
                .child("Products")
                .child(productId);

        applyChangesBtn = findViewById(R.id.maintain_product_btn_apply_changes);

        typeDrug = findViewById(R.id.maintain_product_type_view);
        priceDrug = findViewById(R.id.maintain_product_price);
        weigthDrug = findViewById(R.id.maintain_product_weight);
        drugImage = findViewById(R.id.maintain_product_image);
        descriptionDrug = findViewById(R.id.maintain_product_description);
        deleteBtn = findViewById(R.id.maintain_product_btn_delete);
        approveBtn = findViewById(R.id.maintain_product_btn_approve_product);

        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteThisProduct();
            }
        });

        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aprroveDrug();
            }
        });

    }



    private void aprroveDrug() {

        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("productStatus", "Approved");

        productReference.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AdminMaintainProductActivity.this,
                            "Approved Successfully", Toast.LENGTH_SHORT)
                            .show();
                    Intent intent = new Intent(
                            AdminMaintainProductActivity.this,
                            HomeActivity.class
                    );
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(AdminMaintainProductActivity.this,
                            "Error ", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }


    private void deleteThisProduct() {
        CharSequence options[] = new CharSequence[]
                {
                        "Yes.",
                        "No, Cancel"
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProductActivity.this);
        builder.setTitle("Do you really need to delete this product?");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
//                                            yes button clicked
                    productReference.removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(AdminMaintainProductActivity.this,
                                            "The Drug has deleted successFully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(
                                            AdminMaintainProductActivity.this, AdminCategoryActivity.class
                                    );
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }else{
//                                            non selected
                    finish();
                }
            }
        });
        builder.show();


    }


    private void applyChanges() {
        String typeProduct = typeDrug.getText().toString();
        String description = descriptionDrug.getText().toString();
        String price = priceDrug.getText().toString();
        String weight = weigthDrug.getText().toString();
//        String image = .getText().toString();

        if (typeProduct.equals("")){
            Toast.makeText(this, "Please write a Name of this Drug", Toast.LENGTH_SHORT).show();
        }else if (description.equals("")){
            Toast.makeText(this, "Please write a Description of this Drug", Toast.LENGTH_SHORT).show();
        }else if (price.equals("")){
            Toast.makeText(this, "Please write a Price of this Drug", Toast.LENGTH_SHORT).show();
        }else if (weight.equals("")){
            Toast.makeText(this, "Please write a Weigth of this Drug", Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("prodId", productId);
            productMap.put("description", description);
//            productMap.put("image", animalImage);
            productMap.put("Quantity", weight);
            productMap.put("price", price);
            productMap.put("type", typeProduct.toUpperCase());

            productReference.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductActivity.this,
                                "Changes applied Successfully", Toast.LENGTH_SHORT)
                                .show();
                        Intent intent = new Intent(
                                AdminMaintainProductActivity.this,
                                AdminCategoryActivity.class
                        );
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AdminMaintainProductActivity.this,
                                "Error ", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
    }


    private void displaySpecificProductInfo(){
        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String productType = snapshot.child("type").getValue().toString();
                    String productDescription = snapshot.child("description").getValue().toString();
                    String productWeight = snapshot.child("weight").getValue().toString();
                    String productPrice = snapshot.child("price").getValue().toString();
                    String productImage = snapshot.child("image").getValue().toString();

                    typeDrug.setText(productType);
                    priceDrug.setText(productPrice);
                    weigthDrug.setText(productWeight);
                    descriptionDrug.setText(productDescription);

                    if (productImage.equals("") || productImage == null){
                        Picasso.get().load(R.drawable.select_product_image).into(drugImage);
                    }else{
                        Picasso.get().load(productImage).placeholder(R.drawable.select_product_image).into(drugImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}