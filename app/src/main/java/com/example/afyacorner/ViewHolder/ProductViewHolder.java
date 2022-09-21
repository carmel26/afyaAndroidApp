package com.example.afyacorner.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afyacorner.Interface.ItemClickListener;
import com.example.afyacorner.R;


public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductType, txtProductDescription,
            txtProductWeight, txtProductPrice;

    public ImageView productImageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImageView = itemView.findViewById(R.id.product_image);
        txtProductDescription = itemView.findViewById(R.id.product_description);
        txtProductWeight = itemView.findViewById(R.id.product_weight);
        txtProductPrice = itemView.findViewById(R.id.product_price);
        txtProductType = itemView.findViewById(R.id.product_type_view);
    }

    public  void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.OnClick(view, getAdapterPosition(), false);
    }
}
