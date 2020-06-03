package com.example.android.beautysalon.Fragments;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ss.com.bannerslider.Slider;

import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.beautysalon.Adapter.HomeSliderAdapter;
import com.example.android.beautysalon.Adapter.LookbookAdapter;
import com.example.android.beautysalon.BookingActivity;
import com.example.android.beautysalon.CartActivity;
import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Database.CartDatabase;
import com.example.android.beautysalon.Database.DatabaseUtils;
import com.example.android.beautysalon.HistoryActivity;
import com.example.android.beautysalon.Interface.IBannerLoadListener;
import com.example.android.beautysalon.Interface.IBookingInfoLoadListener;
import com.example.android.beautysalon.Interface.IBookingInformationChangeListener;
import com.example.android.beautysalon.Interface.ICountItemInCartListener;
import com.example.android.beautysalon.Interface.ILookbookLoadListener;
import com.example.android.beautysalon.Model.Banner;
import com.example.android.beautysalon.Model.BookingInformation;
import com.example.android.beautysalon.PriceActivity;
import com.example.android.beautysalon.R;
import com.example.android.beautysalon.Service.PicassoImageLoadingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment implements ILookbookLoadListener, IBannerLoadListener, IBookingInfoLoadListener, IBookingInformationChangeListener, ICountItemInCartListener {

    private Unbinder unbinder;
    CartDatabase cartDatabase;
    AlertDialog dialog;

    @BindView(R.id.notification_badge)
    NotificationBadge notificationBadge;

    @BindView(R.id.layout_user_information)
    LinearLayout layout_user_information;

    @BindView(R.id.txt_user_name)
    TextView txt_user_name;
    @BindView(R.id.banner_slider)
    Slider banner_slider;
    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;

    @BindView(R.id.card_booking_info)
    CardView card_booking_info;
    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_master)
    TextView txt_salon_master;
    @BindView(R.id.txt_time)
    TextView txt_time;
