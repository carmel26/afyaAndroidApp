package com.example.afyacorner.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afyacorner.AdminCategoryActivity;
import com.example.afyacorner.ProductsActivity;
import com.example.afyacorner.Models.AdminOrders;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrdersAdapter extends FirebaseRecyclerAdapter<AdminOrders, OrdersAdapter.myViewHolder> {
    private Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public OrdersAdapter(@NonNull FirebaseRecyclerOptions<AdminOrders> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull AdminOrders model) {

        System.out.println("Data "+model.getName());
        System.out.println("Data "+position);

        holder.orderId.setText(model.getOrderId());

        holder.name.setText("Name: "+model.getName());
        holder.phone.setText("Phone: "+model.getPhone());
//        NumberFormat format = NumberFormat.getCurrencyInstance();
//        format.setMaximumFractionDigits(0);
//        format.setCurrency(Currency.getInstance("TZS"));
//        String price = format.format(Integer.parseInt(model.getTotalAmount(), 10));
        String price =model.getTotalAmount();
        holder.totalAmount.setText("Total Amount: "+price);
        holder.address.setText("Address: "+model.getAddress());
        holder.city.setText("City: "+model.getCity());
//        holder.userDateTime.setText("Order at : "+model.getDate()+ " "+model.getTime());

        holder.showOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                                Because Admin can select any order in the list, that is why we use the object getRef
//                                with position for to get the position selected and the key for the object selectect
//                                will be the phone number, cause the after orders, we have phoneNumber and all the content

                String uID = getRef(position).getKey();

                Intent intent = new Intent( context, ProductsActivity.class);
                intent.putExtra("PhoneNumberSelected", uID);
                context.startActivity(intent);
            }
        });


        if (Prevalent.userGroup.equals("Admins")){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CharSequence options[] = new CharSequence[]
                            {
                                    "Yes",
                                    "No"
                            };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Do you want to allow Shipping of this order?");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i==0){
//                                            yes button clicked
                                String uID = getRef(position).getKey();
                                UpdateOrderAndMakeItVisibleOnTheSellerSide(uID);
                            }else{
//                                            non selected
                              return;
                            }
                        }
                    });
                    builder.show();
                }
            });
        }

    }

    private void UpdateOrderAndMakeItVisibleOnTheSellerSide(String uID) {
        //        System.out.println(" UID "+uID);
        Log.i("UId data", uID);
        HashMap<String, Object> shipStatus = new HashMap<>();
        shipStatus.put("state", "VisibleSellerSide");
        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference()
                .child("Orders");
        ordersReference.child(uID).updateChildren(shipStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context,
                                    "Now is visible on the seller Side... ",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, AdminCategoryActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_layout, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView name,phone,totalAmount,date,
                city, address,orderId,userDateTime;
        Button showOrdersButton;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_name_order);
            phone = itemView.findViewById(R.id.phone_number_order);
            totalAmount = itemView.findViewById(R.id.total_price_order);
            date = itemView.findViewById(R.id.date_time_order);
            address = itemView.findViewById(R.id.address_order);
            city = itemView.findViewById(R.id.city_order);
            orderId = itemView.findViewById(R.id.orderId);
            showOrdersButton = itemView.findViewById(R.id.show_all_products_order);

        }
    }
}
