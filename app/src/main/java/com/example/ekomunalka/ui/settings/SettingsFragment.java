package com.example.ekomunalka.ui.settings;

import android.database.Cursor;
import android.hardware.lights.LightState;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ekomunalka.DatabaseHelper;
import com.example.ekomunalka.MainActivity;
import com.example.ekomunalka.R;
import com.example.ekomunalka.databinding.FragmentSettingsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private DatabaseHelper db;
    private MainActivity mainActivity;
    private ListView settings;
    private SimpleAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[][] settingsItems =
                {{"Версія", getActivity().getResources().getString(R.string.version)},
                {"Розробник", "@thegradle"}};

        db = new DatabaseHelper(getContext());
        mainActivity = new MainActivity();
        settings = view.findViewById(R.id.settingsList);

        List<Map<String, String>> settingsList = new ArrayList<>();

        for (String[] settingsItem : settingsItems) {
            Map<String, String> item = new HashMap<>();
            item.put("title", settingsItem[0]);
            item.put("subtitle", settingsItem[1]);
            settingsList.add(item);
        }

        adapter = new SimpleAdapter(
                getActivity(),
                settingsList,
                R.layout.mylist1,
                new String[] { "title", "subtitle" },
                new int[] { R.id.title, R.id.subtitle }
        );

        settings.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}