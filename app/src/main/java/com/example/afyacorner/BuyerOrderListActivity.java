package com.example.afyacorner;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afyacorner.AdminCategoryActivity;
import com.example.afyacorner.Home.HomeActivity;
import com.example.afyacorner.Models.AdminOrders;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;

public class BuyerOrderListActivity extends AppCompatActivity {
    private RecyclerView orderList;
    private DatabaseReference ordersReference;
    private String totalAmountFromfireBase = "";
    private String orderRandomKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_order_list);

        ordersReference = FirebaseDatabase.getInstance().getReference()
                .child("Orders Client").child(Prevalent.currentOnlineUser.getPhone());
        orderList = findViewById(R.id.orders_Buyer_List);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersReference, AdminOrders.class)
                        .build();


        FirebaseRecyclerAdapter<AdminOrders, BuyerOrderListActivity.AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, BuyerOrderListActivity.AdminOrdersViewHolder>(options) {
                    @NonNull
                    @Override
                    public BuyerOrderListActivity.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.orders_layout, parent, false);
                        return new BuyerOrderListActivity.AdminOrdersViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull BuyerOrderListActivity.AdminOrdersViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull AdminOrders model) {
                        holder.username.setText("Name: "+model.getName());
                        holder.phone.setText("Phone: "+model.getPhone());

                        NumberFormat format = NumberFormat.getCurrencyInstance();
                        format.setMaximumFractionDigits(0);
                        format.setCurrency(Currency.getInstance("TZS"));
                        String price = format.format(Integer.parseInt(model.getTotalAmount(), 10));
                        totalAmountFromfireBase = model.getTotalAmount();
                        holder.totalPrice.setText("Total Amount: "+price);
                        holder.userShippingAddress.setText("Address: "+model.getAddress());
                        holder.city_order.setText("City: "+model.getCity());
                        holder.userDateTime.setText("Order at : "+model.getDate()+ " "+model.getTime());

                        holder.showOrdersButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                Because Admin can select any order in the list, that is why we use the object getRef
//                                with position for to get the position selected and the key for the object selectect
//                                will be the phone number, cause the after orders, we have phoneNumber and all the content

                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(BuyerOrderListActivity.this, ProductsActivity.class);
                                intent.putExtra("PhoneNumberSelected", Prevalent.currentOnlineUser.getPhone());
                                startActivity(intent);
                            }
                        });

//
//                            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    CharSequence options[] = new CharSequence[]
//                                            {
//                                                    "Yes",
//                                                    "No"
//                                            };
//
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(BuyerOrderListActivity.this);
//                                    builder.setTitle("Have You received this Order?");
//
//                                    builder.setItems(options, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            if (i==0){
////                                            yes button clicked
//                                                String uID = getRef(position).getKey();
//                                                UpdateOrderAndMakeItVisibleOnTheSellerSide(uID);
//                                            }else{
////                                            non selected
//                                            }
//                                        }
//                                    });
//                                    builder.show();
//                                }
//                            });

                    }
                } ;
        orderList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView username, phone, totalPrice, userDateTime, userShippingAddress,city_order;
        public Button showOrdersButton;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name_order);
            phone = itemView.findViewById(R.id.phone_number_order);
            totalPrice = itemView.findViewById(R.id.total_price_order);
            userDateTime = itemView.findViewById(R.id.date_time_order);
            userShippingAddress = itemView.findViewById(R.id.address_order);
            city_order = itemView.findViewById(R.id.city_order);

            showOrdersButton = itemView.findViewById(R.id.show_all_products_order);
        }
    }

    private void UpdateOrderAndMakeItVisibleOnTheSellerSide(String uID) {
//        System.out.println(" UID "+uID);
        Log.i("UId data", uID);
        HashMap<String, Object> shipStatus = new HashMap<>();
        shipStatus.put("state", "Drug received");
        ordersReference.child(uID).updateChildren(shipStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(BuyerOrderListActivity.this,
                                    "Thank you for your confirmations... Enjoy your Shopping ",
                                    Toast.LENGTH_SHORT).show();
                            final DatabaseReference userAmountReference = FirebaseDatabase.getInstance().getReference();
                            HashMap<String, Object> shipStatus = new HashMap<>();
                            shipStatus.put("cash", ""+totalAmountFromfireBase);
                            userAmountReference.child("Users").child(Prevalent.currentOnlineUser.getPhone()).updateChildren(shipStatus);

                            Intent intent = new Intent(BuyerOrderListActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                });

    }
}