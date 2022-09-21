package com.example.afyacorner;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.afyacorner.Adapters.OrdersAdapter;
import com.example.afyacorner.Models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NewOrdersActivity extends AppCompatActivity {
    private TextView textView;
    private RecyclerView orderList;
    private DatabaseReference ordersReference;
    private RecyclerView recyclerView;
    private String phoneNumber = "";
    private OrdersAdapter ordersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

//         phoneNumber = getIntent().getStringExtra("PhoneNumber");
        ordersReference = FirebaseDatabase.getInstance().getReference()
                .child("Orders");
        orderList = findViewById(R.id.orders_List);
        orderList.setLayoutManager(new LinearLayoutManager(this));



        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersReference, AdminOrders.class)
                        .build();

        ordersAdapter = new OrdersAdapter(options, NewOrdersActivity.this);
        orderList.swapAdapter(ordersAdapter, false);

    }


    @Override
    protected void onStart() {
        super.onStart();
        ordersAdapter.startListening();

    }


    @Override
    protected void onStop() {
        super.onStop();
//        adapter.stopListening();
        ordersAdapter.startListening();
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

}