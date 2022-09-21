package com.example.afyacorner;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.afyacorner.MainActivity;
import com.example.afyacorner.AddNewProductActivity;
import com.example.afyacorner.NewOrdersActivity;
import com.example.afyacorner.Home.HomeActivity;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView drug1, drug2, drug3;
    private TextView slogan_category;
    private Button checkNewOrder, logOutAdmin, maintainProductBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        drug2 = findViewById(R.id.drug2_category_admin);
        drug1 = findViewById(R.id.drug1_category_admin);
        drug3 = findViewById(R.id.drug3_category_admin);

        slogan_category = findViewById(R.id.slogan_category);

        checkNewOrder = findViewById(R.id.check_orders_adminCategory_btn);
        logOutAdmin = findViewById(R.id.admin_logout_adminCategory_btn);
        maintainProductBtn = findViewById(R.id.maintain_orders_adminCategory_btn);

        slogan_category.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        if (Prevalent.userGroup.equals("Users")){
            checkNewOrder.setVisibility(View.GONE);
            logOutAdmin.setVisibility(View.GONE);
            maintainProductBtn.setVisibility(View.GONE);
        }

        maintainProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin", Prevalent.userGroup);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        logOutAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, NewOrdersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        drug1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AddNewProductActivity.class);
                intent.putExtra("category", "tablets");
                startActivity(intent);
            }
        });

        drug2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AddNewProductActivity.class);
                intent.putExtra("category", "syringe");
                startActivity(intent);
            }
        });


        drug3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AddNewProductActivity.class);
                intent.putExtra("category", "liquid medicine");
                startActivity(intent);
            }
        });
    }
}