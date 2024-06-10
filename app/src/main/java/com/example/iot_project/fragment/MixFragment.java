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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

public class MixFragment extends Fragment {
    MQTTHelper mqttHelper;
    String link="tuannguyen2208natIOT/feeds/routine";
    private LinearLayout layout_mix1, layout_mix2, layout_mix3;
    Button btn_mix1, btn_mix2, btn_mix3, btn_mixer;
    ImageButton imgbtn_mix1, imgbtn_mix2, imgbtn_mix3;
    private LinearLayout currentSelectedLayout = null, layout_scheduler_timePicker;
    private EditText scheduler_name, scheduler_water, scheduler_liquid, scheduler_timePicker, scheduler_time;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    private int botron = 0, mode = 0;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mix, container, false);
        db = new SQLiteHelper(getContext());

        Spinner select_mode = view.findViewById(R.id.select_mode);
        String[] options = {"Bây giờ", "Hẹn giờ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_mode.setAdapter(adapter);

        layout_mix1 = view.findViewById(R.id.layout_mix_1);
        layout_mix2 = view.findViewById(R.id.layout_mix_2);
        layout_mix3 = view.findViewById(R.id.layout_mix_3);
        layout_scheduler_timePicker = view.findViewById(R.id.layout_scheduler_timePicker);

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
        scheduler_timePicker = view.findViewById(R.id.scheduler_timePicker);
        scheduler_time = view.findViewById(R.id.scheduler_time);

        // Setup button and image button click listeners
        setupButtonClickListener(btn_mix1, layout_mix1);
        setupButtonClickListener(btn_mix2, layout_mix2);
        setupButtonClickListener(btn_mix3, layout_mix3);

        setupImageButtonClickListener(imgbtn_mix1, layout_mix1);
        setupImageButtonClickListener(imgbtn_mix2, layout_mix2);
        setupImageButtonClickListener(imgbtn_mix3, layout_mix3);

        btn_mixer.setOnClickListener(v -> handleMixButtonClick());

        select_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                if ("Hẹn giờ".equals(selectedOption)) {
                    layout_scheduler_timePicker.setVisibility(View.VISIBLE);
                    mode = 1;
                } else if ("Bây giờ".equals(selectedOption)) {
                    layout_scheduler_timePicker.setVisibility(View.GONE);
                    mode = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        startMQTT();

        return view;
    }

    private void setupButtonClickListener(Button button, LinearLayout layout) {
        button.setOnClickListener(v -> changeLayoutColor(layout));
    }

    private void setupImageButtonClickListener(ImageButton imageButton, LinearLayout layout) {
        imageButton.setOnClickListener(v -> changeLayoutColor(layout));
    }

    private void handleMixButtonClick() {
        if (botron != 1 && botron != 2 && botron != 3) {
            showAlert("Bạn chưa chọn bộ trộn");
            return;
        }

        String nameText = scheduler_name.getText().toString();
        String waterText = scheduler_water.getText().toString();
        String liquidText = scheduler_liquid.getText().toString();
        String timePickerText = scheduler_timePicker.getText().toString();
        String timeText = scheduler_time.getText().toString();

        if (nameText.isEmpty() || waterText.isEmpty() || liquidText.isEmpty()) {
            showAlert("Bạn chưa nhập đủ thông tin");
            return;
        }

        if (timeText.isEmpty()) {
            showAlert("Bạn chưa nhập thời gian trộn");
            return;
        }

        if (mode == 1 && timePickerText.isEmpty()) {
            showAlert("Bạn chưa nhập thời gian hẹn giờ");
            return;
        }

        try {
            int water = Integer.parseInt(waterText);
            int liquid = Integer.parseInt(liquidText);
            int time = Integer.parseInt(timeText);
            int hour , minute ,day,month,year, timeIntCD ;
            String shour , sminute ,sday,smonth,syear,starttime,endtime,timePicker,detail;
            Intent intent = new Intent(getContext(), notification.class);
            Calendar calendar = Calendar.getInstance();
            Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
            switch (mode) {
                case 1:
                    if (!timePickerText.contains(":")) {
                        showAlert("Vui lòng nhập đúng định dạng thời gian");
                        break;
                    }
                    String[] timeParts = timePickerText.split(":");
                    hour = Integer.parseInt(timeParts[0].trim());
                    minute = Integer.parseInt(timeParts[1].trim());
                    if ((hour < 0 || hour > 23) || (minute < 0 || minute > 59)) {
                        showAlert("Thời gian không hợp lệ. Vui lòng nhập lại.");
                        return;
                    }
                    day=calendar.get(Calendar.DAY_OF_MONTH);
                    month= calendar.get(Calendar.MONTH) + 1;
                    year= calendar.get(Calendar.YEAR);
                    shour = String.valueOf(hour);
                    sminute = String.valueOf(minute);
                    sday=String.valueOf(day);
                    smonth=String.valueOf(month);
                    syear=String.valueOf(year);
                    if(hour<10)
                    {
                        shour="0"+shour;
                    }
                    if(minute<10)
                    {
                        sminute="0"+sminute;
                    }
                    timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
                    starttime=shour+":"+sminute;
                    sendDataMQTT(link,starttime+"/"+botron+"/on");

                    timeIntCD = minute + time;
                    if (timeIntCD >= 60) {
                        hour = hour + timeIntCD / 60;
                        minute = timeIntCD % 60;
                    } else {
                        minute = timeIntCD;
                    }
                    shour = String.valueOf(hour);
                    sminute = String.valueOf(minute);
                    if(hour<10)
                    {
                        shour="0"+shour;
                    }
                    if(minute<10)
                    {
                        sminute="0"+sminute;
                    }
                    endtime=shour+":"+sminute;
                    sendDataMQTT(link,endtime+"/"+botron+"/off");
                    detail = "Đặt hẹn giờ cho bộ trộn "+botron + " tên bộ trộn :  " + nameText + " bắt đầu trộn từ "+starttime+" đến " +endtime+" thành công.";
                    addItemAndReload(timePicker, detail);
                    Toast.makeText(getActivity(), "Đặt hẹn giờ bộ trộn "+botron+ " thành công!", Toast.LENGTH_SHORT).show();
                    resetFields();
                    break;

                case 2:
                    calendar = Calendar.getInstance();
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                    day=calendar.get(Calendar.DAY_OF_MONTH);
                    month= calendar.get(Calendar.MONTH) + 1;
                    year= calendar.get(Calendar.YEAR);
                    shour = String.valueOf(hour);
                    sminute = String.valueOf(minute);
                    sday=String.valueOf(day);
                    smonth=String.valueOf(month);
                    syear=String.valueOf(year);
                    if(hour<10)
                    {
                        shour="0"+shour;
                    }
                    if(minute<10)
                    {
                        sminute="0"+sminute;
                    }
                    timePicker = sday + "/"+smonth+"/"+syear+"-"+shour+":"+sminute;
                    starttime=shour+":"+sminute;
                    sendDataMQTT(link,starttime+"/"+botron+"/on");

                    timeIntCD = minute + time;
                    if (timeIntCD >= 60) {
                        hour = hour + timeIntCD / 60;
                        minute = timeIntCD % 60;
                    } else {
                        minute = timeIntCD;
                    }
                    shour = String.valueOf(hour);
                    sminute = String.valueOf(minute);
                    if(hour<10)
                    {
                        shour="0"+shour;
                    }
                    if(minute<10)
                    {
                        sminute="0"+sminute;
                    }
                    endtime=shour+":"+sminute;
                    sendDataMQTT(link,endtime+"/"+botron+"/off");
                    detail = "Đặt bộ trộn "+botron + " tên bộ trộn : ( " + nameText + " ) bắt đầu trộn từ "+starttime+" đến " +endtime+" thành công.";
                    addItemAndReload(timePicker, detail);
                    Toast.makeText(getActivity(), "Đặt bộ trộn "+botron+ " thành công!", Toast.LENGTH_SHORT).show();
                    resetFields();
                    break;

                default:
                    break;
            }
        } catch (NumberFormatException e) {
            showAlert("Thông tin nước, dung dịch và thời gian phải là số");
        }
    }

    private void changeLayoutColor(LinearLayout newLayout) {
        if (currentSelectedLayout != null) {
            currentSelectedLayout.setBackgroundResource(R.drawable.border_black_2dp);
        }
        newLayout.setBackgroundResource(R.drawable.border_black_2dp_background);
        currentSelectedLayout = newLayout;
        if (currentSelectedLayout == layout_mix1) {
            botron = 1;
        } else if (currentSelectedLayout == layout_mix2) {
            botron = 2;
        } else if (currentSelectedLayout == layout_mix3) {
            botron = 3;
        }
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void resetFields() {
        scheduler_name.setText("");
        scheduler_time.setText("");
        scheduler_liquid.setText("");
        scheduler_water.setText("");
        scheduler_timePicker.setText("");
    }
    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        long id = db.addItem(item);
        if (id != -1) {
            loadData();
        } else {
            Log.e("MixFragment", "Failed to insert item");
        }
    }

    private void loadData() {
        adapter = new RecycleViewAdapter();
        List<Item> list = db.getAll();
        Log.e("MixFragment", "FHi");
        adapter.setList(list);
        adapter.notifyDataSetChanged();
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