package com.example.android.beautysalon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.beautysalon.Adapter.MyCartAdapter;
import com.example.android.beautysalon.Database.CartDatabase;
import com.example.android.beautysalon.Database.CartItem;
import com.example.android.beautysalon.Database.DatabaseUtils;
import com.example.android.beautysalon.Interface.ICartItemLoadListener;
import com.example.android.beautysalon.Interface.ICartItemUpdateListener;
import com.example.android.beautysalon.Interface.ISumCartListener;

import java.util.List;

public class CartActivity extends AppCompatActivity implements ICartItemLoadListener, ICartItemUpdateListener, ISumCartListener {

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.btn_clear_cart)
    Button button_clear_cart;

    @OnClick(R.id.btn_clear_cart)
    void clearCart() {
        DatabaseUtils.clearCart(cartDatabase);
        DatabaseUtils.getAllCart(cartDatabase, this);
    }
    CartDatabase cartDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ButterKnife.bind(CartActivity.this);
        cartDatabase = CartDatabase.getInstance(this);

        DatabaseUtils.getAllCart(cartDatabase, this);

        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(linearLayoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));

    }

    @Override
    public void onGetAllItemFromCartSuccess(List<CartItem> cartItems) {
        MyCartAdapter adapter = new MyCartAdapter(this, cartItems, this);
        recycler_cart.setAdapter(adapter);
    }

    @Override
    public void onCartItemUpdateSuccess() {
        DatabaseUtils.sumCart(cartDatabase, this);
    }

    @Override
    public void onSumCartSuccess(Long value) {
        txt_total_price.setText(new StringBuilder("₴").append(value));
    }
}
