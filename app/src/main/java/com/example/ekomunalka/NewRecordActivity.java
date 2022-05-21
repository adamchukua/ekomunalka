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

import java.util.Calendar;

public class NewRecordActivity extends AppCompatActivity {

    DatabaseHelper db;
    Button addData;
    EditText currentReadings;
    Spinner chooseService;
    CheckBox isPaid;
    EditText commentText;

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

        addData.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            String date = c.get(Calendar.MONTH) + "." + c.get(Calendar.YEAR);
            String service = chooseService.getSelectedItem().toString();;
            int current = Integer.parseInt(currentReadings.getText().toString());
            int paid = isPaid.isChecked() ? 1 : 0;
            String comment = commentText.getText().toString();

            Object[] newEntries = {
                    date,
                    service,
                    current,
                    paid,
                    comment
            };

            if (date.length() != 0 && service.length() != 0) {
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