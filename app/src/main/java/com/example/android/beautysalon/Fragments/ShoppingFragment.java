package com.example.android.beautysalon.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.beautysalon.Adapter.MyShoppingItemAdapter;
import com.example.android.beautysalon.Common.SpacesItemDecoration;
import com.example.android.beautysalon.Interface.IShoppingDataLoadListener;
import com.example.android.beautysalon.Model.ShoppingItem;
import com.example.android.beautysalon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment implements IShoppingDataLoadListener {
    CollectionReference shoppingItemRef;
    IShoppingDataLoadListener iShoppingDataLoadListener;
    Unbinder unbinder;

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    @BindView(R.id.chip_shampoo)
    Chip chip_shampoo;
    @OnClick(R.id.chip_shampoo)
    void shampooChipClick() {
        setSelectedChip(chip_shampoo);
        loadShoppingItem(chip_shampoo.getText().toString());
    }

    @BindView(R.id.recycler_items)
    RecyclerView recycler_items;

    private void loadShoppingItem(String itemMenu) {
        shoppingItemRef = FirebaseFirestore.getInstance().collection("Shopping")
                .document(itemMenu)
                .collection("Items");

        shoppingItemRef.get()
                .addOnFailureListener(e -> iShoppingDataLoadListener.onShoppingDataLoadFailed(e.getMessage())).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ShoppingItem> shoppingItems = new ArrayList<>();
                        for (DocumentSnapshot itemSnapShot: task.getResult()) {
                            ShoppingItem shoppingItem = itemSnapShot.toObject(ShoppingItem.class);
                            shoppingItem.setId(itemSnapShot.getId());
                            shoppingItems.add(shoppingItem);

                        }
                        iShoppingDataLoadListener.onShoppingDataLoadSuccess(shoppingItems);
                    }
                });
    }

    private void setSelectedChip(Chip chip) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chipItem = (Chip) chipGroup.getChildAt(i);
            if (chipItem.getId() != chip.getId()) {
                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                chipItem.setChipBackgroundColorResource(android.R.color.holo_green_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));
            }

        }
    }

    @BindView(R.id.chip_mask)
    Chip chip_mask;
    @OnClick(R.id.chip_mask)
    void maskChipClick() {
        setSelectedChip(chip_mask);
        loadShoppingItem(chip_mask.getText().toString());
    }

    @BindView(R.id.chip_hairConditioner)
    Chip chip_hairConditioner;
    @OnClick(R.id.chip_hairConditioner)
    void hairConditionerChipClick() {
        setSelectedChip(chip_hairConditioner);
        loadShoppingItem("HairConditioner");
    }
    @BindView(R.id.chip_hairDye)
    Chip chip_hairDye;
    @OnClick(R.id.chip_hairDye)
    void hairDyeChipClick() {
        setSelectedChip(chip_hairDye);
        loadShoppingItem("HairDye");
    }


    public ShoppingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_shopping, container, false);
        unbinder = ButterKnife.bind(this,itemView);
        iShoppingDataLoadListener = this;
        loadShoppingItem("Shampoo");
        initView();
        return itemView;
    }

    private void initView() {
        recycler_items.setHasFixedSize(true);
        recycler_items.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler_items.addItemDecoration(new SpacesItemDecoration(8));

    }

    @Override
    public void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList) {
        MyShoppingItemAdapter adapter = new MyShoppingItemAdapter(getContext(), shoppingItemList);
        recycler_items.setAdapter(adapter);

    }

    @Override
    public void onShoppingDataLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
