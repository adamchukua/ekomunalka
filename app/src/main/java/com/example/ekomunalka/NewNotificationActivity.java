package com.example.ekomunalka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

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
        if (db.addNotification(data)) {
            mainActivity.Toast(this, "Дані додані!", false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            NewNotificationActivity.super.onBackPressed();
        } else {
            mainActivity.Toast(this, "Щось пішло не так...", true);
        }
    }
}