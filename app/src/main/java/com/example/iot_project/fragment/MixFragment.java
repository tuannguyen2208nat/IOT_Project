package com.example.iot_project.fragment;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
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
import android.widget.Toast;

import com.example.iot_project.R;
import com.example.iot_project.adapter.RecycleViewAdapter;
import com.example.iot_project.database.MQTTHelper;
import com.example.iot_project.database.SQLiteHelper;
import com.example.iot_project.model.Item;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;

public class MixFragment extends Fragment {
    MQTTHelper mqttHelper;
    String link="tuannguyen2208natIOT/feeds/routine";
    private LinearLayout layout_mix1, layout_mix2, layout_mix3;
    Button btn_mix1, btn_mix2, btn_mix3, btn_on,btn_off;
    ImageButton imgbtn_mix1, imgbtn_mix2, imgbtn_mix3;
    private LinearLayout currentSelectedLayout = null, layout_scheduler_timePicker,layout_thucong;
    private EditText  scheduler_water, scheduler_liquid, scheduler_timePicker;
    private int botron = 0, mode = 1;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mix, container, false);
        db = new SQLiteHelper(getContext());
        mqttHelper = new MQTTHelper(getContext());

        Spinner select_mode = view.findViewById(R.id.select_mode);
        String[] options = { "Thủ công","Tự động"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_mode.setAdapter(adapter);

        layout_mix1 = view.findViewById(R.id.layout_mix_1);
        layout_mix2 = view.findViewById(R.id.layout_mix_2);
        layout_mix3 = view.findViewById(R.id.layout_mix_3);
        layout_scheduler_timePicker = view.findViewById(R.id.layout_scheduler_timePicker);
        layout_thucong=view.findViewById(R.id.layout_thucong);

        btn_mix1 = view.findViewById(R.id.btn_mix_1);
        btn_mix2 = view.findViewById(R.id.btn_mix_2);
        btn_mix3 = view.findViewById(R.id.btn_mix_3);
        btn_on=view.findViewById(R.id.btn_on);
        btn_off=view.findViewById(R.id.btn_off);

        imgbtn_mix1 = view.findViewById(R.id.imgbtn_1);
        imgbtn_mix2 = view.findViewById(R.id.imgbtn_2);
        imgbtn_mix3 = view.findViewById(R.id.imgbtn_3);

        scheduler_water = view.findViewById(R.id.scheduler_water);
        scheduler_liquid = view.findViewById(R.id.scheduler_liquid);
        scheduler_timePicker = view.findViewById(R.id.scheduler_timePicker);

        setupButtonClickListener(btn_mix1, layout_mix1);
        setupButtonClickListener(btn_mix2, layout_mix2);
        setupButtonClickListener(btn_mix3, layout_mix3);

        setupImageButtonClickListener(imgbtn_mix1, layout_mix1);
        setupImageButtonClickListener(imgbtn_mix2, layout_mix2);
        setupImageButtonClickListener(imgbtn_mix3, layout_mix3);

        btn_on.setOnClickListener(v -> handleMixButtonClick());
        btn_off.setOnClickListener(v -> handleOffButtonClick());

        select_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                if ("Thủ công".equals(selectedOption)) {
                    layout_scheduler_timePicker.setVisibility(View.GONE);
                    layout_thucong.setVisibility(View.VISIBLE);
                    mode = 1;
                } else if ("Tự động".equals(selectedOption)) {
                    layout_scheduler_timePicker.setVisibility(View.VISIBLE);
                    layout_thucong.setVisibility(View.GONE);
                    mode = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

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

        String waterText = scheduler_water.getText().toString();
        String liquidText = scheduler_liquid.getText().toString();

        if ( waterText.isEmpty()) {
            showAlert("Vui lòng nhập lượng nước, dung dịch");
            return;
        }
        if(liquidText.isEmpty())
        {
            showAlert("Vui lòng nhập lượng nước, dung dịch");
            return;
        }


            int hour , minute ,day,month,year;
            String shour , sminute ,sday,smonth,syear,timePicker,detail;
            Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
            CountDownTimer Timer;
            switch (mode) {
                case 1:
                    sendDataMQTT(link,botron+"/on");
                    Toast.makeText(getActivity(), "Bộ trộn khu vực "+ botron + " bắt đầu trộn", Toast.LENGTH_SHORT).show();
                    resetFields();
                    break;
                case 2:
                    String timePickerText = scheduler_timePicker.getText().toString();
                    if (timePickerText.isEmpty()) {
                        showAlert("Vui lòng nhập thời gian tắt");
                        return;
                    }
                    int time=Integer.parseInt(timePickerText);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
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
                    detail = "Đặt bộ trộn "+ botron + " hẹn giờ trộn "+ time + " phút , thành công.";
                    addItemAndReload(timePicker, detail);
                    sendDataMQTT(link,botron+"/on");
                    Toast.makeText(getActivity(), "Đặt bộ trộn "+botron+ " thành công!", Toast.LENGTH_SHORT).show();
                    Timer=new CountDownTimer(time * 60 * 1000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }
                        public void onFinish() {
                            sendDataMQTT(link,botron+"/off");
                        }
                    }.start();
                    resetFields();
                    break;
                default:
                    break;
            }
    }

    private void handleOffButtonClick(){
        if (botron != 1 && botron != 2 && botron != 3) {
            showAlert("Bạn chưa chọn bộ trộn");
            return;
        }
        Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
        sendDataMQTT(link,botron+"/off");
        Toast.makeText(getActivity(), "Bộ trộn khu vực "+ botron + " kết thúc trộn.", Toast.LENGTH_SHORT).show();
        resetFields();
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

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(StandardCharsets.UTF_8);
        msg.setPayload(b);
        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
            Log.e("MixFragment","MixFragment can't send data");
        }
    }
}