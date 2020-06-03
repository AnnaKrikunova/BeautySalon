package com.example.android.beautysalon.Interface;

import com.example.android.beautysalon.Database.CartItem;

import java.util.List;

public interface ICartItemLoadListener {
    void onGetAllItemFromCartSuccess(List<CartItem> cartItems);
}
