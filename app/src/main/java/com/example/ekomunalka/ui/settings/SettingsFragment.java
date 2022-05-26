package com.example.ekomunalka.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ekomunalka.DatabaseHelper;
import com.example.ekomunalka.MainActivity;
import com.example.ekomunalka.R;
import com.example.ekomunalka.TariffActivity;
import com.example.ekomunalka.TariffsActivity;
import com.example.ekomunalka.databinding.FragmentSettingsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private DatabaseHelper db;
    private MainActivity mainActivity;
    private ListView info;
    private ListView settings;
    private SimpleAdapter infoAdapter;
    private SimpleAdapter settingsAdapter;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    /*if (data != null) {
                        if (data.getIntExtra("result", -1) == 1) {
                            RefreshListOfRecords();
                        }
                    }*/
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[][] infoItems =
                {{"Версія", getActivity().getResources().getString(R.string.version)},
                {"Розробник", "@thegradle"}};
        String[][] settingsItems =
                {{"Тарифи", "Додати, змінити чи видалити тарифи"},
                {"Скинути дані", "Всі записи та тарифи будуть видалені"}};

        db = new DatabaseHelper(getContext());
        mainActivity = new MainActivity();

        info = view.findViewById(R.id.informationList);
        settings = view.findViewById(R.id.settingsList);

        List<Map<String, String>> infoList = new ArrayList<>();
        List<Map<String, String>> settingsList = new ArrayList<>();

        for (String[] infoItem : infoItems) {
            Map<String, String> item = new HashMap<>();
            item.put("title", infoItem[0]);
            item.put("subtitle", infoItem[1]);
            infoList.add(item);
        }
        for (String[] settingsItem : settingsItems) {
            Map<String, String> item = new HashMap<>();
            item.put("title", settingsItem[0]);
            item.put("subtitle", settingsItem[1]);
            settingsList.add(item);
        }

        infoAdapter = new SimpleAdapter(
                getActivity(),
                infoList,
                R.layout.mylist1,
                new String[] { "title", "subtitle" },
                new int[] { R.id.title, R.id.subtitle }
        );
        settingsAdapter = new SimpleAdapter(
                getActivity(),
                settingsList,
                R.layout.mylist1,
                new String[] { "title", "subtitle" },
                new int[] { R.id.title, R.id.subtitle }
        );

        info.setAdapter(infoAdapter);
        settings.setAdapter(settingsAdapter);

        settings.setOnItemClickListener((parent, view1, position, id) -> {
            if (position == 0) {
                Intent intent = new Intent(getActivity(), TariffsActivity.class);
                activityLauncher.launch(intent);
            } else if (position == 1) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Видалити всі дані?")
                        .setMessage("Ви дійсно хочете видалити всі записи та всі тарифи?")
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.yes, (arg0, arg1) ->
                                clearData())
                        .create().show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void clearData() {
        db.clearData();
        mainActivity.Toast(getActivity(), "Дані очищено!", true);
    }
}