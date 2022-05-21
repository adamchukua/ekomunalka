package com.example.ekomunalka.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.ekomunalka.DatabaseHelper;
import com.example.ekomunalka.NewRecordActivity;
import com.example.ekomunalka.R;
import com.example.ekomunalka.RecordActivity;
import com.example.ekomunalka.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    ListView listView;
    DatabaseHelper db;
    FloatingActionButton openNewRecordActivity;
    int resultOfAddingNewRecord;
    SimpleCursorAdapter adapter;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        resultOfAddingNewRecord = data.getIntExtra("result", -1);

                        if (resultOfAddingNewRecord == 1) {
                            RefreshList();
                        }
                    }
                }
            });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView);
        db = new DatabaseHelper(getContext());
        openNewRecordActivity = view.findViewById(R.id.openNewRecordActivity);

        RefreshList();

        openNewRecordActivity.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewRecordActivity.class);
            activityLauncher.launch(intent);
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), RecordActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void RefreshList() {
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor data = db.getListContents();

        adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.mylist,
                data,
                new String[] { "SERVICE", "DATE", "SERVICE" },
                new int[] { R.id.title, R.id.subtitle, R.id.icon },
                0
        );

        adapter.setViewBinder((view, cursor, columnIndex) -> {
            if(view.getId() == R.id.icon) {
                if (cursor.getString(columnIndex).equals("Вода")) {
                    ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_water_svgrepo_com));
                } else if (cursor.getString(columnIndex).equals("Газ")) {
                    ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_gas_svgrepo_com));
                } else if (cursor.getString(columnIndex).equals("Електроенергія")) {
                    ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_electricity_svgrepo_com));
                }


                return true;
            }

            return false;
        });

        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}