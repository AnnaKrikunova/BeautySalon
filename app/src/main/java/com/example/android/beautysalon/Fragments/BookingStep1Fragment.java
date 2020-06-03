package com.example.android.beautysalon.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.beautysalon.Adapter.MySalonAdapter;
import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Common.SpacesItemDecoration;
import com.example.android.beautysalon.Interface.IBranchLoadListener;
import com.example.android.beautysalon.Interface.ISalonsLoadListener;
import com.example.android.beautysalon.Model.Salon;
import com.example.android.beautysalon.R;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep1Fragment extends Fragment implements ISalonsLoadListener, IBranchLoadListener {

    CollectionReference salonsRef;
    CollectionReference branchRef;

    ISalonsLoadListener iSalonsLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_salon)
    RecyclerView recycler_salon;

    Unbinder unbinder;
    AlertDialog dialog;

    static  BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance() {
        if (instance == null) {
            instance = new BookingStep1Fragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        salonsRef = FirebaseFirestore.getInstance().collection("Salons");
        iSalonsLoadListener = this;
        iBranchLoadListener = this;
        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_one, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        initView();
        loadSalons();
        return itemView;
    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_salon.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadSalons() {
        salonsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList();
                        list.add("Please choose the city");
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult())
                            list.add(documentSnapshot.getId());
                        iSalonsLoadListener.onSalonsLoadSuccess(list);
                    }
                }).addOnFailureListener(e -> {
                    iSalonsLoadListener.onSalonsLoadFailed(e.getMessage());
                });
    }

    @Override
    public void onSalonsLoadSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (position > 0) {
                loadBranchOfCity(item.toString());
            } else {
                recycler_salon.setVisibility(View.GONE);
            }
        });
    }

    private void loadBranchOfCity(String city) {
        dialog.show();
        Common.city = city;
        branchRef = FirebaseFirestore.getInstance()
                .collection("Salons")
                .document(city)
                .collection("Branch");
        branchRef.get().addOnCompleteListener(task -> {
            List<Salon> list = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                    Salon salon = documentSnapshot.toObject(Salon.class);
                    salon.setSalonId(documentSnapshot.getId());
                    list.add(salon);
                }

                iBranchLoadListener.onBranchLoadSuccess(list);
            }
        }).addOnFailureListener(e -> {
            iBranchLoadListener.onBranchLoadFailed(e.getMessage());
        });
    }

    @Override
    public void onSalonsLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBranchLoadSuccess(List<Salon> salonList) {
        MySalonAdapter adapter = new MySalonAdapter(getActivity(), salonList);
        recycler_salon.setAdapter(adapter);
        recycler_salon.setVisibility(View.VISIBLE);
        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
