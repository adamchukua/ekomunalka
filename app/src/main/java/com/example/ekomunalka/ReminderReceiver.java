package com.example.ekomunalka;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {

    private int id;
    private String title;
    private String subtitle;
    private int day;
    private Calendar calendar;

    @Override
    public void onReceive(Context context, Intent intent) {
        id = intent.getIntExtra("id", -1);
        title = intent.getStringExtra("title");
        subtitle = intent.getStringExtra("subtitle");
        day = intent.getIntExtra("day", -1);
        calendar = Calendar.getInstance();

        if (calendar.get(Calendar.DAY_OF_MONTH) == day || calendar.get(Calendar.HOUR_OF_DAY) == 9) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Нагадування")
                    .setContentTitle(title)
                    .setContentText(subtitle)
                    .setSmallIcon(R.drawable.ic_stat_logo)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(subtitle));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(id, builder.build());
        }
    }
}