package com.example.iot_project.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.iot_project.R;

import java.util.Date;

public class notification extends BroadcastReceiver {

    private static final String CHANNEL_ID = "201";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("hengio".equals(intent.getAction())) {
            String timePicker = intent.getStringExtra("timePicker");
            int area = intent.getIntExtra("area", 0);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription("Mieu ta kenh 1");
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(timePicker)
                        .setContentText("Khu vực " + area + " bắt đầu tưới")
                        .setColor(Color.RED)
                        .setSmallIcon(R.drawable.icon_notifications)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

                notificationManager.notify(getNotificationId(), builder.build());

            }
        } else if ("ketthuc".equals(intent.getAction())) {
            int area = intent.getIntExtra("area", 0);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel 2", NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription("Mieu ta kenh 2");
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Khu vực " +area +" kết thúc tưới")
                        .setColor(Color.RED)
                        .setSmallIcon(R.drawable.icon_notifications)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

                notificationManager.notify(getNotificationId(), builder.build());
            }
        }
    }

    private int getNotificationId() {
        return (int) (new Date().getTime() % Integer.MAX_VALUE);
    }
}