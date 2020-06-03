package com.example.android.beautysalon.Interface;

import com.example.android.beautysalon.Model.Banner;

import java.util.List;

public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
