package com.example.afyacorner;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.afyacorner.Home.HomeActivity;
import com.example.afyacorner.Home.ProductDetailActivity;
import com.example.afyacorner.Models.Cart;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;
import com.example.afyacorner.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount, msgTextValidationOrder;

    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartList_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = findViewById(R.id.next_process_item);
        txtTotalAmount = findViewById(R.id.totalPrice_cart);
        msgTextValidationOrder = findViewById(R.id.msg1_validation_message_order);

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumberFormat format = NumberFormat.getCurrencyInstance();
                format.setMaximumFractionDigits(0);
                format.setCurrency(Currency.getInstance("TZS"));
                String price = format.format(Integer.parseInt(String.valueOf(overTotalPrice), 10));
                txtTotalAmount.setText("Total Price = "+ price);

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef
                                        .child("User View")
                                        .child(Prevalent.currentOnlineUser.getPhone())
                                        .child("Products"),
                                Cart.class).build();

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
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                        holder.txtProductType.setText(model.getProductType());
                        holder.txtProductPrice.setText("Price: "+model.getPrice());
                        holder.txtProductWeight.setText(model.getWeight());
                        holder.txtProductQuantity.setText("Quantity: "+model.getQuantity());

                        String priceString = model.getPrice();
                        priceString = priceString.replace("TZS", "");
                        priceString = priceString.replace(",", "");
                        int priceFloat = Integer.parseInt(priceString);

                        int oneTypeProductTotalPrice = (Integer.valueOf(priceFloat)) * Integer.valueOf(model.getQuantity());

                        overTotalPrice = overTotalPrice + oneTypeProductTotalPrice;

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{
                                        "Edit",
                                        "Remove"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options: ");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0){
                                            Intent intent = new Intent(CartActivity.this, ProductDetailActivity.class);
                                            intent.putExtra("pid", model.getProdId());
                                            intent.putExtra("OldQuantity", model.getQuantity());
                                            startActivity(intent);
                                        }else if (i == 1){
//                                            remove product in user view data
                                            cartListRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(model.getProdId())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
//                                                                update status of the product in product list
                                                                HashMap<String, Object> productUpdateStatus =
                                                                        new HashMap<>();
                                                                productUpdateStatus.put("selectedStatus", "Not Selected");
                                                                DatabaseReference referenceProduct = FirebaseDatabase.getInstance().getReference().child("Products");
                                                                referenceProduct.child(model.getProdId()).updateChildren(productUpdateStatus);

//                                                              update the status in cart list admin for dashboard graphs
                                                                HashMap<String, Object> productCartAdmin =
                                                                        new HashMap<>();
                                                                productCartAdmin.put("statusCartBuyer", "Removed in Cart");
                                                                DatabaseReference referenceCartAdmin = FirebaseDatabase.getInstance().getReference().child("Cart List");
                                                                referenceCartAdmin.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                                                        .child("Products").child(model.getProdId()).updateChildren(productCartAdmin);


                                                                final DatabaseReference databaseReference =
                                                                        FirebaseDatabase.getInstance().getReference().child("Products");
                                                                databaseReference.child(model.getProdId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        int oldQuantity = Integer.parseInt(snapshot.child("quantity").getValue().toString());
                                                                        int newQuantity = oldQuantity -  Integer.parseInt(model.getQuantity());
                                                                        HashMap<String, Object> newForDataBase = new HashMap<>();
                                                                        newForDataBase.put("quantity", ""+newQuantity);
                                                                        databaseReference.child(model.getQuantity()).updateChildren(newForDataBase);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });


                                                                Toast.makeText(CartActivity.this, "Drug removed", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
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

                    if (shippingStateOfProduct.equals("shipped")){
                        // order confirmed by the admin and display the total amount to the user
//                        NumberFormat format = NumberFormat.getCurrencyInstance();
//                        format.setMaximumFractionDigits(0);
//                        format.setCurrency(Currency.getInstance("TZS"));
//                        String price = format.format(Integer.parseInt(String.valueOf(totalAmount), 10));

                        msgTextValidationOrder.setText("Congratulations your final order has been placed successfully,\n" +
                                "soon your will have to visit The animal seller for Taking it!!! ");

                        txtTotalAmount.setText("Dear "+userName +"\n your order is shipped successfully");
                        recyclerView.setVisibility(View.GONE);

                        msgTextValidationOrder.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this,
                                "You can buy more Animal once you receive the first Order", Toast.LENGTH_SHORT).show();

                    }else if (shippingStateOfProduct.equals("not shipped")){

//                        NumberFormat format = NumberFormat.getCurrencyInstance();
//                        format.setMaximumFractionDigits(0);
//                        format.setCurrency(Currency.getInstance("TZS"));
//                        String price = format.format(Integer.parseInt(String.valueOf(totalAmount), 10));

                        txtTotalAmount.setText(" Order is not shipped ");
                        recyclerView.setVisibility(View.GONE);

                        msgTextValidationOrder.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this,
                                "You can buy more Animal once you receive the first Order",
                                Toast.LENGTH_SHORT).show();

                    }else{
//                        Toast.makeText(CartActivity.this,
//                                " Thank you for choosing us , select any other animal",
//                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                        startActivity(intent);

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