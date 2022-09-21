package com.example.afyacorner;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.Home.HomeActivity;
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

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;

    private String totalAmount = "";
    private String amountCashAccount = "0";
    private String orderRandomKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss ");
        String saveCurrentTime =  currentTime.format(calendar.getTime());

        orderRandomKey = saveCurrentDate + saveCurrentTime;


        nameEditText = findViewById(R.id.shipment_name);
        addressEditText = findViewById(R.id.shipment_home_address);
        cityEditText = findViewById(R.id.shipment_city_name);
        phoneEditText = findViewById(R.id.shipment_phone_number);
        confirmOrderBtn = findViewById(R.id.btn_confirm_final_shipment);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = "+totalAmount, Toast.LENGTH_SHORT).show();

        DatabaseReference getUSerAmountRef = FirebaseDatabase.getInstance().getReference().child("Users");
        getUSerAmountRef.child(Prevalent.currentOnlineUser.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        amountCashAccount = snapshot.child("cash").getValue().toString();
//                        Log.i("Data get Inside ", snapshot.child("cash").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInput();
            }
        });
    }


    private void checkInput() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Please provide your full name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Please provide your phone number", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Please provide your address", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(this, "Please provide your City", Toast.LENGTH_SHORT).show();
        }else{
            ConfirmOrder();
        }
    }


    private void ConfirmOrder() {
        final String saveCurrentTime, saveCurrentDate;

        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendarForDate.getTime());

//        check cash in user wallet account ...

        int amountUserWallet = Integer.parseInt(amountCashAccount);
        int totalAmountOrder = Integer.parseInt(totalAmount);
        int restInAccount = amountUserWallet-totalAmountOrder;
//        if (amountUserWallet < totalAmountOrder){
//            Toast.makeText(ConfirmFinalOrderActivity.this,
//                    "You Don't have enough money for Continue the operation, " +
//                            "Please Contact the admin for to add Money in" +
//                            " your account before continuing this purchasing",
//                    Toast.LENGTH_LONG).show();
//            Toast.makeText(ConfirmFinalOrderActivity.this,
//                    "YOU HAVE: " +amountUserWallet+
//                            " BUT YOU NEED A MINIMUM OF: " +totalAmountOrder+
//                            " ON YOUR ACCOUNT ",
//                    Toast.LENGTH_LONG).show();
//        }else {
//        continue the operation ...
            final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                    .child("Orders")
                    .child(Prevalent.currentOnlineUser.getPhone()) ;

            HashMap<String, Object> orderMap = new HashMap<>();
            orderMap.put("totalAmount", totalAmount);
            orderMap.put("name", nameEditText.getText().toString());
            orderMap.put("address", addressEditText.getText().toString());
            orderMap.put("phone", phoneEditText.getText().toString());
            orderMap.put("city", cityEditText.getText().toString());
            orderMap.put("date", saveCurrentDate);
            orderMap.put("time", saveCurrentTime);
            orderMap.put("state", "not shipped");
            orderMap.put("orderId", orderRandomKey);

            final DatabaseReference orderRefClient = FirebaseDatabase.getInstance().getReference()
                    .child("Orders Client")
                    .child(Prevalent.currentOnlineUser.getPhone())
                    .child(orderRandomKey);

            HashMap<String, Object> orderClientMap = new HashMap<>();
            orderClientMap.put("totalAmount", totalAmount);
            orderClientMap.put("name", nameEditText.getText().toString());
            orderClientMap.put("address", addressEditText.getText().toString());
            orderClientMap.put("phone", phoneEditText.getText().toString());
            orderClientMap.put("city", cityEditText.getText().toString());
            orderClientMap.put("date", saveCurrentDate);
            orderClientMap.put("time", saveCurrentTime);
            orderClientMap.put("state", "not shipped");
            orderClientMap.put("orderId", orderRandomKey);

//                    get Card seller Info
            final DatabaseReference orderCardDetail = FirebaseDatabase.getInstance().getReference()
                    .child("Cart List")
                    .child("User View")
                    .child(Prevalent.currentOnlineUser.getPhone())
                    .child("Products");
            orderCardDetail.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String prodId = snapshot.getKey().toString();
                    System.out.println("ProdId "+prodId +snapshot.hasChildren());
                    System.out.println("Quantity "+snapshot.child("quantity"));
                    System.out.println("child(prodId).child(\"quantity\") "+snapshot.child(prodId).child("quantity").getValue());
                    System.out.println("child(prodId) "+snapshot.child(prodId));
                    System.out.println("snapshot getValue "+snapshot.getValue());
                    System.out.println("snapshot "+snapshot.child("Products"));
                    System.out.println("snapshot child(prodId) "+snapshot.child(prodId).child("Products").child("quantity"));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//        System.out.println("orderMap "+orderMap);
            orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        FirebaseDatabase.getInstance().getReference()
                                .child("Cart List")
                                .child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                                    Toast.makeText(ConfirmFinalOrderActivity.this,
                                                            "Your final order have been placed successfully!",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                    }
                                });
                    }
                }
            });


//        }


    }
}