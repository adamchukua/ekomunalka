package com.example.ekomunalka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private MainActivity mainActivity;
    private EditText title;
    private EditText subtitle;
    private EditText day;
    private Button save;
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
            mainActivity.Toast(this, "Тариф видалено!", false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            NotificationActivity.super.onBackPressed();
        } else {
            mainActivity.Toast(this, "Щось пішло не так...", true);
        }
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
            mainActivity.Toast(this, "Дані оновлено!", false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            NotificationActivity.super.onBackPressed();
        } else {
            mainActivity.Toast(this, "Щось пішло не так...", true);
        }
    }
}