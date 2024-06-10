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
import com.example.iot_project.database.MQTTHelper;
import com.example.iot_project.database.SQLiteHelper;
import com.example.iot_project.model.Item;
import com.example.iot_project.notification.notification;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

public class WaterFragment extends Fragment {
    MQTTHelper mqttHelper;
    String link="tuannguyen2208natIOT/feeds/routine";
    LinearLayout layout_btn_on, layout_tu_dong, layout_btn_off;
    Button btn_thu_cong, btn_tu_dong, btnTuoi, btnTat;
    EditText water, timePicker_on, timePicker_off;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int waterInt = 0;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;
    int area=0,mode=0;

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
        timePicker_on = view.findViewById(R.id.timePicker_on);
        timePicker_off = view.findViewById(R.id.timePicker_off);
        layout_btn_on.setVisibility(View.GONE);
        layout_btn_off.setVisibility(View.GONE);
        layout_tu_dong.setVisibility(View.GONE);
        startMQTT();



        btn_tu_dong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_btn_on.setVisibility(View.VISIBLE);
                layout_btn_off.setVisibility(View.GONE);
                layout_tu_dong.setVisibility(View.VISIBLE);
                mode = 1;
            }
        });

        btn_thu_cong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_btn_on.setVisibility(View.VISIBLE);
                layout_btn_off.setVisibility(View.VISIBLE);
                layout_tu_dong.setVisibility(View.GONE);
                mode = 2;
            }
        });

        btnTuoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!area1.isChecked() && !area2.isChecked() && !area3.isChecked()) {
                        showAlert("Bạn chưa chọn khu vực");
                        return;
                }
                if ((mode != 1) && (mode != 2)) {
                        showAlert("Bạn chưa chọn chế độ");
                        return;
                    }
                        String waterText = water.getText().toString();
                    if (waterText.isEmpty()) {
                        showAlert("Bạn chưa nhập đủ thông tin");
                        return;
                    }
                        waterInt = Integer.parseInt(waterText);
                    if (waterInt <= 0) {
                        showAlert("Lượng nước không hợp lệ. Vui lòng nhập lại.");
                        return;
                    }
                    if (area1.isChecked()) {
                        area = 1;
                    } else if (area2.isChecked()) {
                        area = 2;
                    } else if (area3.isChecked()) {
                        area = 3;
                    }
                    Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
                    try {
                        String timeText_on = timePicker_on.getText().toString();
                        String timeText_off = timePicker_off.getText().toString();
                        int day,month,year, timeIntCD ;
                        int hour_on , minute_on , hour_off , minute_off; ;
                        String shour , sminute ,sday,smonth,syear,starttime,endtime,timePicker,detail;
                        Intent intent = new Intent(getContext(), notification.class);
                        Calendar calendar = Calendar.getInstance();
                        Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
                        switch (mode) {
                            case 1:
                                if (!timeText_on.contains(":")||!timeText_off.contains(":")) {
                                    showAlert("Vui lòng nhập đúng định dạng thời gian");
                                    break;
                                }
                                String[] timeParts = timeText_on.split(":");
                                hour_on = Integer.parseInt(timeParts[0].trim());
                                minute_on = Integer.parseInt(timeParts[1].trim());
                                String[] timeParts_off = timeText_off.split(":");
                                hour_off = Integer.parseInt(timeParts_off[0].trim());
                                minute_off = Integer.parseInt(timeParts_off[1].trim());

                                if ((hour_on < 0 || hour_on > 23) || (minute_on < 0 || minute_on > 59)) {
                                    showAlert("Thời gian không hợp lệ. Vui lòng nhập lại.");
                                    return;
                                }
                                if ((hour_off < 0 || hour_off > 23) || (minute_off < 0 || minute_off > 59)) {
                                    showAlert("Thời gian kết thúc tưới không hợp lệ. Vui lòng nhập lại.");
                                    return;
                                }
                                day=calendar.get(Calendar.DAY_OF_MONTH);
                                month= calendar.get(Calendar.MONTH) + 1;
                                year= calendar.get(Calendar.YEAR);
                                shour = String.valueOf(hour_on);
                                sminute = String.valueOf(minute_on);
                                sday=String.valueOf(day);
                                smonth=String.valueOf(month);
                                syear=String.valueOf(year);
                                if(hour_on<10)
                                {
                                    shour="0"+shour;
                                }
                                if(minute_on<10)
                                {
                                    sminute="0"+sminute;
                                }
                                timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
                                starttime=shour+":"+sminute;
                                sendDataMQTT(link,starttime+"/"+"8"+"/on");
                                shour = String.valueOf(hour_off);
                                sminute = String.valueOf(minute_off);
                                if(hour_off<10)
                                {
                                    shour="0"+shour;
                                }
                                if(minute_off<10)
                                {
                                    sminute="0"+sminute;
                                }
                                endtime=shour+":"+sminute;
                                sendDataMQTT(link,starttime+"/"+"8"+"/off");
                                detail = "Đặt hẹn giờ tưới cho khu vực "+area+ " bắt đầu tuới từ "+starttime+" đến " +endtime+" thành công.";
                                addItemAndReload(timePicker, detail);
                                Toast.makeText(getActivity(), "Đặt hẹn giờ  tưới cho khu vực "+area+ " thành công!", Toast.LENGTH_SHORT).show();

                                resetFields();
                                break;

                            case 2:
                                calendar = Calendar.getInstance();
                                hour_on = calendar.get(Calendar.HOUR_OF_DAY);
                                minute_on = calendar.get(Calendar.MINUTE);
                                day=calendar.get(Calendar.DAY_OF_MONTH);
                                month= calendar.get(Calendar.MONTH) + 1;
                                year= calendar.get(Calendar.YEAR);
                                shour = String.valueOf(hour_on);
                                sminute = String.valueOf(minute_on);
                                sday=String.valueOf(day);
                                smonth=String.valueOf(month);
                                syear=String.valueOf(year);
                                if(hour_on<10)
                                {
                                    shour="0"+shour;
                                }
                                if(minute_on<10)
                                {
                                    sminute="0"+sminute;
                                }
                                timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
                                starttime=shour+":"+sminute;
                                sendDataMQTT(link,starttime+"/"+"8"+"/on");
                                detail = "Máy bơm tưới cây khu vực "+area + " bắt đầu tuới từ "+starttime;
                                addItemAndReload(timePicker, detail);
                                Toast.makeText(getActivity(), "Máy bơm tưới cây khu vực "+ area + " bắt đầu tuới", Toast.LENGTH_SHORT).show();
                                resetFields();
                                break;

                            default:
                                break;
                        }
                    } catch (NumberFormatException e) {
                        showAlert("Thông tin nước, dung dịch và thời gian phải là số");
                    }
            }

        });

        btnTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
                if (!area1.isChecked() && !area2.isChecked() && !area3.isChecked()) {
                    showAlert("Bạn chưa chọn khu vực");}
                else
                {
                    if (area1.isChecked()) {
                        area = 1;
                    } else if (area2.isChecked()) {
                        area = 2;
                    } else if (area3.isChecked()) {
                        area = 3;
                    }
                    Toast.makeText(getActivity(), "Máy bơm tưới cây khu vực "+ area + " kết thúc tuới", Toast.LENGTH_SHORT).show();
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
                    String endtime=shour+":"+sminute;
                    sendDataMQTT(link,endtime+"/"+"8"+"/off");
                    String  timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
                    String detail = "Máy bơm tưới cây khu vực " + area + " kết thúc.";
                    addItemAndReload(timePicker, detail);
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

    public void startMQTT() {
        mqttHelper = new MQTTHelper(getContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }
            @Override
            public void connectionLost(Throwable cause) {

            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);
        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }
}
