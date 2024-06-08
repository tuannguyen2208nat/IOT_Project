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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.iot_project.R;
import com.example.iot_project.adapter.RecycleViewAdapter;
import com.example.iot_project.database.SQLiteHelper;
import com.example.iot_project.model.Item;
import com.example.iot_project.notification.notification;

import java.util.Calendar;
import java.util.List;

public class WaterFragment extends Fragment {

    LinearLayout layout_btn_on, layout_tu_dong, layout_btn_off;
    Button btn_thu_cong, btn_tu_dong, btnTuoi, btnTat;
    EditText water, timePicker_on, timePicker_off;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int chedo = 0;
    private int waterInt = 0;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water, container, false);
        db = new SQLiteHelper(getContext());

        CheckBox area1 = view.findViewById(R.id.area1);
        CheckBox area2 = view.findViewById(R.id.area2);
        CheckBox area3 = view.findViewById(R.id.area3);
        layout_btn_on = view.findViewById(R.id.layout_btn_on);
        layout_tu_dong = view.findViewById(R.id.layout_tu_dong);
        layout_btn_off = view.findViewById(R.id.layout_btn_off);
        btn_thu_cong = view.findViewById(R.id.btn_thu_cong);
        btn_tu_dong = view.findViewById(R.id.btn_tu_dong);
        btnTuoi = view.findViewById(R.id.btn_tuoi);
        btnTat = view.findViewById(R.id.btn_tat);
        water = view.findViewById(R.id.water);
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
                if (!area1.isChecked() && !area2.isChecked() && !area3.isChecked()) {
                    showAlert("Bạn chưa chọn khu vực");
                } else {
                    if ((chedo != 1) && (chedo != 2)) {
                        showAlert("Bạn chưa chọn chế độ");
                    } else {
                        String waterText = water.getText().toString();
                        if (waterText.isEmpty()) {
                            showAlert("Bạn chưa nhập đủ thông tin");
                        } else {
                            waterInt = Integer.parseInt(waterText);
                            if (waterInt <= 0) {
                                showAlert("Lượng nước không hợp lệ. Vui lòng nhập lại.");
                            } else {
                                int khuvuc = 0;
                                if (area1.isChecked()) {
                                    khuvuc = 1;
                                } else if (area2.isChecked()) {
                                    khuvuc = 2;
                                } else if (area3.isChecked()) {
                                    khuvuc = 3;
                                }
                                Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
                                if (chedo == 1) {
                                    Toast.makeText(getActivity(), "Máy bơm tưới cây khu vực " + khuvuc + " bật thành công !", Toast.LENGTH_SHORT).show();
                                    Calendar calendar = Calendar.getInstance();
                                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                    int minute = calendar.get(Calendar.MINUTE);
                                    int year = calendar.get(Calendar.YEAR);
                                    int month = calendar.get(Calendar.MONTH)+1;
                                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                                    String shour = String.valueOf(hour);
                                    String sminute = String.valueOf(minute);
                                    String sday = String.valueOf(day);
                                    String smonth = String.valueOf(month);
                                    String syear = String.valueOf(year);
                                    if(hour<10)
                                    {
                                        shour="0"+shour;
                                    }
                                    if(minute<10)
                                    {
                                        sminute="0"+sminute;
                                    }
                                    String timePicker =sday+"/"+"/"+smonth+"/"+"/"+syear+"-"+ shour + ":" + sminute;
                                    String detail = "Máy bơm tưới cây khu vực " + khuvuc + " được bật";
                                    addItemAndReload(timePicker, detail);
                                    water.setText("");
                                } else {
                                    int hour_on , minute_on , hour_off , minute_off; ;
                                    timePicker_on = view.findViewById(R.id.timePicker_on);
                                    timePicker_off = view.findViewById(R.id.timePicker_off);
                                    String timeText_on = timePicker_on.getText().toString();
                                    String timeText_off = timePicker_off.getText().toString();
                                    if (timeText_on.isEmpty() || timeText_off.isEmpty()) {
                                        showAlert("Bạn chưa nhập đủ thông tin");
                                    } else {
                                            if (timeText_on.contains(":")||timeText_off.contains(":")) {
                                                String[] timeParts_on = timeText_on.split(":");
                                                hour_on = Integer.parseInt(timeParts_on[0].trim());
                                                minute_on = Integer.parseInt(timeParts_on[1].trim());
                                                if ((hour_on < 0 || hour_on > 23) || (minute_on < 0 || minute_on > 59)) {
                                                    showAlert("Thời gian bắt dầu tưới không hợp lệ. Vui lòng nhập lại.");
                                                    return;
                                                }

                                                String[] timeParts_off = timeText_off.split(":");
                                                hour_off = Integer.parseInt(timeParts_off[0].trim());
                                                minute_off = Integer.parseInt(timeParts_off[1].trim());
                                                if ((hour_off < 0 || hour_off > 23) || (minute_off < 0 || minute_off > 59)) {
                                                    showAlert("Thời gian kết thúc tưới không hợp lệ. Vui lòng nhập lại.");
                                                    return;
                                                }

                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTimeInMillis(System.currentTimeMillis());
                                                calendar.set(Calendar.HOUR_OF_DAY, hour_on);
                                                calendar.set(Calendar.MINUTE, minute_on);
                                                calendar.set(Calendar.SECOND, 0);

                                                Intent intent = new Intent(getContext(), notification.class);
                                                intent.setAction("hengio_tuoi");
                                                intent.putExtra("area", khuvuc);
                                                alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                                                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                                                calendar = Calendar.getInstance();
                                                calendar.setTimeInMillis(System.currentTimeMillis());
                                                calendar.set(Calendar.HOUR_OF_DAY, hour_off);
                                                calendar.set(Calendar.MINUTE, minute_off);
                                                calendar.set(Calendar.SECOND, 0);

                                                intent.setAction("ketthuc_tuoi");
                                                intent.putExtra("area", khuvuc);
                                                alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                                                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                                Toast.makeText(getActivity(), "Đặt lịch tưới khu vực " + khuvuc + " thành công !", Toast.LENGTH_SHORT).show();
                                                resetFields();
                                            } else {
                                                showAlert("Định dạng thời gian không hợp lệ. Vui lòng nhập lại.");
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        btnTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!area1.isChecked() && !area2.isChecked() && !area3.isChecked()) {
                    showAlert("Bạn chưa chọn khu vực");}
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
                    Toast.makeText(getActivity(), "Máy bơm tưới cây khu vực " + khuvuc + " tắt thành công !", Toast.LENGTH_SHORT).show();
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH)+1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    String shour = String.valueOf(hour);
                    String sminute = String.valueOf(minute);
                    String sday = String.valueOf(day);
                    String smonth = String.valueOf(month);
                    String syear = String.valueOf(year);
                    if(hour<10)
                    {
                        shour="0"+shour;
                    }
                    if(minute<10)
                    {
                        sminute="0"+sminute;
                    }
                    String timePicker =sday+"/"+"/"+smonth+"/"+"/"+syear+"-"+ shour + ":" + sminute;
                    String detail = "Máy bơm tưới cây khu vực " + khuvuc + " được tắt";
                    addItemAndReload(timePicker, detail);
                    resetFields();
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
    private void resetFields() {
        water.setText("");
        timePicker_on.setText("");
        timePicker_off.setText("");
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
