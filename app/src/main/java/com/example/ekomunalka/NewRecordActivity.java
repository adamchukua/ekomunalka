package com.example.ekomunalka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class NewRecordActivity extends AppCompatActivity {

    DatabaseHelper db;
    MainActivity mainActivity;
    RecordActivity recordActivity;
    Button saveData;
    EditText currentReadings;
    Spinner chooseService;
    Spinner chooseMonth;
    CheckBox isPaid;
    EditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        db = new DatabaseHelper(NewRecordActivity.this);
        mainActivity = new MainActivity();
        recordActivity = new RecordActivity();

        currentReadings = findViewById(R.id.current);
        chooseService = findViewById(R.id.chooseService);
        ArrayAdapter<String> servicesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mainActivity.services);
        chooseService.setAdapter(servicesAdapter);
        isPaid = findViewById(R.id.isPaid);
        saveData = findViewById(R.id.saveData);
        commentText = findViewById(R.id.comment);
        chooseMonth = findViewById(R.id.chooseMonth);
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mainActivity.months);
        chooseMonth.setAdapter(monthsAdapter);
        chooseMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        saveData.setOnClickListener(v -> {
            String date = Arrays.asList(mainActivity.months)
                    .indexOf(chooseMonth.getSelectedItem().toString()) + "." + Calendar.getInstance().get(Calendar.YEAR);
            String service = chooseService.getSelectedItem().toString();
            String current = currentReadings.getText().toString();
            String paid = isPaid.isChecked() ? "1" : "0";
            String comment = commentText.getText().toString();

            Map<String, String> newEntries = recordActivity.GetDataFromLocal(date, service, current, paid, comment);

            if (!Objects.requireNonNull(newEntries.get("current")).isEmpty()) {
                AddData(newEntries);
            } else {
                mainActivity.Toast(this, "Введіть значення!", false);
            }
        });
    }

    public void AddData(Map<String, String> newEntries) {
        try {
            db.addData(newEntries);
        }
        catch (SQLiteConstraintException e) {
            mainActivity.Toast(this,
                    "Сервіс \"" + newEntries.get("service") + "\" вже записаний в цьому місяці", true);

            return;
        }
        catch (Exception e) {
            mainActivity.Toast(this, "Щось пішло не так...", true);

            return;
        }

        mainActivity.Toast(this, "Дані додані!", false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("result", 1);
        setResult(RESULT_OK, intent);
        NewRecordActivity.super.onBackPressed();
    }
}