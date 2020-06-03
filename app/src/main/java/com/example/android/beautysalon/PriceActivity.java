package com.example.android.beautysalon;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.beautysalon.Adapter.PriceListAdapter;
import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Common.MasterServices;
import com.example.android.beautysalon.Interface.iMasterServicesLoadListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriceActivity extends AppCompatActivity implements iMasterServicesLoadListener {


    @BindView(R.id.price_list)
    ListView listView;


    private void loadMasterServices() {
        Common.city = "Kyiv";
        String salonId = "ihvHDE7dnG6EwB4L6Jcd";
        FirebaseFirestore.getInstance()
                .collection("Salons")
                .document(Common.city)
                .collection("Branch")
                .document(salonId)
                .collection("Services")
                .get()
                .addOnFailureListener(e -> onMasterServicesLoadFailed(e.getMessage())).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<MasterServices> masterServices = new ArrayList<>();
                for(DocumentSnapshot masterSnapshot: task.getResult()) {
                    MasterServices services = masterSnapshot.toObject(MasterServices.class);
                    masterServices.add(services);

                }
                onMasterServicesLoadSuccess(masterServices);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        ButterKnife.bind(PriceActivity.this);
        loadMasterServices();
    }

    @Override
    public void onMasterServicesLoadSuccess(List<MasterServices> masterServicesList) {
        List<String> nameServices = new ArrayList<>();
        List<Long> priceServices = new ArrayList<>();

        for(MasterServices masterServices: masterServicesList) {
            nameServices.add(masterServices.getName());
        }

        for(MasterServices masterServices: masterServicesList) {
            priceServices.add(masterServices.getPrice());
        }

        String[] service_items;
        long[] service_prices = new long[priceServices.size()];

        service_items = nameServices.toArray(new String[0]);
        int index = 0;
        for (final Long value: priceServices) {
            service_prices[index++] = value;
        }


        listView = findViewById(R.id.price_list);
        PriceListAdapter adapter = new PriceListAdapter(this, service_items, service_prices);
        listView.setAdapter(adapter);
    }

    @Override
    public void onMasterServicesLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
