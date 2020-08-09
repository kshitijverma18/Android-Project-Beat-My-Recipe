package com.example.kshitijverma.beat_my_recipie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import Adapters.NotificationAdapter;
import Model.notification;

public class notification_activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<notification> mNotificationList;
    Toolbar toolbar;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_activity);
        recyclerView = findViewById(R.id.notification_recyclerview);
        recyclerView.setHasFixedSize(true);
        toolbar = findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHomeIntent = new Intent(notification_activity.this, MainActivity.class);
                startActivity(toHomeIntent);
                Log.i("checking finish", "checking finish");
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mNotificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, mNotificationList);
        recyclerView.setAdapter(notificationAdapter);
        readNotifications();
    }

    private void readNotifications() {
        FirebaseDatabase.getInstance().getReference().child("notifications").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot snap : snapshot.getChildren()) {

                    notification notification = snap.getValue(Model.notification.class);
                    if (TextUtils.equals("delete", getTimeAgo(notification.getDate()))) {
                        FirebaseDatabase.getInstance().getReference().child("notifications").child(notification.
                                getUserId()).child(notification.getPostId()).removeValue();
                    } else {
                        //mNotificationList.add(snap.getValue(notification.class));
                        mNotificationList.add(snap.getValue(notification.class));
                    }


                }
                Collections.reverse(mNotificationList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(Date date) {
        long time = date.getTime();
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return "in the future";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {

            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 31 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else {
            return "delete";
        }

    }
    @Override
    public void onBackPressed()
    {
        Intent toHomeIntent=new Intent(notification_activity.this,MainActivity.class);
        startActivity(toHomeIntent);
        finish();
    }

}
