package com.example.android.beautysalon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Interface.IRecycleItemSelectedListener;
import com.example.android.beautysalon.Model.Salon;
import com.example.android.beautysalon.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.MyViewHolder> {

    Context context;
    List<Salon> salonList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MySalonAdapter(Context context, List<Salon> salonList) {
        this.context = context;
        this.salonList = salonList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_salon, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_salon_name.setText(salonList.get(position).getName());
        holder.txt_salon_address.setText(salonList.get(position).getAddress());
        if (!cardViewList.contains(holder.card_salon)) {
            cardViewList.add(holder.card_salon);
        }
        holder.setiRecycleItemSelectedListener((view, position1) -> {
            for(CardView cardView: cardViewList) {
                cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            }
            holder.card_salon.setCardBackgroundColor(context.getResources()
            .getColor(android.R.color.holo_green_dark));
            Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
            intent.putExtra(Common.KEY_SALON_STORE, salonList.get(position1));
            intent.putExtra(Common.KEY_STEP, 1);
            localBroadcastManager.sendBroadcast(intent);
        });
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_salon_name, txt_salon_address;
        CardView card_salon;
        IRecycleItemSelectedListener iRecycleItemSelectedListener;

        public void setiRecycleItemSelectedListener(IRecycleItemSelectedListener iRecycleItemSelectedListener) {
            this.iRecycleItemSelectedListener = iRecycleItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_salon = itemView.findViewById(R.id.card_salon);
            txt_salon_address = itemView.findViewById(R.id.txt_salon_address);
            txt_salon_name = itemView.findViewById(R.id.txt_salon_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecycleItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
