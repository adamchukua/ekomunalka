package com.example.ekomunalka.ui.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ekomunalka.MainActivity;
import com.example.ekomunalka.R;
import com.example.ekomunalka.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationManager notificationManager;
    private Button notifyBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = (NotificationManager) getActivity().getSystemService(Context
                .NOTIFICATION_SERVICE);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        notifyBtn = view.findViewById(R.id.button);
        notifyBtn.setOnClickListener(view1 -> {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "My Notification")
                    .setContentTitle("Тестове повідомлення")
                    .setContentText("Привіт! Дякую що натиснув на цю кнопочку, ось тобі тестовий текст повідомлення")
                    .setSmallIcon(R.drawable.ic_stat_logo)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Привіт! Дякую що натиснув на цю кнопочку, ось тобі тестовий текст повідомлення"));

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity());
            managerCompat.notify(1, builder.build());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}