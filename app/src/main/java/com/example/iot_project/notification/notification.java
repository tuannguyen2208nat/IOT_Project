package com.example.iot_project.notification;

import static java.security.AccessController.getContext;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.iot_project.R;
import com.example.iot_project.adapter.RecycleViewAdapter;
import com.example.iot_project.database.SQLiteHelper;
import com.example.iot_project.model.Item;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class notification extends BroadcastReceiver {

    private static final String CHANNEL_ID = "201";
    private RecycleViewAdapter adapter;
    private SQLiteHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {
        db=new SQLiteHelper(context);
        if ("hengio".equals(intent.getAction())) {
            int area = intent.getIntExtra("area", 0);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription("Mieu ta kenh 1");
                    notificationManager.createNotificationChannel(channel);
                }

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String shour = String.valueOf(hour);
                String sminute = String.valueOf(minute);
                if(hour<10)
                {
                    shour="0"+shour;
                }
                if(minute<10)
                {
                    sminute="0"+sminute;
                }
                String timePicker = shour + ":" + sminute;

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(timePicker)
                        .setContentText("Khu vực " + area + " bắt đầu tưới")
                        .setColor(Color.RED)
                        .setSmallIcon(R.drawable.icon_notifications)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

                notificationManager.notify(getNotificationId(), builder.build());

                String detail = "Máy bơm tưới cây khu vực " + area + " được bật";
                addItemAndReload(timePicker, detail);

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
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String shour = String.valueOf(hour);
                String sminute = String.valueOf(minute);
                if(hour<10)
                {
                    shour="0"+shour;
                }
                if(minute<10)
                {
                    sminute="0"+sminute;
                }
                String timePicker = shour + ":" + sminute;

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(timePicker)
                        .setContentText("Khu vực " + area + " kết thúc tưới")
                        .setColor(Color.RED)
                        .setSmallIcon(R.drawable.icon_notifications)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

                notificationManager.notify(getNotificationId(), builder.build());

                String detail = "Máy bơm tưới cây khu vực " + area + " kết thúc tưới";
                addItemAndReload(timePicker, detail);
            }
        }
    }
    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        long id = db.addItem(item);
        if (id != -1) {
            loadData();
        } else {
            Log.e("WaterFragment", "Failed to insert item");
        }
    }

    private void loadData() {
        adapter = new RecycleViewAdapter();
        List<Item> list = db.getAll();
        Log.e("WaterFragment", "FHi");
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    private int getNotificationId() {
        return (int) (new Date().getTime() % Integer.MAX_VALUE);
    }
}