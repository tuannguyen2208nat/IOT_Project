package com.example.iot_project.fragment;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.iot_project.R;
import com.example.iot_project.notification.notification;

import java.util.Calendar;

public class WaterFragment extends Fragment {

    LinearLayout layout_btn_on, layout_tu_dong,layout_btn_off;
    Button btn_thu_cong, btn_tu_dong, btnTuoi,btnTat;
    EditText water,timePicker,water_time;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int chedo = 0;
    private int waterInt=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water, container, false);

        CheckBox area1 = view.findViewById(R.id.area1);
        CheckBox area2 = view.findViewById(R.id.area2);
        CheckBox area3 = view.findViewById(R.id.area3);
        layout_btn_on = view.findViewById(R.id.layout_btn_on);
        layout_tu_dong = view.findViewById(R.id.layout_tu_dong);
        layout_btn_off=view.findViewById(R.id.layout_btn_off);
        btn_thu_cong = view.findViewById(R.id.btn_thu_cong);
        btn_tu_dong = view.findViewById(R.id.btn_tu_dong);
        btnTuoi = view.findViewById(R.id.btn_tuoi);
        btnTat=view.findViewById(R.id.btn_tat);
        water=view.findViewById(R.id.water);
        layout_btn_on.setVisibility(View.GONE);
        layout_btn_off.setVisibility(View.GONE);
        layout_tu_dong.setVisibility(View.GONE);

        btn_thu_cong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_btn_on.setVisibility(View.VISIBLE);
                layout_btn_off.setVisibility(View.VISIBLE);
                layout_tu_dong.setVisibility(View.GONE);
                chedo = 1;
            }
        });

        btn_tu_dong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_btn_on.setVisibility(View.VISIBLE);
                layout_btn_off.setVisibility(View.GONE);
                layout_tu_dong.setVisibility(View.VISIBLE);
                chedo = 2;
            }
        });

        btnTuoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!area1.isChecked() && !area2.isChecked() && !area3.isChecked()) {showAlert("Bạn chưa chọn khu vực");}
                else {
                    if ((chedo!=1)&&(chedo!=2)) {showAlert("Bạn chưa chọn chế độ");}
                    else
                    {
                        String waterText = water.getText().toString();
                        if (waterText.isEmpty()) {showAlert("Bạn chưa nhập đủ thông tin");}
                        else
                        {
                            waterInt = Integer.parseInt(waterText);
                            if (waterInt<=0) {showAlert("Lượng nước không hợp lệ. Vui lòng nhập lại.");}
                            else
                            {
                                int khuvuc = 0;
                                if (area1.isChecked()) {
                                    khuvuc = 1;
                                } else if (area2.isChecked()) {
                                    khuvuc = 2;
                                } else if (area3.isChecked()) {
                                    khuvuc = 3;
                                }
                                if(chedo==1)
                                {
                                    Toast.makeText(getActivity(), "Khu vực " + khuvuc + " tuới thành công !", Toast.LENGTH_SHORT).show();
                                    water.setText("");
                                    int finalKhuvuc = khuvuc;
                                    btnTat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(getActivity(), "Khu vực " + finalKhuvuc + " tắt thành công !", Toast.LENGTH_SHORT).show();
                                            water.setText("");
                                        }
                                    });
                                }
                                else {
                                    int hour=0,minute=0,water_timeInt=0;
                                    timePicker = view.findViewById(R.id.timePicker);
                                    water_time= view.findViewById(R.id.water_time);
                                    String timeText = timePicker.getText().toString();
                                    String water_timeText = water_time.getText().toString();
                                    water_timeInt = Integer.parseInt(water_timeText);
                                    if (timeText.isEmpty() || water_timeText.isEmpty()) {showAlert("Bạn chưa nhập đủ thông tin");}
                                    else {
                                        if (water_timeInt<1) {showAlert("Số phút tưới không hợp lệ. Vui lòng nhập lại.");}
                                        else
                                        {
                                            if (timeText.contains(":")) {
                                                String[] timeParts = timeText.split(":");
                                                hour = Integer.parseInt(timeParts[0].trim());
                                                minute = Integer.parseInt(timeParts[1].trim());
                                                if ((hour < 0 || hour > 23) || (minute < 0 || minute > 59))
                                                {
                                                    showAlert("Thời gian không hợp lệ. Vui lòng nhập lại.");
                                                    return;
                                                }

                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTimeInMillis(System.currentTimeMillis());
                                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                                calendar.set(Calendar.MINUTE, minute);
                                                calendar.set(Calendar.SECOND, 0);

                                                String shour = String.valueOf(hour);
                                                String sminute = String.valueOf(minute);
                                                if (minute < 10) {
                                                    sminute = "0" + minute;
                                                }
                                                Intent intent = new Intent(getContext(), notification.class);
                                                intent.setAction("hengio");
                                                intent.putExtra("area", khuvuc);
                                                intent.putExtra("timePicker", shour + ":" + sminute);
                                                alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                                                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                                Toast.makeText(getActivity(), "Đặt lịch tuới khu vực " + khuvuc + " thành công !", Toast.LENGTH_SHORT).show();

                                                int water_timeIntCD=minute+water_timeInt;
                                                if(water_timeIntCD>=60)
                                                {
                                                    hour=hour+water_timeIntCD/60;
                                                    minute=water_timeIntCD%60;
                                                }
                                                else
                                                {
                                                    minute=water_timeIntCD;
                                                }

                                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                                calendar.set(Calendar.MINUTE, minute);
                                                calendar.set(Calendar.SECOND, 0);

                                                intent.setAction("ketthuc");
                                                intent.putExtra("area", khuvuc);
                                                alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                                                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                                water.setText("");
                                                timePicker.setText("");
                                                water_time.setText("");

                                            }
                                            else {
                                                showAlert("Định dạng thời gian không hợp lệ. Vui lòng nhập lại.");
                                            }

                                        }
                                    }
                                }
                            }
                        }//

                    }
                }
            }
        });

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (buttonView.getId() == R.id.area1) {
                        area2.setChecked(false);
                        area3.setChecked(false);
                    } else if (buttonView.getId() == R.id.area2) {
                        area1.setChecked(false);
                        area3.setChecked(false);
                    } else if (buttonView.getId() == R.id.area3) {
                        area1.setChecked(false);
                        area2.setChecked(false);
                    }
                }
            }
        };

        area1.setOnCheckedChangeListener(listener);
        area2.setOnCheckedChangeListener(listener);
        area3.setOnCheckedChangeListener(listener);

        return view;
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
