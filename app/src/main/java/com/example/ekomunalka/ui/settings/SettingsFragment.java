package com.example.ekomunalka.ui.settings;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.ekomunalka.DatabaseHelper;
import com.example.ekomunalka.MainActivity;
import com.example.ekomunalka.R;
import com.example.ekomunalka.ReminderReceiver;
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
                {"Скинути дані", "Всі записи, тарифи та нагадування будуть видалені"},
                {"Тестове повідомлення", "Отримати тестове повідомлення"}};

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
                R.layout.simple_list,
                new String[] { "title", "subtitle" },
                new int[] { R.id.title, R.id.subtitle }
        );
        settingsAdapter = new SimpleAdapter(
                getActivity(),
                settingsList,
                R.layout.simple_list,
                new String[] { "title", "subtitle" },
                new int[] { R.id.title, R.id.subtitle }
        );


        info.setAdapter(infoAdapter);
        setListViewHeightBasedOnChildren(info);
        settings.setAdapter(settingsAdapter);
        setListViewHeightBasedOnChildren(settings);

        settings.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    Intent tariffsActivity = new Intent(getActivity(), TariffsActivity.class);
                    startActivity(tariffsActivity);
                    break;
                case 1:
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Видалити всі дані?")
                            .setMessage("Ви дійсно хочете видалити всі записи, всі тарифи та всі нагадування?")
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.yes, (arg0, arg1) ->
                                    clearData())
                            .create().show();
                    break;
                case 2:
                    NotificationChannel channel = new NotificationChannel(
                            "Тестове повідомлення",
                            "Тестове повідомлення",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(getActivity(), "Тестове повідомлення")
                                    .setContentTitle("Тестове повідомлення")
                                    .setContentText("Привіт! Дякую що натиснув на цю кнопочку, ось тобі тестовий текст повідомлення")
                                    .setSmallIcon(R.drawable.ic_stat_logo)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText("Привіт! Дякую що натиснув на цю кнопочку, ось тобі тестовий текст повідомлення"));

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity());
                    managerCompat.notify(1, builder.build());
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void clearData() {
        Cursor reminders = db.getNotifications();
        int id;
        while (reminders.moveToNext()) {
            id = Integer.parseInt(reminders.getString(0));
            Intent reminderReceiver = new Intent(getActivity(), ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, reminderReceiver, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);
        }

        db.clearData();
        mainActivity.toast(getActivity(), "Дані очищено!", true);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup)
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}