package com.example.ekomunalka.ui.notifications;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Build;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.ekomunalka.DatabaseHelper;
import com.example.ekomunalka.NewNotificationActivity;
import com.example.ekomunalka.NotificationActivity;
import com.example.ekomunalka.R;
import com.example.ekomunalka.databinding.FragmentNotificationsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NotificationsFragment extends Fragment {

    private DatabaseHelper db;
    private FragmentNotificationsBinding binding;
    private FloatingActionButton openNewNotificationActivity;
    private ListView notificationList;
    private TextView empty;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        if (data.getIntExtra("result", -1) == 1) {
                            refreshListOfNotifications();
                        }
                    }
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHelper(getActivity());

        openNewNotificationActivity = view.findViewById(R.id.openNewNotificationActivity);
        notificationList = view.findViewById(R.id.notificationList);
        empty = view.findViewById(R.id.emptyNotifications);

        openNewNotificationActivity.setOnClickListener(v -> {
            Intent newNotificationActivity = new Intent(getActivity(), NewNotificationActivity.class);
            activityLauncher.launch(newNotificationActivity);
        });

        notificationList.setOnItemClickListener((parent, view12, position, id) -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            intent.putExtra("id", id);
            activityLauncher.launch(intent);
        });

        refreshListOfNotifications();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void refreshListOfNotifications() {
        Cursor data = db.getNotifications();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.mylist1,
                data,
                new String[] { "title", "day" },
                new int[] { R.id.title, R.id.subtitle },
                0
        );

        adapter.setViewBinder((view, cursor, columnIndex) -> {
            String field = cursor.getString(columnIndex);

            if (view.getId() == R.id.subtitle) {
                ((TextView) view).setText(getString(R.string.remindEveryNDay, field));

                return true;
            }

            return false;
        });

        notificationList.setEmptyView(empty);
        notificationList.setAdapter(adapter);
    }
}