package com.example.android.beautysalon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.beautysalon.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PriceListAdapter extends ArrayAdapter<String> {
    String[] service_items;
    long[] prices;
    Context c;
    LayoutInflater inflater;
    public PriceListAdapter(@NonNull Context context, String[] service_items, long[] prices) {
        super(context, R.layout.price_row, service_items);
        this.c = context;
        this.service_items = service_items;
        this.prices = prices;
    }

    public class ViewHolder{
        TextView customer_item, customer_price;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.price_row, null);
            holder.customer_item = convertView.findViewById(R.id.row_service_item);
            holder.customer_price = convertView.findViewById(R.id.row_service_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.customer_item.setText(service_items[position]);
        holder.customer_price.setText(prices[position]+"");
        return convertView;
    }
}
