package com.example.android.beautysalon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Interface.IRecycleItemSelectedListener;
import com.example.android.beautysalon.Model.TimeSlot;
import com.example.android.beautysalon.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder>{
    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());
        if (timeSlotList.size() == 0) {
            holder.card_time_slot.setEnabled(true);
            holder.card_time_slot.setCardBackgroundColor(context.getResources()
                    .getColor(android.R.color.white));
            holder.txt_time_slot_description.setText("Available");
            holder.txt_time_slot_description.setTextColor(context.getResources()
            .getColor(android.R.color.black));
            holder.txt_time_slot.setTextColor(context.getResources()
                    .getColor(android.R.color.black));
        } else {
            for (TimeSlot slotValue: timeSlotList) {
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if (slot == position) {
                    holder.card_time_slot.setEnabled(false);
                    holder.card_time_slot.setTag(Common.DISABLE_TAG);
                    holder.card_time_slot.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.darker_gray));
                    holder.txt_time_slot_description.setText("Full");
                    holder.txt_time_slot_description.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                    holder.txt_time_slot.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                }
            }
        }
        if(!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);
        holder.setiRecycleItemSelectedListener((view, position1) -> {
            for (CardView cardView:cardViewList) {
                if (cardView.getTag() == null) {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));
                }
            }
            holder.card_time_slot.setCardBackgroundColor(context.getResources()
                    .getColor(android.R.color.holo_green_dark));
            Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
            intent.putExtra(Common.KEY_TIME_SLOT, position);
            intent.putExtra(Common.KEY_STEP, 3);
            localBroadcastManager.sendBroadcast(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot, txt_time_slot_description;
        CardView card_time_slot;
        IRecycleItemSelectedListener iRecycleItemSelectedListener;

        public void setiRecycleItemSelectedListener(IRecycleItemSelectedListener iRecycleItemSelectedListener) {
            this.iRecycleItemSelectedListener = iRecycleItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = itemView.findViewById(R.id.txt_time_slot_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecycleItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
