package com.example.android.beautysalon.Interface;

import com.example.android.beautysalon.Model.BookingInformation;

import java.util.List;

public interface IHistoryLoadListener {
    void onHistoryInfoLoadFailed(String message);

    void onHistoryInfoLoadSuccess(List<BookingInformation> bookingInformationList);
}
