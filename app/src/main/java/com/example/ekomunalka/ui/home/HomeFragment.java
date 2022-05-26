package com.example.ekomunalka.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ekomunalka.DatabaseHelper;
import com.example.ekomunalka.MainActivity;
import com.example.ekomunalka.NewRecordActivity;
import com.example.ekomunalka.R;
import com.example.ekomunalka.RecordActivity;
import com.example.ekomunalka.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    MainActivity mainActivity;
    DatabaseHelper db;
    ListView listView;
    private TextView empty;
    FloatingActionButton openNewRecordActivity;
    SimpleCursorAdapter adapter;
    String previousDate;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        if (data.getIntExtra("result", -1) == 1) {
                            RefreshListOfRecords();
                        }
                    }
                }
            });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHelper(getContext());
        mainActivity = new MainActivity();
        listView = view.findViewById(R.id.listView);
        empty = view.findViewById(R.id.empty);
        openNewRecordActivity = view.findViewById(R.id.openNewRecordActivity);

        RefreshListOfRecords();

        openNewRecordActivity.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewRecordActivity.class);
            activityLauncher.launch(intent);
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), RecordActivity.class);
            intent.putExtra("id", id);
            activityLauncher.launch(intent);
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void RefreshListOfRecords() {
        Cursor data = db.getRecords();
        previousDate = "";

        adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.mylist,
                data,
                new String[] { "service", "paid", "sum", "service", "date" },
                new int[] { R.id.title, R.id.subtitle, R.id.resultSum, R.id.icon, R.id.date },
                0
        );

        adapter.setViewBinder((view, cursor, columnIndex) -> {
            String field = cursor.getString(columnIndex);

            if (view.getId() == R.id.icon) {
                switch (field) {
                    case "Вода":
                        ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_water_svgrepo_com));
                        break;
                    case "Газ":
                        ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_gas_svgrepo_com));
                        break;
                    case "Електроенергія":
                        ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_electricity_svgrepo_com));
                        break;
                }

                return true;
            } else if (view.getId() == R.id.subtitle) {
                switch (field) {
                    case "0":
                        ((TextView) view).setText("Не сплачено");
                        break;
                    case "1":
                        ((TextView) view).setText("Сплачено");
                        break;
                }

                return true;
            } else if (view.getId() == R.id.date) {
                int month = Integer.parseInt(field.substring(0, field.length() - 5));

                if (field.equals(previousDate)) {
                    view.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,0,0);
                    params.height = 0;
                    view.setLayoutParams(params);
                }

                ((TextView) view).setText(mainActivity.months[month].toLowerCase(Locale.ROOT) +
                        " " + field.substring(field.length() - 4));
                previousDate = field;

                return true;
            }

            return false;
        });

        listView.setEmptyView(empty);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}