package com.example.android.beautysalon.Interface;

import com.example.android.beautysalon.Model.Banner;

import java.util.List;

public interface ILookbookLoadListener {
    void onLookbookLoadSuccess(List<Banner> banners);
    void onLookbookLoadFailed(String message);
}
