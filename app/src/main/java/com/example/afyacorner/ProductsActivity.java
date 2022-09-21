package com.example.afyacorner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.example.afyacorner.Home.HomeActivity;
import com.example.afyacorner.Models.Cart;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;
import com.example.afyacorner.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProductsActivity extends AppCompatActivity {
    private RecyclerView productList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListReference;
    private DatabaseReference orderClientRedf;
    private DatabaseReference sellerRefInfo;
    private String totalAmountFromfireBase;


    String userId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        userId = getIntent().getStringExtra("PhoneNumberSelected");

        productList = findViewById(R.id.products_details_List);
        productList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productList.setLayoutManager(layoutManager);

        cartListReference = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(userId)
                .child("Products");

        orderClientRedf = FirebaseDatabase.getInstance().getReference()
                .child("Orders Clients").child(Prevalent.currentOnlineUser.getPhone());

        sellerRefInfo = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListReference.orderByChild("statusCartBuyer").equalTo("Selected"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.cart_items_layout, parent, false);
                        CartViewHolder cartViewHolder = new CartViewHolder(view);
                        return cartViewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Cart model) {
                        holder.txtProductType.setText(model.getProductType());
                        holder.txtProductPrice.setText("Price: "+model.getPrice());
                        holder.txtProductWeight.setText(model.getWeight());
                        holder.txtProductQuantity.setText("Quantity: "+model.getQuantity());

                        String priceString =  model.getPrice();
                        priceString = priceString.replace("TZS", "");
                        priceString = priceString.replace(",", "");
                        totalAmountFromfireBase = priceString;

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                System.out.println("Number : "+getRef(position).getKey());
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(ProductsActivity.this);
                                builder.setTitle("Have You received this Animal?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i==0){
//                                            yes button clicked
                                            String uID = getRef(position).getKey();
                                            UpdateOrderAndMakeItVisibleOnTheSellerSide(uID, model.getProductSellerID(), model.getPrice());
                                        }else{
//                                            non selected
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }
                };
        productList.setAdapter(adapter);
        adapter.startListening();
    }


    private void UpdateOrderAndMakeItVisibleOnTheSellerSide(String uID, String productSellerId, String amountCart) {

        HashMap<String, Object> shipStatus = new HashMap<>();
//        getSeller Amount
        shipStatus.put("state", "YesDrugReceived");
        orderClientRedf.child(uID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProductsActivity.this,
                                    "Activity ended successfully,Thank you.",
                                    Toast.LENGTH_SHORT).show();
                            HashMap<String, Object> orderStatus = new HashMap<>();
                            orderStatus.put("statusCartBuyer", "OrderFinished");
                            cartListReference.child(uID).updateChildren(orderStatus);
                            Intent intent = new Intent(ProductsActivity.this,
                                    HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });

    }
}