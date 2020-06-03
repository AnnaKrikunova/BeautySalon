package com.example.android.beautysalon.Common;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.beautysalon.HomeActivity;
import com.example.android.beautysalon.Model.BookingInformation;
import com.example.android.beautysalon.Model.Master;
import com.example.android.beautysalon.Model.MyToken;
import com.example.android.beautysalon.Model.Salon;
import com.example.android.beautysalon.Model.User;
import com.example.android.beautysalon.R;
import com.example.android.beautysalon.Service.MyFCMService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.app.NotificationCompat;
import io.paperdb.Paper;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_SALON_STORE = "SALON_SAVE";
    public static final String KEY_MASTER_LOAD_DONE = "MASTER_LOAD_DONE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_MASTER_SELECTED = "MASTER_SELECTED";
    public static final int TIME_SLOT_TOTAL = 10;
    public static final String DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String EVENT_URI_CACHE = "URI_EVENT_SAVE";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final String LOGGED_KEY = "UserLogged";
    public static final String RATING_INFORMATION_KEY = "RATING_INFORMATION";
    public static String IS_LOGIN = "IsLogin";
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

    public static final String RATING_STATE_KEY = "RATING_STATE";
    public static final String RATING_SALON_ID = "RATING_SALON_ID";
    public static final String RATING_SALON_NAME = "RATING_SALON_NAME";
    public static final String RATING_MASTER_ID = "RATING_MASTER_ID";

    public static User currentUser;
    public static Salon currentSalon;
    public static int step = 0;
    public static String city = "";
    public static Master currentMaster;
    public static int currentTimeSlot = -1;
    public static Calendar bookingDate = Calendar.getInstance();
    public static BookingInformation currentBooking;
    public static String currentBookingId="";

    public static String convertTimeSlotToString(int slot) {
        switch (slot) {
            case 0: return "9:00-10:00";
            case 1: return "10:00-11:00";
            case 2: return "11:00-12:00";
            case 3: return "12:00-13:00";
            case 4: return "13:00-14:00";
            case 5: return "14:00-15:00";
            case 6: return "15:00-16:00";
            case 7: return "16:00-17:00";
            case 8: return "17:00-18:00";
            case 9: return "18:00-19:00";
            default: return "Closed";
        }
    }

    public static String convertTimeStampToString(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        return simpleDateFormat.format(date);
    }

    public static void showNotification(Context context, int notification_id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context,
                    notification_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        String NOTIFICATION_CHANNEL_ID = "salon_client_app";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Staff app");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        Notification notification = builder.build();
        notificationManager.notify(notification_id, notification);
    }

    public static void showRatingDialog(Context context, String stateName, String salonId, String salonName, String masterId) {
        DocumentReference masterNeedRateRef = FirebaseFirestore.getInstance()
                .collection("Salons")
                .document(stateName)
                .collection("Branch")
                .document(salonId)
                .collection("Master")
                .document(masterId);
        masterNeedRateRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Master masterRate = task.getResult().toObject(Master.class);
                    masterRate.setMasterId(task.getResult().getId());

                    View view = LayoutInflater.from(context)
                            .inflate(R.layout.layout_rating_dialog, null);
                    TextView txt_salon_name = view.findViewById(R.id.txt_salon_name);
                    TextView txt_master_name = view.findViewById(R.id.txt_master_name);
                    AppCompatRatingBar ratingBar = view.findViewById(R.id.rating);

                    txt_master_name.setText(masterRate.getName());
                    txt_salon_name.setText(salonName);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setView(view)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Double original_rating = masterRate.getRating();
                                    Long ratingTimes = masterRate.getRatingTimes();
                                    float userRating = ratingBar.getRating();

                                    Double finalRating = (original_rating + userRating);
                                    Map<String, Object> data_update = new HashMap<>();
                                    data_update.put("rating", finalRating);
                                    data_update.put("ratingTimes", ++ratingTimes);

                                    masterNeedRateRef.update(data_update)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Thank you for rating", Toast.LENGTH_SHORT).show();
                                                Paper.init(context);
                                                Paper.book().delete(Common.RATING_INFORMATION_KEY);
                                            }
                                        }
                                    });

                                }
                            })
                            .setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setNeutralButton("NEVER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Paper.init(context);
                                    Paper.book().delete(Common.RATING_INFORMATION_KEY);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    public enum TOKEN_TYPE {
        CLIENT,
        MASTER,
        MANAGER
    }

    public static String formatShoppingItemName(String name) {
        return name.length() > 13 ? new StringBuilder(name.substring(0,10)).append("...").toString() : name;
    }

    public static void updateToken(Context context, String token) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            MyToken myToken = new MyToken();
            myToken.setToken(token);
            myToken.setTokenType(TOKEN_TYPE.CLIENT);
            myToken.setUserPhone(user.getPhoneNumber());

            FirebaseFirestore.getInstance()
                    .collection("Tokens")
                    .document(user.getPhoneNumber())
                    .set(myToken)
                    .addOnCompleteListener(task -> {

                    });
        } else {
            Paper.init(context);
            String myUser = Paper.book().read(Common.LOGGED_KEY);
            if (myUser != null) {
                if (!TextUtils.isEmpty(myUser)) {
                    MyToken myToken = new MyToken();
                    myToken.setToken(token);
                    myToken.setTokenType(TOKEN_TYPE.CLIENT);
                    myToken.setUserPhone(myUser);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(myUser)
                            .set(myToken)
                            .addOnCompleteListener(task -> {

                            });
                }
            }
        }
    }
}
