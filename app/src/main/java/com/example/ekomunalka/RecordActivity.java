package com.example.ekomunalka;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RecordActivity extends AppCompatActivity {

    DatabaseHelper db;
    MainActivity mainActivity;
    Spinner chooseMonth;
    Spinner chooseService;
    EditText currentReadings;
    EditText previousReadings;
    Spinner chooseTariff;
    CheckBox isPaid;
    EditText commentText;
    EditText transportationFee;
    TextView sum;
    Button saveData;
    String[] tariffs;

    Cursor receivedItem;
    Cursor tariffs_db;
    int id;
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
        setTitle("Редагувати запис");

        db = new DatabaseHelper(this);
        mainActivity = new MainActivity();
        currentReadings = findViewById(R.id.current);
        previousReadings = findViewById(R.id.previous);
        chooseService = findViewById(R.id.chooseService);
        ArrayAdapter<String> servicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mainActivity.services);
        chooseService.setAdapter(servicesAdapter);
        isPaid = findViewById(R.id.isPaid);
        saveData = findViewById(R.id.saveRecord);
        commentText = findViewById(R.id.comment);
        sum = findViewById(R.id.sum);
        transportationFee = findViewById(R.id.transportationFee);
        chooseTariff = findViewById(R.id.chooseTariff);
        tariffs_db = db.getTariffs();
        tariffs = refreshListOfTariffs();
        tariff_id = -1;

        Intent receivedIntent = getIntent();
        id = (int) receivedIntent.getLongExtra("id", -1);
        receivedItem = db.getRecord(id);
        tariffs_db = db.getTariffs();

        Map<String, String> data = GetDataFromDB();
        int chosenServiceId = GetServiceId(Objects.requireNonNull(data.get("service")));

        chooseMonth = findViewById(R.id.chooseMonth);
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mainActivity.months);
        chooseMonth.setAdapter(monthsAdapter);
        chooseMonth.setSelection(Integer.parseInt(Objects.requireNonNull(data.get("date")).substring(0, Objects.requireNonNull(data.get("date")).length() - 5)));
        chooseService.setSelection(chosenServiceId);
        chooseTariff.setSelection(Integer.parseInt(Objects.requireNonNull(data.get("tariff_id"))));
        currentReadings.setText(data.get("current"));
        isPaid.setChecked(Objects.equals(data.get("paid"), "1"));
        commentText.setText(data.get("comment"));
        sum.setText("0 грн");
        transportationFee.setText(data.get("transportationFee"));

        readingsValidate();
        sumCalculate();

        chooseMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                readingsValidate();
                sumCalculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        chooseService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                readingsValidate();
                sumCalculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        chooseTariff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == (tariffs.length - 1)) {
                    Intent intent = new Intent(RecordActivity.this, NewTariffActivity.class);
                    activityLauncher.launch(intent);
                } else {
                    readingsValidate();
                    sumCalculate();
                    tariff_id = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        currentReadings.setOnClickListener(v -> {
            if (!readingsValidate()) {
                mainActivity.toast(RecordActivity.this,
                        "Спочатку оберіть сервіс, тариф та місяць!", true);
            }
        });

        previousReadings.setOnClickListener(v -> {
            if (!readingsValidate()) {
                mainActivity.toast(RecordActivity.this,
                        "Спочатку оберіть сервіс, тариф та місяць!", true);
            }
        });

        previousReadings.addTextChangedListener(new TextWatcher() {
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
                sumCalculate();
            }
        });

        transportationFee.addTextChangedListener(new TextWatcher() {
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
        });

        saveData.setOnClickListener(v -> {
            String date = Arrays.asList(mainActivity.months)
                    .indexOf(chooseMonth.getSelectedItem().toString()) + "." + Objects.requireNonNull(data.get("date")).substring(Objects.requireNonNull(data.get("date")).length() - 4);
            String service = chooseService.getSelectedItem().toString();
            String current = currentReadings.getText().toString();
            String paid = isPaid.isChecked() ? "1" : "0";
            String comment = commentText.getText().toString();
            String sum_result = sum.getText().toString().substring(0, sum.length() - 4);
            String sum_transportationFee = transportationFee.getText().toString();
            String tariff = String.valueOf(tariff_id);

            Map<String, String> newValues = GetDataFromLocal(date, service, current, paid, sum_transportationFee, sum_result, tariff, comment);

            if (!data.equals(newValues) && sumCalculate()) {
                UpdateData(newValues, id);
            } else {
                if (Objects.requireNonNull(newValues.get("service")).isEmpty()) {
                    mainActivity.toast(this, "Введіть значення!", true);
                } else {
                    mainActivity.toast(this, "Дані ті самі!", true);
                }
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
                    .setTitle("Видалити запис?")
                    .setMessage("Ви дійсно хочете видалити цей запис?")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes, (arg0, arg1) ->
                            deleteRecord())
                    .create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteRecord() {
        if (db.deleteRecord(id)) {
            mainActivity.toast(this, "Запис видалено!", false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);
            RecordActivity.super.onBackPressed();
        } else {
            mainActivity.toast(this, "Щось пішло не так...", true);
        }
    }

    public void UpdateData(Map<String, String> newValues, int id) {
        try {
            db.updateRecord(newValues, id);
        } catch (ParseException e) {
            e.printStackTrace();
            mainActivity.toast(this, "Щось пішло не так...", true);
            return;
        }

        mainActivity.toast(this, "Дані оновлено!", false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("result", 1);
        setResult(RESULT_OK, intent);
        RecordActivity.super.onBackPressed();
    }

    public Map<String, String> GetDataFromDB() {
        Map<String, String> data = new HashMap<>();

        while (receivedItem.moveToNext()) {
            data.put("date", receivedItem.getString(1));
            data.put("service", receivedItem.getString(2));
            data.put("current", receivedItem.getString(3));
            data.put("paid", receivedItem.getString(4));
            data.put("transportationFee", receivedItem.getString(5));
            data.put("sum", receivedItem.getString(6));
            data.put("tariff_id", receivedItem.getString(7));
            data.put("comment", receivedItem.getString(8));
        }

        return data;
    }

    public Map<String, String> GetDataFromLocal(String date, String service, String current, String paid, String transportationFee, String sum, String tariff_id, String comment) {
        Map<String, String> data = new HashMap<>();

        data.put("date", date);
        data.put("service", service);
        data.put("current", current);
        data.put("paid", paid);
        data.put("transportationFee", transportationFee);
        data.put("sum", sum);
        data.put("tariff_id", tariff_id);
        data.put("comment", comment);

        return data;
    }

    public int GetServiceId(String service) {
        int id = -1;

        switch (service) {
            case "Вода":
                id = 1;
                break;
            case "Газ":
                id = 2;
                break;
            case "Електроенергія":
                id = 3;
                break;
        }

        return id;
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