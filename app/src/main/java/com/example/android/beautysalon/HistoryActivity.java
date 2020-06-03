package com.example.android.beautysalon;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.beautysalon.Adapter.MyHistoryAdapter;
import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Interface.IBookingInfoLoadListener;
import com.example.android.beautysalon.Interface.IHistoryLoadListener;
import com.example.android.beautysalon.Model.BookingInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements IHistoryLoadListener {

    @BindView(R.id.recycler_history)
    RecyclerView recycler_history;
    @BindView(R.id.txt_history)
    TextView txt_history;
    IHistoryLoadListener iHistoryLoadListener;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        iHistoryLoadListener = this;
        ButterKnife.bind(this);
        init();
        initView();

        loadUserBookingInformation();
    }

    private void loadUserBookingInformation() {
        dialog.show();
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");
        userBooking.whereEqualTo("done", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnFailureListener(e -> iHistoryLoadListener.onHistoryInfoLoadFailed(e.getMessage())).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<BookingInformation> bookingInformationList = new ArrayList<>();
                        for(DocumentSnapshot userBookingSnapshot: task.getResult()) {
                            BookingInformation bookingInformation = userBookingSnapshot.toObject(BookingInformation.class);
                            bookingInformationList.add(bookingInformation);
                        }
                        iHistoryLoadListener.onHistoryInfoLoadSuccess(bookingInformationList);
                    }
                });
    }

    private void initView() {
        recycler_history.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_history.setLayoutManager(layoutManager);
        recycler_history.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

    }

    @Override
    public void onHistoryInfoLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onHistoryInfoLoadSuccess(List<BookingInformation> bookingInformationList) {
        MyHistoryAdapter adapter = new MyHistoryAdapter(this, bookingInformationList);
        recycler_history.setAdapter(adapter);
        txt_history.setText(new StringBuilder("HISTORY (").append(bookingInformationList.size()).append(")"));
        dialog.dismiss();
    }
}
