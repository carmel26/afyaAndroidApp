package com.example.afyacorner.Home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afyacorner.AdminCategoryActivity;
import com.example.afyacorner.AdminMaintainProductActivity;
import com.example.afyacorner.BuyerOrderListActivity;
import com.example.afyacorner.CartActivity;
import com.example.afyacorner.Cookies.Prevalent;
import com.example.afyacorner.MainActivity;
import com.example.afyacorner.Models.Product;
import com.example.afyacorner.R;
import com.example.afyacorner.SettingsActivity;
import com.example.afyacorner.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afyacorner.databinding.ActivityHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference productReference;
    private static String PRODUCTREFERENCE = "Products";
    private String typeUser = "";
    private String categoryUser = "";
    private String[] orderIds  = new String[]{} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        typeUser = getIntent().getExtras().get("Admin").toString();
        typeUser = Prevalent.userGroup;
        categoryUser = typeUser;

        Paper.init(this);
        productReference = FirebaseDatabase.getInstance().getReference().child(PRODUCTREFERENCE);

        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                if (!typeUser.equals("Admins")) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.nav_cart){
                    if (!typeUser.equals("Admins")) {
                        Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                        startActivity(intent);
                    }
                } else  if (id == R.id.nav_orderBuyer){
                    if (!typeUser.equals("Admins")) {
//                        get orders from this connected user
                        DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference()
                                .child("Orders");

                        orderReference.child(Prevalent.currentOnlineUser.getPhone());

                        System.out.println(orderReference.getKey()+" Inside "+ orderIds);
                        orderReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    orderIds = new String[]{snapshot.getKey()};
                                    System.out.println("orderIds Inside "+ orderIds);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        System.out.println("orderIds "+ orderIds);
                        Intent intent = new Intent(HomeActivity.this, BuyerOrderListActivity.class);
                        intent.putExtra("orderId", orderIds);
                        startActivity(intent);
                    }
                }else  if (id == R.id.nav_category){
//                    if (!typeUser.equals("Admins")) {
                        Intent intent = new Intent(HomeActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);
//                    }
                }else  if (id == R.id.nav_settings){
                        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(intent);
                }else  if (id == R.id.nav_logout){
                    Paper.book().destroy();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });


        View headView = navigationView.getHeaderView(0);
        TextView userNameTextView = headView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headView.findViewById(R.id.user_profile_image);

        if (!typeUser.equals("Admins") && Prevalent.currentOnlineUser.getName() != null){
            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        }else if (Prevalent.currentOnlineUser.getName() != null){
            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        }

        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);

//        for our content_home2_files ,we need to handle data in it
        recyclerView = findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

//    retrieving data from firebase


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Product> options;
        if (Prevalent.userGroup.equals("Admins")){
            options =
                    new FirebaseRecyclerOptions.Builder<Product>()
                            .setQuery(productReference, Product.class)
                            .build();
        }else{
            options =
                    new FirebaseRecyclerOptions.Builder<Product>()
                            .setQuery(productReference, Product.class)
                            .build();
        }


        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    //            option parameter is our recycle query which contain our dataReference
                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                        NumberFormat format = NumberFormat.getCurrencyInstance();
                        format.setMaximumFractionDigits(0);
                        format.setCurrency(Currency.getInstance("TZS"));
                        String price = format.format(Integer.parseInt(model.getPrice(), 10));

                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(2);
                        String weight = df.format(Integer.parseInt(model.getQuantity(), 10));

                        holder.txtProductPrice.setText("Price: "+price+"");
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductWeight.setText("Quantity: "+weight+"");

//                        if (model.getProductStatus().equals("Not Approved")){
//                            holder.txtProductType.setText(model.getType()+"(Not Approved)");
//                            holder.txtProductType.setTextColor(Color.parseColor("#8f0e0e"));
//                            holder.txtProductWeight.setTextColor(Color.parseColor("#8f0e0e"));
//                            holder.txtProductPrice.setTextColor(Color.parseColor("#8f0e0e"));
//                        }else{
                            holder.txtProductType.setText(model.getType());
                            holder.txtProductPrice.setTextColor(Color.parseColor("#000000"));
//                        }

                        Picasso.get().load(model.getImage()).into(holder.productImageView);

//                        holder.txtProductCategoryName.setText(model.getCategory());
//                        holder.txtProductDateTime.setText(model.getDate()+"-"+model.getDate());


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent;
////
//                                    if (Prevalent.currentOnlineUser.getPhone().equals(model.getSellerId())  && !typeUser.equals("Admins")){
//                                        Toast.makeText(HomeActivity.this,
//                                                "You can't BUY OWN DRUG... Select another please",
//                                                Toast.LENGTH_SHORT).show();
//                                    }else{
                                        if (typeUser.equals("Admins")) {
                                            intent = new Intent(HomeActivity.this, AdminMaintainProductActivity.class);
                                        }else {
                                            intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
                                        }
                                        intent.putExtra("pid", model.getProdId());
                                        intent.putExtra("sellerName", model.getSellerName());
                                        intent.putExtra("sellerId", model.getSellerId());
                                        startActivity(intent);
//                                    }

                            }
                        });
                    }

                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
//        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        System.out.println("number1 : "+item.getItemId());

        return super.onOptionsItemSelected(item);
    }
}