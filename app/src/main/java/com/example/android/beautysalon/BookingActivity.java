package com.example.android.beautysalon;


import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.example.android.beautysalon.Adapter.MyViewPagerAdapter;
import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Common.NonSwipeViewPager;
import com.example.android.beautysalon.Model.Master;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference masterRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

    @OnClick(R.id.btn_previous_step)
    void previousStep() {
        if (Common.step == 3 || Common.step > 0) {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if (Common.step < 3) {
                btn_next_step.setEnabled(true);
                setColorButton();

            }
        }
    }
    @OnClick(R.id.btn_next_step)
    void nextClick() {
        //Toast.makeText(this, ""+Common.currentSalon.getSalonId(), Toast.LENGTH_SHORT).show();
        if (Common.step < 3 || Common.step == 0) {
            Common.step++;
            if (Common.step == 1) {
                if (Common.currentSalon != null)
                    loadBarberBySalon(Common.currentSalon.getSalonId());
            } else if(Common.step == 2) {
                if (Common.currentMaster != null)
                    loadTimeSlotMaster(Common.currentMaster.getMasterId());
            } else if(Common.step == 3) {
                if (Common.currentTimeSlot != -1)
                    confirmBooking();
            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotMaster(String masterId) {
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadBarberBySalon(String salonId) {
        dialog.show();

        if (!TextUtils.isEmpty(Common.city)){
            masterRef = FirebaseFirestore.getInstance()
                    .collection("Salons")
                    .document(Common.city)
            .collection("Branch")
            .document(salonId)
            .collection("Master");
            masterRef.get().addOnCompleteListener(task -> {
                ArrayList<Master> masters = new ArrayList<>();
                for(QueryDocumentSnapshot masterSnapShot:task.getResult()){
                    Master master = masterSnapShot.toObject(Master.class);
                    master.setPassword("");
                    master.setMasterId(masterSnapShot.getId());
                    masters.add(master);
                }
                Intent intent = new Intent(Common.KEY_MASTER_LOAD_DONE);
                intent.putParcelableArrayListExtra(Common.KEY_MASTER_LOAD_DONE, masters);
                localBroadcastManager.sendBroadcast(intent);
                dialog.dismiss();
            }).addOnFailureListener(e -> {
                dialog.dismiss();
            });
        }

    }

    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if (step == 1)
                Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if (step == 2)
                Common.currentMaster = intent.getParcelableExtra(Common.KEY_MASTER_SELECTED);
            else if (step == 3)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);
            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        localBroadcastManager = localBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        setUpStepView();
        setColorButton();
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                stepView.go(position, true);
                if (position == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

                btn_next_step.setEnabled(false);
                setColorButton();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
        if (btn_next_step.isEnabled()) {
            btn_next_step.setBackgroundResource(R.color.colorButton);
        } else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_previous_step.isEnabled()) {
            btn_previous_step.setBackgroundResource(R.color.colorButton);
        } else {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setUpStepView() {
        List<String> stepList = new ArrayList();
        stepList.add("Salon");
        stepList.add("Barber");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
    }
}
