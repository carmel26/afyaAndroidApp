package com.example.afyacorner.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.afyacorner.Models.Product;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    private Button addToCart;
    private ImageView productImage;
    private TextView productPrice,productDescription,productType,
            productWeight;
    private ElegantNumberButton numberButton;
    private String productID = "",
            productSellerID = "",
            productSellerName = "",
            state = "Normal";

    private String oldQuantity = "0";


    private static String PRODUCT_REF = "Products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

//        getting product id send by onbindviewHolder in homeActivity
        productID = getIntent().getStringExtra("pid");
        productSellerID = getIntent().getStringExtra("sellerId");
        productSellerName = getIntent().getStringExtra("sellerName");
        oldQuantity = getIntent().getStringExtra("OldQuantity");

        numberButton = findViewById(R.id.product_number_detail);
        productImage = findViewById(R.id.product_image_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);
        productWeight = findViewById(R.id.product_weight_details);
        productType = findViewById(R.id.product_type_details);
        addToCart = findViewById(R.id.pd_add_to_cart_detail_product);



        getProductDetails(productID);


        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberButton.getNumber().equalsIgnoreCase("0")){
                    Toast.makeText(ProductDetailActivity.this,
                            "Please provide a quantity greater than 0",
                            Toast.LENGTH_SHORT).show();
                }else if (state.equals("Order Placed") || state.equals("Order Shipped")){
                    Toast.makeText(ProductDetailActivity.this,
                            "You can buy others Drugs when your current order is shipped or Confirmed",
                            Toast.LENGTH_SHORT).show();
                }else {
                    addingToCartList();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        checkOrderState();

    }

    // add to cart function
    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendarForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance()
                .getReference().child("Cart List");

        final HashMap<String, Object>  cartMap = new HashMap<>();
        cartMap.put("prodId", productID);
        cartMap.put("productType", productType.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("description", productDescription.getText().toString());
        cartMap.put("weight", productWeight.getText().toString());
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");
        cartMap.put("productSellerID", productSellerID);
        cartMap.put("productSellerName", productSellerName);

        cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
//                            adding statusCartBuyer for helping next data analysis if the product was removed or not
                            cartMap.put("statusCartBuyer", "Selected");
                            cartListRef.child("Admin View")
                                    .child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                                    .child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
//                                                changing status of the product
                                                HashMap<String, Object> productUpdateStatus =
                                                        new HashMap<>();
                                                productUpdateStatus.put("selectedStatus", "Selected");

                                                DatabaseReference referenceProduct = FirebaseDatabase.getInstance().getReference().child("Products");
                                                referenceProduct.child(productID).updateChildren(productUpdateStatus)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                final DatabaseReference databaseReference =
                                                                        FirebaseDatabase.getInstance().getReference().child("Products");
                                                                databaseReference.child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        int oldQuantityFirebase = Integer.parseInt(snapshot.child("quantity").getValue().toString());
                                                                        int oldQuantityCart = 0;
                                                                        if (oldQuantity != null){
                                                                            if (!oldQuantity.equals("")) {
                                                                                oldQuantityCart = Integer.parseInt(oldQuantity);
                                                                            }
                                                                        }
                                                                        int difference =  Integer.parseInt(numberButton.getNumber()) - oldQuantityCart;
                                                                        int newQuantity = oldQuantityFirebase -  (difference);
                                                                        HashMap<String, Object> newForDataBase = new HashMap<>();
                                                                        newForDataBase.put("quantity", ""+newQuantity);
                                                                        databaseReference.child(productID).updateChildren(newForDataBase);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });

                                                                Toast.makeText(ProductDetailActivity.this, "Added to the cart list", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });

                                            }else{
                                                Toast.makeText(ProductDetailActivity.this, "Error adding to the admin view", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(ProductDetailActivity.this, "Error adding to user view", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }



    private void getProductDetails(String productID) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
                .child(PRODUCT_REF);

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Product product = snapshot.getValue(Product.class);

                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    format.setMaximumFractionDigits(0);
                    format.setCurrency(Currency.getInstance("TZS"));
                    String price = format.format(Integer.parseInt(product.getPrice(), 10));

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    String weight = df.format(Integer.parseInt(product.getQuantity(), 10));

                    productType.setText(product.getType());
                    productDescription.setText(product.getDescription());
                    productWeight.setText(weight+" ");
                    productPrice.setText(price);
//                    numberButton.setNumber(product.getQuantity());
                    Picasso.get().load(product.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void checkOrderState() {
        DatabaseReference orderReference;

        orderReference = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderData = rootRef.child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    String userName ="";
                    String totalAmount = "";
                    String shippingStateOfProduct = "";
                    for (DataSnapshot orderData : userSnapshot.getChildren()){ // 👈 add an extra loop

                        if (orderData.getKey().equals("name")){
                            userName = orderData.getValue().toString();
                        }

                        if (orderData.getKey().equals("totalAmount")){
                            totalAmount = orderData.getValue().toString();
                        }

                        if (orderData.getKey().equals("state")){
                            shippingStateOfProduct = orderData.getValue().toString();
                        }

                    }

                    if (shippingStateOfProduct.equals("Shipped")){
                        state = "Order Shipped";
                    }else if (shippingStateOfProduct.equals("Not Shipped")){
                        state = "Order Placed";
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(" Error ", databaseError.getMessage()); //Log errors
            }
        };
        orderData.addValueEventListener(valueEventListener);

//        orderReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//
//                    String stest = snapshot.getValue().toString();
//                    stest = stest.substring(stest.indexOf("{") + 1);
//                    stest = stest.substring(0, stest.indexOf(" ="));
//

    }

}