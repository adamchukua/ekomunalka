package com.example.ekomunalka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TariffsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView tariffsList;
    private TextView empty;
    private FloatingActionButton openNewTariffActivity;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        if (data.getIntExtra("result", -1) == 1) {
                            refreshListOfTariffs();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tariffs);

        db = new DatabaseHelper(this);

        tariffsList = findViewById(R.id.tariffsList);
        openNewTariffActivity = findViewById(R.id.openNewTariffActivity);
        empty = findViewById(R.id.emptyTariffs);

        refreshListOfTariffs();

        openNewTariffActivity.setOnClickListener(v -> {
            Intent intent = new Intent(TariffsActivity.this, NewTariffActivity.class);
            activityLauncher.launch(intent);
        });

        tariffsList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(TariffsActivity.this, TariffActivity.class);
            intent.putExtra("id", id);
            activityLauncher.launch(intent);
        });
    }

    public void refreshListOfTariffs() {
        Cursor data = db.getTariffs();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.simple_list,
                data,
                new String[] { "name", "comment" },
                new int[] { R.id.title, R.id.subtitle },
                0
        );

        adapter.setViewBinder((view, cursor, columnIndex) -> {
            String field = cursor.getString(columnIndex);

            if (view.getId() == R.id.subtitle) {
                ((TextView) view).setText(getString(R.string.tariff_price, field));

                return true;
            }

            return false;
        });

        tariffsList.setEmptyView(empty);
        tariffsList.setAdapter(adapter);
    }
}