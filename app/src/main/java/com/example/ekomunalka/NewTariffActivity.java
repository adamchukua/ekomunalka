package com.example.ekomunalka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class NewTariffActivity extends AppCompatActivity {

    DatabaseHelper db;
    MainActivity mainActivity;
    String name;
    String price;
    String comment;
    Button saveTariff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tariff);

        db = new DatabaseHelper(this);
        mainActivity = new MainActivity();
        saveTariff = findViewById(R.id.saveTariff);

        saveTariff.setOnClickListener(v -> {
            name = ((EditText) findViewById(R.id.name)).getText().toString();
            price = ((EditText) findViewById(R.id.price)).getText().toString();
            comment = ((EditText) findViewById(R.id.comment_tariff)).getText().toString();

            Map<String, String> values = new HashMap<>();

            values.put("name", name);
            values.put("price", price);
            values.put("comment", comment);

            if (!name.isEmpty() && !price.isEmpty()) {
                AddData(values);
            } else {
                mainActivity.Toast(NewTariffActivity.this, "Введіть значення!", false);
            }
        });
    }

    public void AddData(Map<String, String> values) {
        try {
            db.addTariff(values);
        }
        catch (SQLiteConstraintException e) {
            mainActivity.Toast(this,
                    "Тариф \"" + values.get("name") + "\" вже існує", true);

            return;
        }
        catch (Exception e) {
            mainActivity.Toast(this, "Щось пішло не так...", true);

            return;
        }

        mainActivity.Toast(this, "Дані додані!", false);
        Intent intent = new Intent(this, NewRecordActivity.class);
        intent.putExtra("result", 1);
        setResult(RESULT_OK, intent);
        NewTariffActivity.super.onBackPressed();
    }
}