//    @BindView(R.id.txt_time_remain)
//    TextView txt_time_remain;

    @OnClick(R.id.btn_delete_booking)
    void deleteBooking() {
        deleteBookingFromMaster(false);
    }

    @OnClick(R.id.btn_change_booking)
    void changeBooking() {
       changeBookingFromUser();
    }

    private void changeBookingFromUser() {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Hey")
                .setMessage("Do you really want to change booking information?")
                .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("OK", (dialogInterface, i) -> {
                    deleteBookingFromMaster(true);
                });
        confirmDialog.show();
    }

    private void deleteBookingFromMaster(boolean isChange) {
        if (Common.currentBooking != null) {
            dialog.show();
            DocumentReference barberBookingInfo = FirebaseFirestore.getInstance()
                    .collection("Salons")
                    .document(Common.currentBooking.getBookingCity())
                    .collection("Branch")
                    .document(Common.currentBooking.getSalonId())
                    .collection("Master")
                    .document(Common.currentBooking.getMasterId())
                    .collection(Common.convertTimeStampToString(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());
            barberBookingInfo.delete().addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(aVoid -> deleteBookingFromUser(isChange));

        } else {
            Toast.makeText(getContext(), "Current Booking must not be null", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBookingFromUser(boolean isChange) {
        if (!TextUtils.isEmpty(Common.currentBookingId)) {
            DocumentReference userBookingInfo = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking")
                    .document(Common.currentBookingId);
            userBookingInfo.delete().addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(aVoid -> {
                Paper.init(getActivity());
                if (Paper.book().read(Common.EVENT_URI_CACHE) != null) {
                    Uri eventUri = null;
                    String eventString = Paper.book().read(Common.EVENT_URI_CACHE).toString();
                    if (eventString != null && !TextUtils.isEmpty(eventString)) {
                        eventUri = Uri.parse(eventString);
                    }
                    if (eventUri != null)
                        getActivity().getContentResolver().delete(eventUri, null, null);
                }

                Toast.makeText(getContext(), "Success delete booking", Toast.LENGTH_SHORT).show();

                loadUserBooking();
                if (isChange)
                    iBookingInformationChangeListener.onBookingInformationChange();
                dialog.dismiss();
            });
        } else {
            dialog.dismiss();
            Toast.makeText(getContext(), "Booking information ID must not be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.card_view_booking)
    void booking() {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    @OnClick(R.id.card_view_cart)
    void openCartActivity() {
        startActivity(new Intent(getActivity(), CartActivity.class));
    }

    @OnClick(R.id.card_view_history)
    void openHistoryActivity() {
        startActivity(new Intent(getActivity(), HistoryActivity.class));
    }

    @OnClick(R.id.card_view_price)
    void openPriceActivity(){startActivity(new Intent(getActivity(), PriceActivity.class));}

    CollectionReference bannerRef, lookbookRef;

    IBannerLoadListener iBannerLoadListener;
    ILookbookLoadListener iLookbookLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;

    ListenerRegistration userBookingListener = null;
    EventListener<QuerySnapshot> userBookingEvent = null;

    public HomeFragment() {
        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        lookbookRef = FirebaseFirestore.getInstance().collection("Lookbook");

    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
        countCartItem();
    }

    private void loadUserBooking() {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        userBooking
                .whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()) {
                                BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation, queryDocumentSnapshot.getId());
                                break;
                            }
                        } else {
                            iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                        }
                    }
                }).addOnFailureListener(e -> iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage()));

        if (userBookingEvent != null) {
            if (userBookingListener == null) {
                userBookingListener = userBooking.addSnapshotListener(userBookingEvent);
            }

        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        cartDatabase = CartDatabase.getInstance(getContext());

        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener = this;
        iLookbookLoadListener = this;
        iBookingInfoLoadListener = this;
        iBookingInformationChangeListener = this;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            setUserInformation();
            loadBanner();
            loadLookBook();
            initRealtimeUserBooking();
            loadUserBooking();
            countCartItem();
        }
        return view;
    }

    private void initRealtimeUserBooking() {
        if (userBookingEvent == null) {
            userBookingEvent = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    loadUserBooking();
                }
            };
        }
    }

    private void countCartItem() {
        DatabaseUtils.countItemInCart(cartDatabase, this);
    }

    private void loadLookBook() {
        lookbookRef.get().addOnCompleteListener(task -> {
            List<Banner> lookbooks = new ArrayList<>();
            if (task.isSuccessful()) {
                for(QueryDocumentSnapshot bannerSnapshot: task.getResult()) {
                    Banner banner = bannerSnapshot.toObject(Banner.class);
                    lookbooks.add(banner);
                }
                iLookbookLoadListener.onLookbookLoadSuccess(lookbooks);
            }
        }).addOnFailureListener(e -> iLookbookLoadListener.onLookbookLoadFailed(e.getMessage()));
    }

    private void loadBanner() {
        bannerRef.get().addOnCompleteListener(task -> {
            List<Banner> banners = new ArrayList<>();
            if (task.isSuccessful()) {
                for(QueryDocumentSnapshot bannerSnapshot: task.getResult()) {
                    Banner banner = bannerSnapshot.toObject(Banner.class);
                    banners.add(banner);
                }
                iBannerLoadListener.onBannerLoadSuccess(banners);
            }
        }).addOnFailureListener(e -> iBannerLoadListener.onBannerLoadFailed(e.getMessage()));
    }

    private void setUserInformation() {
        layout_user_information.setVisibility(View.VISIBLE);
        txt_user_name.setText(Common.currentUser.getName());

    }

    @Override
    public void onLookbookLoadSuccess(List<Banner> banners) {
        recycler_look_book.setHasFixedSize(true);
        recycler_look_book.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_look_book.setAdapter(new LookbookAdapter(getActivity(), banners));
    }

    @Override
    public void onLookbookLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        banner_slider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String bookingId) {
        Common.currentBooking = bookingInformation;
        Common.currentBookingId = bookingId;

        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_salon_master.setText(bookingInformation.getMasterName());
        txt_time.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(), 0).toString();
//        txt_time_remain.setText(dateRemain);
        card_booking_info.setVisibility(View.VISIBLE);
        dialog.dismiss();
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInformationChange() {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    @Override
    public void onCartItemCountSuccess(int count) {
        notificationBadge.setText(String.valueOf(count));
    }

    @Override
    public void onDestroy() {
        if (userBookingListener != null) {
            userBookingListener.remove();
        }
        super.onDestroy();
    }
}
