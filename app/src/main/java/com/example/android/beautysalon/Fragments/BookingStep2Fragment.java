package com.example.android.beautysalon.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.beautysalon.Adapter.MyMasterAdapter;
import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Common.SpacesItemDecoration;
import com.example.android.beautysalon.Model.Master;
import com.example.android.beautysalon.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookingStep2Fragment extends Fragment {
    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.recycler_master)
    RecyclerView recycler_master;
    private BroadcastReceiver masterDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Master> masterArrayList = intent.getParcelableArrayListExtra(Common.KEY_MASTER_LOAD_DONE);
            MyMasterAdapter adapter = new MyMasterAdapter(getContext(), masterArrayList);
            recycler_master.setAdapter(adapter);
        }
    };
    static  BookingStep2Fragment instance;

    public static BookingStep2Fragment getInstance() {
        if (instance == null) {
            instance = new BookingStep2Fragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(masterDoneReceiver, new IntentFilter(Common.KEY_MASTER_LOAD_DONE));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(masterDoneReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_two, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        initView();
        return itemView;
    }

    private void initView() {
        recycler_master.setHasFixedSize(true);
        recycler_master.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_master.addItemDecoration(new SpacesItemDecoration(4));
    }
}
