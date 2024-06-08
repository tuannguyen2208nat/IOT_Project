package com.example.iot_project.notification;

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

    private static final String CHANNEL_ID_1 = "CHANNEL_1";
    private static final String CHANNEL_ID_2 = "CHANNEL_2";
    private static final String CHANNEL_ID_3 = "CHANNEL_3";
    private static final String CHANNEL_ID_21 = "CHANNEL_21";
    private static final String CHANNEL_ID_22 = "CHANNEL_22";
    private static final String CHANNEL_ID_23 = "CHANNEL_23";
    RecycleViewAdapter adapter;
    private SQLiteHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {
        db = new SQLiteHelper(context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        createNotificationChannels(notificationManager);

        if ("hengio_tuoi".equals(intent.getAction()) || "ketthuc_tuoi".equals(intent.getAction())) {
            int area = intent.getIntExtra("area", 1);
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int day=calendar.get(Calendar.DAY_OF_MONTH);
            int month= calendar.get(Calendar.MONTH) + 1;
            int year= calendar.get(Calendar.YEAR);
            String shour = String.valueOf(hour);
            String sminute = String.valueOf(minute);
            String sday=String.valueOf(day);
            String smonth=String.valueOf(month);
            String syear=String.valueOf(year);
            if(hour<10)
            {
                shour="0"+shour;
            }
            if(minute<10)
            {
                sminute="0"+sminute;
            }
            String timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
            String datePicker =  sday + "/"+smonth+"/"+syear;

            String detail = "Hẹn giờ: máy bơm tưới cây khu vực " + area + " " + ("hengio_tuoi".equals(intent.getAction()) ? "được bật" : "kết thúc tưới");
            addItemAndReload(timePicker, detail);

            String notificationTitle = "Hẹn giờ thành công " + datePicker;
            String notificationText = "Khu vực " + area + " " + ("hengio_tuoi".equals(intent.getAction()) ? "bắt đầu tưới" : "kết thúc tưới");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, getChannelId(area))
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setColor(Color.RED)
                    .setSmallIcon(R.drawable.icon_notifications)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND);

            notificationManager.notify(getNotificationId(), builder.build());
        }
        if ("hengio_tron".equals(intent.getAction()) || "ketthuc_tron".equals(intent.getAction())) {
            int id = intent.getIntExtra("id", 1);
            String name=intent.getStringExtra("name");
            int ID=id+20;
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int day=calendar.get(Calendar.DAY_OF_MONTH);
            int month= calendar.get(Calendar.MONTH) + 1;
            int year= calendar.get(Calendar.YEAR);
            String shour = String.valueOf(hour);
            String sminute = String.valueOf(minute);
            String sday=String.valueOf(day);
            String smonth=String.valueOf(month);
            String syear=String.valueOf(year);
            if(hour<10)
            {
                shour="0"+shour;
            }
            if(minute<10)
            {
                sminute="0"+sminute;
            }
            String timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
            String datePicker =  sday + "/"+smonth+"/"+syear;

            String detail = "Hẹn giờ: bộ trộn "+name+" (Bộ trộn " + id + ") " + ("hengio_tron".equals(intent.getAction()) ? " bắt đầu trộn" : "kết thúc trộn");
            addItemAndReload(timePicker, detail);

            String notificationTitle = "Hẹn giờ thành công " + datePicker;
            String notificationText = "Bộ trộn "+name+" (Bộ trộn " + id + ") "  + ("hengio_tron".equals(intent.getAction()) ? "bắt đầu trộn" : "kết thúc trộn");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, getChannelId(ID))
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setColor(Color.RED)
                    .setSmallIcon(R.drawable.icon_notifications)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND);

            notificationManager.notify(getNotificationId(), builder.build());
        }
    }
    private void createNotificationChannels(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Notification Channel 1 for Area 1");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Channel 2", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Notification Channel 2 for Area 2");

            NotificationChannel channel3 = new NotificationChannel(CHANNEL_ID_3, "Channel 3", NotificationManager.IMPORTANCE_HIGH);
            channel3.setDescription("Notification Channel 3 for Area 3");

            NotificationChannel channel21 = new NotificationChannel(CHANNEL_ID_21, "Channel 21", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Notification Channel 1 for Area 1");

            NotificationChannel channel22 = new NotificationChannel(CHANNEL_ID_22, "Channel 22", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Notification Channel 2 for Area 2");

            NotificationChannel channel23 = new NotificationChannel(CHANNEL_ID_23, "Channel 23", NotificationManager.IMPORTANCE_HIGH);
            channel3.setDescription("Notification Channel 3 for Area 3");

            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
            notificationManager.createNotificationChannel(channel3);
            notificationManager.createNotificationChannel(channel21);
            notificationManager.createNotificationChannel(channel22);
            notificationManager.createNotificationChannel(channel23);
        }
    }

    private String getChannelId(int area) {
        switch (area) {
            case 1: return CHANNEL_ID_1;
            case 2: return CHANNEL_ID_2;
            case 3: return CHANNEL_ID_3;
            default: return CHANNEL_ID_1; // Default to CHANNEL_1 if area is out of range
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
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    private int getNotificationId() {
        return (int) (new Date().getTime() % Integer.MAX_VALUE);
    }
}
