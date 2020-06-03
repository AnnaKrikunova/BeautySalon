package com.example.android.beautysalon.Interface;

import com.example.android.beautysalon.Model.ShoppingItem;

import java.util.List;

public interface IShoppingDataLoadListener {
    void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList);
    void onShoppingDataLoadFailed(String message);
}
