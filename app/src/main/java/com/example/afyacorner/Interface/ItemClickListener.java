package com.example.afyacorner.Interface;

import android.view.View;

public interface ItemClickListener {

    default void OnClick(View view, int position, boolean isLongClick){

    }
}
