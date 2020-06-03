package com.example.android.beautysalon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Interface.IRecycleItemSelectedListener;
import com.example.android.beautysalon.Model.Master;
import com.example.android.beautysalon.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyMasterAdapter extends RecyclerView.Adapter<MyMasterAdapter.MyViewHolder> {
    Context context;
    List<Master> masterList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyMasterAdapter(Context context, List<Master> masterList) {
        this.context = context;
        this.masterList = masterList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_master, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.txt_master_name.setText(masterList.get(position).getName());
            if (masterList.get(position).getRatingTimes() != null)
                holder.ratingBar.setRating(masterList.get(position).getRating().floatValue() / masterList.get(position).getRatingTimes());
            else
                holder.ratingBar.setRating(0);
            if (!cardViewList.contains(holder.card_master))
                cardViewList.add(holder.card_master);
            holder.setiRecycleItemSelectedListener((view, position1) -> {
                for (CardView cardView: cardViewList) {
                    cardView.setCardBackgroundColor(context.getResources()
                    .getColor(android.R.color.white));
                }
                holder.card_master.setCardBackgroundColor(context.getResources()
                .getColor(android.R.color.holo_green_dark));

                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_MASTER_SELECTED, masterList.get(position1));
                intent.putExtra(Common.KEY_STEP, 2);
                localBroadcastManager.sendBroadcast(intent);
            });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_master_name;
        RatingBar ratingBar;
        CardView card_master;
        IRecycleItemSelectedListener iRecycleItemSelectedListener;

        public void setiRecycleItemSelectedListener(IRecycleItemSelectedListener iRecycleItemSelectedListener) {
            this.iRecycleItemSelectedListener = iRecycleItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_master_name = itemView.findViewById(R.id.txt_master_name);
            ratingBar = itemView.findViewById(R.id.rtb_master);
            card_master = itemView.findViewById(R.id.card_master);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecycleItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
