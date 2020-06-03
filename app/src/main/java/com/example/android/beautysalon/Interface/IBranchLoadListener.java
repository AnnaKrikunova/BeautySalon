package com.example.android.beautysalon.Interface;

import com.example.android.beautysalon.Model.Salon;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);
}
