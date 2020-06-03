package com.example.android.beautysalon.Interface;

import com.example.android.beautysalon.Common.MasterServices;

import java.util.List;

public interface iMasterServicesLoadListener {
    void onMasterServicesLoadSuccess(List<MasterServices> masterServicesList);
    void onMasterServicesLoadFailed(String message);
}
