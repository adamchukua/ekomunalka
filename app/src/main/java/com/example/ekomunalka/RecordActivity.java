package com.example.ekomunalka;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

public class RecordActivity extends AppCompatActivity {

    DatabaseHelper db;
    TextView textView;
    int id;
    Cursor receivedItem;
    Button saveData;
    EditText currentReadings;
    Spinner chooseService;
    CheckBox isPaid;
    EditText commentText;
    Spinner chooseMonth;

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

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setTitle("Редагувати запис");

        db = new DatabaseHelper(this);
        currentReadings = findViewById(R.id.current);
        chooseService = findViewById(R.id.chooseService);
        String[] items = new String[]{"Вода", "Газ", "Електроенергія"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        chooseService.setAdapter(adapter);
        isPaid = findViewById(R.id.isPaid);
        saveData = findViewById(R.id.saveData);
        commentText = findViewById(R.id.comment);

        Intent receivedIntent = getIntent();
        id = (int) receivedIntent.getLongExtra("id", -1);
        receivedItem = db.getItem(id);
        Object[] data = new Object[5];

        while(receivedItem.moveToNext()) {
            data[0] = receivedItem.getString(receivedItem.getColumnIndex("DATE"));
            data[1] = receivedItem.getString(receivedItem.getColumnIndex("SERVICE"));
            data[2] = receivedItem.getString(receivedItem.getColumnIndex("CURRENT"));
            data[3] = receivedItem.getString(receivedItem.getColumnIndex("PAID"));
            data[4] = receivedItem.getString(receivedItem.getColumnIndex("COMMENT"));
        }

        int chosenServiceId = -1;
        if (data[1].equals("Вода")) {
            chosenServiceId = 0;
        } else if (data[1].equals("Газ")) {
            chosenServiceId = 1;
        } else {
            chosenServiceId = 2;
        }
        chooseService.setSelection(chosenServiceId);
        currentReadings.setText(String.valueOf(data[2]));
        commentText.setText((String) data[4]);
        isPaid.setChecked(Integer.parseInt((String) data[3]) == 1);

        chooseMonth = findViewById(R.id.chooseMonth);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        chooseMonth.setAdapter(adapter1);
        chooseMonth.setSelection(Integer.parseInt(((String) data[0]).substring(0, ((String) data[0]).length() - 5)));

        saveData.setOnClickListener(v -> {
            String date = Arrays.asList(months).indexOf(chooseMonth.getSelectedItem().toString()) + "." + ((String) data[0]).substring(((String) data[0]).length() - 4);
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

            if (!date.equals(data[0]) || !service.equals(data[1]) || current != Integer.parseInt(String.valueOf(data[2])) || paid != Integer.parseInt(String.valueOf(data[3])) || !comment.equals(data[4])) {
                UpdateData(newEntries, id);
            } else {
                if (current == 0) {
                    Toast.makeText(RecordActivity.this, "Ви маєте вписати дані щоб їх додати!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RecordActivity.this, "Дані не змінено!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void UpdateData(Object[] newEntries, int id) {
        boolean insertData = db.UpdateData(newEntries, id);

        if (insertData) {
            Toast.makeText(RecordActivity.this, "Дані оновлено!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            RecordActivity.super.onBackPressed();
        } else {
            Toast.makeText(RecordActivity.this, "Щось пішло не так 🤔", Toast.LENGTH_LONG).show();
        }
    }
}