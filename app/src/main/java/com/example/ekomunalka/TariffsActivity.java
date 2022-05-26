package com.example.ekomunalka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class TariffsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView tariffsList;

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

        refreshListOfTariffs();

        tariffsList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent;

            if (id == 999) {
                intent = new Intent(TariffsActivity.this, NewTariffActivity.class);
            } else {
                intent = new Intent(TariffsActivity.this, TariffActivity.class);
                intent.putExtra("id", id);
            }

            activityLauncher.launch(intent);
        });
    }

    public void refreshListOfTariffs() {
        Cursor data = db.getTariffs();

        MatrixCursor matrixCursor = new MatrixCursor(new String[] { "_id", "name", "comment" });
        matrixCursor.addRow(new Object[] { "999", "Додати новий тариф", "Додайте новий тариф, ввівши назву та ціну за одиницю" });
        MergeCursor mergeCursor = new MergeCursor(new Cursor[] { matrixCursor, data });

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.mylist1,
                mergeCursor,
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

        tariffsList.setAdapter(adapter);
    }
}