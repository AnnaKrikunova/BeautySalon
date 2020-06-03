package com.example.android.beautysalon.Interface;

import java.util.List;

public interface ISalonsLoadListener {
    void onSalonsLoadSuccess(List<String> areaNameList);
    void onSalonsLoadFailed(String message);
}
