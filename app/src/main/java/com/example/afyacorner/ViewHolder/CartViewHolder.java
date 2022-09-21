package com.example.afyacorner.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afyacorner.Interface.ItemClickListener;
import com.example.afyacorner.R;

// for to access our recycle view and populate with data
public class CartViewHolder extends RecyclerView.ViewHolder
     implements View.OnClickListener{
    public TextView txtProductType, txtProductPrice,
                    txtProductQuantity, txtProductWeight;

    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductType = itemView.findViewById(R.id.card_product_type);
        txtProductPrice = itemView.findViewById(R.id.card_product_price);
        txtProductQuantity = itemView.findViewById(R.id.card_product_quantity);
        txtProductWeight = itemView.findViewById(R.id.card_product_weight);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.OnClick(view, getAdapterPosition(), false);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
