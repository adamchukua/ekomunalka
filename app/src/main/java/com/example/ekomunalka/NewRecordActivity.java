package com.example.ekomunalka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class NewRecordActivity extends AppCompatActivity {

    DatabaseHelper db;
    MainActivity mainActivity;
    RecordActivity recordActivity;
    EditText currentReadings;
    Spinner chooseTariff;
    Spinner chooseService;
    Spinner chooseMonth;
    CheckBox isPaid;
    EditText commentText;
    TextView sum;
    Button saveData;
    String[] tariffs;

    Cursor tariffs_db;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        if (data.getIntExtra("result", -1) == 1) {
                            tariffs_db = db.getTariffs();
                            refreshListOfTariffs();
                        }
                    }
                }
            });

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
        saveData = findViewById(R.id.saveRecord);
        commentText = findViewById(R.id.comment);
        chooseMonth = findViewById(R.id.chooseMonth);
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mainActivity.months);
        chooseMonth.setAdapter(monthsAdapter);
        chooseMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        sum = findViewById(R.id.sum);
        chooseTariff = findViewById(R.id.chooseTariff);
        tariffs_db = db.getTariffs();
        tariffs = refreshListOfTariffs();

        sum.setText("0 грн");

        chooseTariff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == (tariffs.length - 1)) {
                    Intent intent = new Intent(NewRecordActivity.this, NewTariffActivity.class);
                    activityLauncher.launch(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currentReadings.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String service = chooseService.getSelectedItem().toString();
                String tariff = chooseTariff.getSelectedItem().toString();
                String previousDate = Arrays.asList(mainActivity.months)
                        .indexOf(chooseMonth.getSelectedItem().toString()) - 1 + "." + Calendar.getInstance().get(Calendar.YEAR);

                if (service.equals("Оберіть сервіс:") || tariff.equals("Оберіть тариф:")) {
                    return;
                }

                int previous = db.getRecordPrevious(service, previousDate);
                int current = Integer.parseInt(currentReadings.getText().toString());
                int price = db.getTariffPrice(tariff);

                sum.setText(String.valueOf((current - previous) * price));
            }
        });

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
            db.addRecord(newEntries);
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

    public String[] refreshListOfTariffs() {
        ArrayList<String> listOfTariffs = new ArrayList<>();

        listOfTariffs.add("Оберіть тариф:");
        while (tariffs_db.moveToNext()) {
            listOfTariffs.add(tariffs_db.getString(1));
        }
        listOfTariffs.add("Додати новий тариф");

        String[] tariffs = listOfTariffs.toArray(new String[0]);
        ArrayAdapter<String> tariffsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tariffs);
        chooseTariff.setAdapter(tariffsAdapter);

        return tariffs;
    }
}