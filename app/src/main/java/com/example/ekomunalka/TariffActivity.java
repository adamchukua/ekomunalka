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

public class TariffActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private MainActivity mainActivity;
    private int id;
    private EditText name;
    private EditText price;
    private EditText comment;
    private Button saveTariff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tariff);

        db = new DatabaseHelper(this);
        mainActivity = new MainActivity();
        id = (int) getIntent().getLongExtra("id", -1);

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        comment = findViewById(R.id.comment_tariff);
        saveTariff = findViewById(R.id.saveTariff);

        getTariff();

        saveTariff.setOnClickListener(v -> {
            String nameStr = name.getText().toString();
            String priceStr = price.getText().toString();
            String commentStr = comment.getText().toString();

            Map<String, String> values = new HashMap<>();

            values.put("name", nameStr);
            values.put("price", priceStr);
            values.put("comment", commentStr);

            if (!nameStr.isEmpty() && !priceStr.isEmpty()) {
                updateTariff(values);
            } else {
                mainActivity.Toast(this, "Введіть значення!", false);
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
                    .setTitle("Видалити тариф?")
                    .setMessage("Ви дійсно хочете видалити цей тариф?")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes, (arg0, arg1) ->
                            deleteTariff())
                    .create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteTariff() {
        if (db.deleteTariff(id)) {
            mainActivity.Toast(this, "Нагадування видалено!", false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            TariffActivity.super.onBackPressed();
        } else {
            mainActivity.Toast(this, "Щось пішло не так...", true);
        }
    }

    public void updateTariff(Map<String, String> newValues) {
        boolean insertData = db.updateTariff(newValues, id);

        if (insertData) {
            mainActivity.Toast(this, "Дані оновлено!", false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            TariffActivity.super.onBackPressed();
        } else {
            mainActivity.Toast(this, "Щось пішло не так...", true);
        }
    }

    public void getTariff() {
        Cursor receivedTariff = db.getTariff(id);

        while (receivedTariff.moveToNext()) {
            name.setText(receivedTariff.getString(1));
            price.setText(receivedTariff.getString(2));
            comment.setText(receivedTariff.getString(3));
        }
    }
}