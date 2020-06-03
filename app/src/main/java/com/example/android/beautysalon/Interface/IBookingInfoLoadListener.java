package com.example.android.beautysalon.Interface;

import com.example.android.beautysalon.Model.BookingInformation;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String documentId);
    void onBookingInfoLoadFailed(String message);

}
