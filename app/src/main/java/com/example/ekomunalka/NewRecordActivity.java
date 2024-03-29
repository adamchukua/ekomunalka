package com.example.ekomunalka;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class NewRecordActivity extends AppCompatActivity {

    DatabaseHelper db;
    MainActivity mainActivity;
    RecordActivity recordActivity;

    Spinner chooseMonth;
    Spinner chooseService;
    Spinner chooseTariff;
    EditText previousReadings;
    EditText currentReadings;
    EditText transportationFee;
    EditText commentText;
    TextView sum;
    CheckBox isPaid;
    Button save;

    String[] tariffs;
    Cursor tariffs_db;
    int tariff_id;

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

                    chooseTariff.setSelection(0);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        db = new DatabaseHelper(NewRecordActivity.this);
        mainActivity = new MainActivity();
        recordActivity = new RecordActivity();

        chooseMonth = findViewById(R.id.chooseMonth);
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mainActivity.months);
        chooseMonth.setAdapter(monthsAdapter);
        chooseMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        chooseService = findViewById(R.id.chooseService);
        ArrayAdapter<String> servicesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mainActivity.services);
        chooseService.setAdapter(servicesAdapter);

        chooseTariff = findViewById(R.id.chooseTariff);
        tariffs_db = db.getTariffs();
        tariffs = refreshListOfTariffs();
        tariff_id = -1;

        previousReadings = findViewById(R.id.previous);
        currentReadings = findViewById(R.id.current);
        transportationFee = findViewById(R.id.transportationFee);
        commentText = findViewById(R.id.comment);
        sum = findViewById(R.id.sum);
        isPaid = findViewById(R.id.isPaid);
        sum.setText(getString(R.string.sum_value, 0.f));
        save = findViewById(R.id.saveRecord);

        readingsValidate();

        AdapterView.OnItemSelectedListener validateAndCalc = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                readingsValidate();
                sumCalculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        TextWatcher calc = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                sumCalculate();
            }
        };

        chooseMonth.setOnItemSelectedListener(validateAndCalc);
        chooseService.setOnItemSelectedListener(validateAndCalc);
        chooseTariff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == (tariffs.length - 1)) {
                    Intent intent = new Intent(NewRecordActivity.this, NewTariffActivity.class);
                    activityLauncher.launch(intent);
                } else {
                    readingsValidate();
                    sumCalculate();
                    tariff_id = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        currentReadings.setOnClickListener(v -> {
            if (!readingsValidate()) {
                mainActivity.toast(NewRecordActivity.this,
                        "Спочатку оберіть сервіс, тариф та місяць!", true);
            }
        });

        previousReadings.setOnClickListener(v -> {
            if (!readingsValidate()) {
                mainActivity.toast(NewRecordActivity.this,
                        "Спочатку оберіть сервіс, тариф та місяць!", true);
            }
        });

        previousReadings.addTextChangedListener(calc);
        currentReadings.addTextChangedListener(calc);
        transportationFee.addTextChangedListener(calc);

        save.setOnClickListener(v -> {
            String date = Arrays.asList(mainActivity.months)
                    .indexOf(chooseMonth.getSelectedItem().toString()) + "." + Calendar.getInstance().get(Calendar.YEAR);
            String service = chooseService.getSelectedItem().toString();
            String current = currentReadings.getText().toString();
            String paid = isPaid.isChecked() ? "1" : "0";
            String comment = commentText.getText().toString();
            String sum_result = sum.getText().toString().substring(0, sum.length() - 4);
            String sum_transportationFee = transportationFee.getText().toString();
            String tariff = String.valueOf(tariff_id);

            Map<String, String> newEntries = recordActivity.getLocalData(date, service, current, paid, sum_transportationFee, sum_result, tariff, comment);

            if (!Objects.requireNonNull(newEntries.get("current")).isEmpty() && sumCalculate()) {
                addData(newEntries);
            } else {
                mainActivity.toast(this, "Введіть значення!", false);
            }
        });
    }

    public void addData(Map<String, String> newEntries) {
        try {
            db.addRecord(newEntries);
        } catch (SQLiteConstraintException e) {
            mainActivity.toast(this,
                    "Сервіс \"" + newEntries.get("service") + "\" вже записаний в цьому місяці", true);

            return;
        } catch (Exception e) {
            mainActivity.toast(this, "Щось пішло не так...", true);
            Log.d("addDataError", e.getMessage());

            return;
        }

        mainActivity.toast(this, "Запис додано!", false);
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

        tariffs = listOfTariffs.toArray(new String[0]);
        ArrayAdapter<String> tariffsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, tariffs);
        chooseTariff.setAdapter(tariffsAdapter);

        return tariffs;
    }

    public boolean readingsValidate() {
        String service = chooseService.getSelectedItem().toString();
        String tariff = chooseTariff.getSelectedItem().toString();
        String date = chooseMonth.getSelectedItem().toString();
        String previous = previousReadings.getText().toString();
        String previousDate = Arrays.asList(mainActivity.months)
                .indexOf(date) - 1 + "." + Calendar.getInstance().get(Calendar.YEAR);

        boolean result = !service.equals("Оберіть сервіс:") &&
                !tariff.equals("Оберіть тариф:") &&
                !date.equals("Оберіть місяць:");

        currentReadings.setFocusableInTouchMode(result);
        currentReadings.setFocusable(result);
        previousReadings.setFocusableInTouchMode(result);
        previousReadings.setFocusable(result);

        if (result && previous.isEmpty()) {
            previousReadings.setText(String.valueOf(db.getRecordPrevious(service, previousDate)));
        }

        return result;
    }

    public boolean sumCalculate() {
        String service = chooseService.getSelectedItem().toString();
        String tariff = chooseTariff.getSelectedItem().toString();
        String date = chooseMonth.getSelectedItem().toString();
        String transportation = transportationFee.getText().toString();
        int previous;
        int current;
        float price;
        float sum_result;

        if (service.equals("Оберіть сервіс:") ||
                tariff.equals("Оберіть тариф:") ||
                date.equals("Оберіть місяць:")) return false;

        if (previousReadings.getText().toString().isEmpty()) return false;

        price = db.getTariffPrice(tariff);

        previous = Integer.parseInt(previousReadings.getText().toString());
        try {
            current = Integer.parseInt(currentReadings.getText().toString());
        } catch (NumberFormatException e) {
            sum.setText(getString(R.string.sum_value, 0.f));
            return true;
        }

        sum_result = (current - previous) * price;
        sum_result += !transportation.isEmpty() ? Float.parseFloat(transportation) : 0;
        sum.setText(getString(R.string.sum_value, sum_result));
        return true;
    }
}