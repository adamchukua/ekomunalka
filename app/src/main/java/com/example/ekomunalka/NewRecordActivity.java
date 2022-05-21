package com.example.ekomunalka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

public class NewRecordActivity extends AppCompatActivity {

    DatabaseHelper db;
    Button addData;
    EditText currentReadings;
    Spinner chooseService;
    Spinner chooseMonth;
    CheckBox isPaid;
    EditText commentText;

    String[] months = {
        "Січень",
        "Лютий",
        "Березень",
        "Квітень",
        "Травень",
        "Червень",
        "Липень",
        "Серпень",
        "Вересень",
        "Жовтень",
        "Листопад",
        "Грудень"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        db = new DatabaseHelper(NewRecordActivity.this);
        currentReadings = findViewById(R.id.current);
        chooseService = findViewById(R.id.chooseService);
        String[] items = new String[]{"Вода", "Газ", "Електроенергія"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        chooseService.setAdapter(adapter);
        isPaid = findViewById(R.id.isPaid);
        addData = findViewById(R.id.saveData);
        commentText = findViewById(R.id.comment);
        chooseMonth = findViewById(R.id.chooseMonth);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        chooseMonth.setAdapter(adapter1);
        chooseMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        addData.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            String date = Arrays.asList(months).indexOf(chooseMonth.getSelectedItem().toString()) + "." + c.get(Calendar.YEAR);
            String service = chooseService.getSelectedItem().toString();;
            int current = currentReadings.getText().toString().isEmpty() ? 0 : Integer.parseInt(currentReadings.getText().toString());
            int paid = isPaid.isChecked() ? 1 : 0;
            String comment = commentText.getText().toString();

            Object[] newEntries = {
                    date,
                    service,
                    current,
                    paid,
                    comment
            };

            if (current != 0) {
                AddData(newEntries);
            } else {
                Toast.makeText(NewRecordActivity.this, "Ви маєте вписати дані щоб їх додати!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void AddData(Object[] newEntries) {
        boolean insertData = db.addData(newEntries);

        if (insertData) {
            Toast.makeText(NewRecordActivity.this, "Дані додані!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            NewRecordActivity.super.onBackPressed();
        } else {
            Toast.makeText(NewRecordActivity.this, "Щось пішло не так 🤔", Toast.LENGTH_LONG).show();
        }
    }
}