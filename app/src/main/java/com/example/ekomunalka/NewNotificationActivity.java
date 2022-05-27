package com.example.ekomunalka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewNotificationActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private MainActivity mainActivity;
    private EditText title;
    private EditText subtitle;
    private EditText day;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        db = new DatabaseHelper(this);
        mainActivity = new MainActivity();

        title = findViewById(R.id.titleNotification);
        subtitle = findViewById(R.id.subtitleNotification);
        day = findViewById(R.id.dayNotification);
        save = findViewById(R.id.saveNotification);

        save.setOnClickListener(v -> {
            String titleStr = title.getText().toString();
            String subtitleStr = subtitle.getText().toString();
            String dayStr = day.getText().toString();

            Map<String, String> data = new HashMap<>();

            data.put("title", titleStr);
            data.put("subtitle", subtitleStr);
            data.put("day", dayStr);

            if (!data.isEmpty()) {
                addNotification(data);
            } else {
                mainActivity.Toast(this, "Заповніть всі поля!", true);
            }
        });
    }

    public void addNotification(Map<String, String> data) {
        int id = db.addNotification(data);

        if (id != -1) {
            createNotification(data, id);
            mainActivity.Toast(this, "Нагадування створено!", false);
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.putExtra("result", 1);
            setResult(RESULT_OK, mainActivity);

            NewNotificationActivity.super.onBackPressed();
        } else {
            mainActivity.Toast(this, "Щось пішло не так...", true);
        }
    }

    public void createNotification(Map<String, String> data, int id) {
        createNotificationChannel();
        Calendar calendar = Calendar.getInstance();
        Intent reminderReceiver = new Intent(this, ReminderReceiver.class);
        reminderReceiver.putExtra("id", id);
        reminderReceiver.putExtra("title", data.get("title"));
        reminderReceiver.putExtra("subtitle", data.get("subtitle"));
        reminderReceiver.putExtra("day", Integer.parseInt(Objects.requireNonNull(data.get("day"))));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, reminderReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void createNotificationChannel() {
        CharSequence name = "Нагадування";
        String description = "Нагадування встановлені в розділі \"Нагадування\" для своєчасної сплати комуналки";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("Нагадування", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}