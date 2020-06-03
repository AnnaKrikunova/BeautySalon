package com.example.android.beautysalon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.beautysalon.Common.Common;
import com.example.android.beautysalon.Fragments.HomeFragment;
import com.example.android.beautysalon.Fragments.ShoppingFragment;
import com.example.android.beautysalon.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    BottomSheetDialog bottomSheetDialog;

    CollectionReference userRef;

    AlertDialog dialog;

    @Override
    protected void onResume() {
        super.onResume();
        checkRatingDialog();
    }

    private void checkRatingDialog() {
        Paper.init(this);
        String dataSerialized = Paper.book().read(Common.RATING_INFORMATION_KEY, "");
        if (!TextUtils.isEmpty(dataSerialized)) {
            Map<String, String> dataReceived = new Gson()
                    .fromJson(dataSerialized, new TypeToken<Map<String, String>>(){}.getType());
            if (dataReceived != null) {
                Common.showRatingDialog(HomeActivity.this,
                        dataReceived.get(Common.RATING_STATE_KEY),
                        dataReceived.get(Common.RATING_SALON_ID),
                        dataReceived.get(Common.RATING_SALON_NAME),
                        dataReceived.get(Common.RATING_MASTER_ID));

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(HomeActivity.this);

        //Init
        userRef = FirebaseFirestore.getInstance().collection("User");
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        if(getIntent() != null){
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {
                dialog.show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Paper.init(HomeActivity.this);
                    Paper.book().write(Common.LOGGED_KEY, user.getPhoneNumber());
                    DocumentReference currentUser = userRef.document(user.getPhoneNumber());
                    currentUser.get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot userSnapshot = task.getResult();
                                    if (!userSnapshot.exists()) {
                                        showUpdateDialog(user.getPhoneNumber());
                                    } else {
                                        Common.currentUser = userSnapshot.toObject(User.class);
                                        bottomNavigationView.setSelectedItemId(R.id.action_home);
                                    }
                                    if (dialog.isShowing())
                                        dialog.dismiss();

                                }
                            });
                }
            }
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_home)
                    fragment = new HomeFragment();
                else if (menuItem.getItemId() == R.id.action_shopping)
                    fragment = new ShoppingFragment();
                return loadFragment(fragment);
            }
        });

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
            return true;
        }
        return false;
    }

    private void showUpdateDialog(String phoneNumber) {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setTitle("One more step");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_information, null);

        Button btn_update = sheetView.findViewById(R.id.btn_update);
        TextInputEditText edt_name = sheetView.findViewById(R.id.edt_name);
        TextInputEditText edt_address = sheetView.findViewById(R.id.edt_address);

        btn_update.setOnClickListener(view -> {
            if (!dialog.isShowing())
                dialog.show();
           User user = new User(edt_name.getText().toString(),
                   edt_address.getText().toString(),
                   phoneNumber);
           userRef.document(phoneNumber).set(user)
                   .addOnSuccessListener(aVoid -> {
                       bottomSheetDialog.dismiss();
                       if (dialog.isShowing())
                           dialog.dismiss();
                       Common.currentUser = user;
                       bottomNavigationView.setSelectedItemId(R.id.action_home);
                       Toast.makeText(HomeActivity.this, "Thank you", Toast.LENGTH_SHORT).show();
                   })
                   .addOnFailureListener(e -> {
                       bottomSheetDialog.dismiss();
                       if (dialog.isShowing())
                           dialog.dismiss();
                       Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                   });
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();

    }
}
