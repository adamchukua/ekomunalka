package com.example.ekomunalka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private MainActivity mainActivity;
    private EditText title;
    private EditText subtitle;
    private EditText day;
    private Button save;
    private AlarmManager alarmManager;
    private int id;

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
        id = (int) getIntent().getLongExtra("id", -1);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        getNotification();

        save.setOnClickListener(v -> {
            String titleStr = title.getText().toString();
            String subtitleStr = subtitle.getText().toString();
            String dayStr = day.getText().toString();

            Map<String, String> data = new HashMap<>();

            data.put("title", titleStr);
            data.put("subtitle", subtitleStr);
            data.put("day", dayStr);

            if (!data.isEmpty()) {
                updateNotification(data);
            } else {
                mainActivity.Toast(this, "Заповніть всі поля!", true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.deleteRecord) {
            new AlertDialog.Builder(this)
                    .setTitle("Видалити нагадування?")
                    .setMessage("Ви дійсно хочете видалити це нагадування?")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes, (arg0, arg1) ->
                            deleteNotification())
                    .create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteNotification() {
        if (db.deleteNotification(id)) {
            cancelNotification(this, id);
            mainActivity.Toast(this, "Нагадування видалено!", false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            NotificationActivity.super.onBackPressed();
        } else {
            mainActivity.Toast(this, "Щось пішло не так...", true);
        }
    }

    public void cancelNotification(Context context, int id) {
        Intent reminderReceiver = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, reminderReceiver, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void getNotification() {
        Cursor notification = db.getNotification(id);

        while (notification.moveToNext()) {
            title.setText(notification.getString(1));
            subtitle.setText(notification.getString(2));
            day.setText(notification.getString(3));
        }
    }

    public void updateNotification(Map<String, String> newValues) {
        if (db.updateNotification(newValues, id)) {
            createNotification(newValues, id);
            mainActivity.Toast(this, "Дані оновлено!", false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            NotificationActivity.super.onBackPressed();
        } else {
            mainActivity.Toast(this, "Щось пішло не так...", true);
        }
    }

    public void createNotification(Map<String, String> data, int id) {
        Calendar calendar = Calendar.getInstance();
        Intent reminderReceiver = new Intent(this, ReminderReceiver.class);
        reminderReceiver.putExtra("id", id);
        reminderReceiver.putExtra("title", data.get("title"));
        reminderReceiver.putExtra("subtitle", data.get("subtitle"));
        reminderReceiver.putExtra("day", Integer.parseInt(Objects.requireNonNull(data.get("day"))));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, reminderReceiver, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}