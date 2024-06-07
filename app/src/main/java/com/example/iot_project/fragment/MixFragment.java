package com.example.iot_project.fragment;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.iot_project.R;
import com.example.iot_project.adapter.RecycleViewAdapter;
import com.example.iot_project.database.SQLiteHelper;
import com.example.iot_project.model.Item;
import com.example.iot_project.notification.notification;

import java.util.Calendar;
import java.util.List;

public class MixFragment extends Fragment {

    private LinearLayout layout_mix1, layout_mix2, layout_mix3;
    private Button btn_mix1, btn_mix2, btn_mix3, btn_mixer;
    private ImageButton imgbtn_mix1, imgbtn_mix2, imgbtn_mix3;
    private LinearLayout currentSelectedLayout = null;
    private EditText scheduler_name, scheduler_water, scheduler_liquid,scheculer_timePicker,scheduler_time;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int id=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mix, container, false);

        layout_mix1 = view.findViewById(R.id.layout_mix_1);
        layout_mix2 = view.findViewById(R.id.layout_mix_2);
        layout_mix3 = view.findViewById(R.id.layout_mix_3);

        btn_mix1 = view.findViewById(R.id.btn_mix_1);
        btn_mix2 = view.findViewById(R.id.btn_mix_2);
        btn_mix3 = view.findViewById(R.id.btn_mix_3);
        btn_mixer = view.findViewById(R.id.btn_mixer);
        imgbtn_mix1 = view.findViewById(R.id.imgbtn_1);
        imgbtn_mix2 = view.findViewById(R.id.imgbtn_2);
        imgbtn_mix3 = view.findViewById(R.id.imgbtn_3);

        scheduler_name = view.findViewById(R.id.scheduler_name);
        scheduler_water = view.findViewById(R.id.scheduler_water);
        scheduler_liquid = view.findViewById(R.id.scheduler_liquid);
        scheculer_timePicker=view.findViewById(R.id.scheduler_timePicker);
        scheduler_time=view.findViewById(R.id.scheduler_time);

        setupButtonClickListener(btn_mix1, layout_mix1, "Bộ trộn 1");
        setupButtonClickListener(btn_mix2, layout_mix2, "Bộ trộn 2");
        setupButtonClickListener(btn_mix3, layout_mix3, "Bộ trộn 3");

        setupImageButtonClickListener(imgbtn_mix1, layout_mix1, "Bộ trộn 1");
        setupImageButtonClickListener(imgbtn_mix2, layout_mix2, "Bộ trộn 2");
        setupImageButtonClickListener(imgbtn_mix3, layout_mix3, "Bộ trộn 3");

        btn_mixer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleMixButtonClick();
            }
        });

        return view;
    }

    private void setupButtonClickListener(Button button, LinearLayout layout, String message) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayoutColor(layout);
            }
        });
    }

    private void setupImageButtonClickListener(ImageButton imageButton, LinearLayout layout, String message) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayoutColor(layout);
            }
        });
    }

    private void handleMixButtonClick() {
        if(id!=1&&id!=2&&id!=3)
        {
            showAlert("Bạn chưa chọn bộ trộn");
            return;
        }
        int mode=0;
        String nameText = scheduler_name.getText().toString();
        String waterText = scheduler_water.getText().toString();
        String liquidText = scheduler_liquid.getText().toString();
        String timePickerText=scheculer_timePicker.getText().toString();
        String timeText = scheduler_time.getText().toString();

        if (nameText.isEmpty() || waterText.isEmpty() || liquidText.isEmpty()) {
            showAlert("Bạn chưa nhập đủ thông tin");
            return;
        }
        if (timeText.isEmpty()) {
            showAlert("Bạn chưa nhập thời gian trộn");
            return;
        }
        if (timePickerText.contains(":")) {
            mode=1;
        }
        else if(timePickerText.isEmpty()){
            mode=2;
        }


        try {
            int water = Integer.parseInt(waterText);
            int liquid = Integer.parseInt(liquidText);
            int time = Integer.parseInt(timeText);
            int hour=0,minute=0,timeIntCD=0;
            Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), notification.class);

            switch(mode){
                case 1:
                    String[] timeParts = timePickerText.split(":");
                    hour = Integer.parseInt(timeParts[0].trim());
                    minute = Integer.parseInt(timeParts[1].trim());
                    if ((hour < 0 || hour > 23) || (minute < 0 || minute > 59)) {
                        showAlert("Thời gian không hợp lệ. Vui lòng nhập lại.");
                        return;
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    intent.setAction("hengio_tron");
                    intent.putExtra("id", id);
                    intent.putExtra("name", nameText);
                    alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                    pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    timeIntCD = minute + time;
                    if (timeIntCD >= 60) {
                        hour = hour + timeIntCD / 60;
                        minute = timeIntCD % 60;
                    } else {
                        minute = timeIntCD;
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    intent.setAction("ketthuc_tron");
                    intent.putExtra("id", id);
                    intent.putExtra("name", nameText);
                    alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                    pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    scheduler_name.setText("");
                    scheduler_time.setText("");
                    scheduler_liquid.setText("");
                    scheduler_water.setText("");
                    scheculer_timePicker.setText("");
                    Toast.makeText(getActivity(), "Đặt bộ trộn " + id + " thành công !", Toast.LENGTH_SHORT).show();
                    break;
                case 2 :
                    calendar = Calendar.getInstance();
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                    timeIntCD = minute + time;
                    if (timeIntCD >= 60) {
                        hour = hour + timeIntCD / 60;
                        minute = timeIntCD % 60;
                    } else {
                        minute = timeIntCD;
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    intent.setAction("ketthuc_tron");
                    intent.putExtra("id", id);
                    intent.putExtra("name", nameText);
                    alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                    pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    scheduler_name.setText("");
                    scheduler_time.setText("");
                    scheduler_liquid.setText("");
                    scheduler_water.setText("");
                    scheculer_timePicker.setText("");
                    Toast.makeText(getActivity(), "Đặt bộ trộn " + id + " thành công !", Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }


        } catch (NumberFormatException e) {
            showAlert("Thông tin nước , dung dịch và thời gian phải là số");
        }
    }

    private void changeLayoutColor(LinearLayout newLayout) {
        if (currentSelectedLayout != null) {
            currentSelectedLayout.setBackgroundResource(R.drawable.border_black_2dp);
        }
        newLayout.setBackgroundResource(R.drawable.border_black_2dp_background);
        currentSelectedLayout = newLayout;
        if(currentSelectedLayout==layout_mix1)
        {
            id=1;
        }
        else if(currentSelectedLayout==layout_mix2)
        {
            id=2;
        }
        else if(currentSelectedLayout==layout_mix3)
        {
            id=3;
        }
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}
