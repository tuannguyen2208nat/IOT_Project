package com.example.iot_project.fragment;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.iot_project.notification.notification;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

public class TimerFragment extends Fragment {
    MQTTHelper mqttHelper;
    String link="tuannguyen2208natIOT/feeds/routine";
    Button btn_mixer;

    private EditText mix1_timer,mix2_timer,mix3_timer;

    private EditText area1_timer,area2_timer,area3_timer;
    private EditText scheduler_name;
    private EditText pump_in,pump_out;
    private EditText timePicker_cycle,timePicker_on,timePicker_off;


    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    RecycleViewAdapter adapter;
    private SQLiteHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        db = new SQLiteHelper(getContext());

        scheduler_name=view.findViewById(R.id.scheduler_name);

        mix1_timer=view.findViewById(R.id.mix1_timer);
        mix2_timer=view.findViewById(R.id.mix2_timer);
        mix3_timer=view.findViewById(R.id.mix3_timer);

        area1_timer=view.findViewById(R.id.area1_timer);
        area2_timer=view.findViewById(R.id.area2_timer);
        area3_timer=view.findViewById(R.id.area3_timer);

        pump_in=view.findViewById(R.id.pump_in);
        pump_out=view.findViewById(R.id.pump_out);

        timePicker_cycle=view.findViewById(R.id.timePicker_cycle);

        timePicker_on=view.findViewById(R.id.timePicker_on);
        timePicker_off=view.findViewById(R.id.timePicker_off);

        btn_mixer = view.findViewById(R.id.btn_mixer);

        startMQTT();

        btn_mixer.setOnClickListener(v -> handleMixButtonClick());

        return view;
    }

    private void handleMixButtonClick() {

        String nameText = scheduler_name.getText().toString();

        String timeText_mix1 = mix1_timer.getText().toString();
        String timeText_mix2 = mix2_timer.getText().toString();
        String timeText_mix3 = mix3_timer.getText().toString();

        String timeText_area1 = area1_timer.getText().toString();
        String timeText_area2 = area2_timer.getText().toString();
        String timeText_area3 = area3_timer.getText().toString();

        String timeText_pump_in = pump_in.getText().toString();
        String timeText_pump_out = pump_out.getText().toString();

        String timeText_cycle = timePicker_cycle.getText().toString();

        String timeText_on = timePicker_on.getText().toString();
        String timeText_off = timePicker_off.getText().toString();



        if (nameText.isEmpty()) {
            showAlert("Bạn chưa nhập tên lịch tưới");
            return;
        }
        if (timeText_mix1.isEmpty()||timeText_mix2.isEmpty()||timeText_mix3.isEmpty()) {
            showAlert("Vui lòng kiểm tra lại lịch tưới bộ trộn");
            return;
        }
        if (timeText_area1.isEmpty()||timeText_area2.isEmpty()||timeText_area3.isEmpty()) {
            showAlert("Vui lòng kiểm tra lại lịch tưới khu vực");
            return;
        }
        if (timeText_pump_in.isEmpty()||timeText_pump_out.isEmpty()) {
            showAlert("Vui lòng kiểm tra lại lịch bơm/xả");
            return;
        }
        if (timeText_cycle.isEmpty()) {
            showAlert("Bạn chưa nhập thời gian kết thúc");
            return;
        }
        if (timeText_on.isEmpty()) {
            showAlert("Bạn chưa nhập thời gian bắt đầu");
            return;
        }
        if (timeText_off.isEmpty()) {
            showAlert("Bạn chưa nhập thời gian kết thúc");
            return;
        }
        if (!timeText_on.contains(":")) {
            showAlert("Vui lòng nhập đúng định dạng thời gian bắt đầu");
            return;
        }
        if (!timeText_off.contains(":")) {
            showAlert("Vui lòng nhập đúng định dạng thời gian kết thúc");
            return;
        }

            Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();

            int hour_on,minute_on,hour_off,minute_off,day,month,year;
            String shour_on , sminute_on ,shour_off,sminute_off,sday,smonth,syear,starttime,endtime,timePicker,detail;

            String[] timeParts_on = timeText_on.split(":");
            hour_on = Integer.parseInt(timeParts_on[0].trim());
            minute_on = Integer.parseInt(timeParts_on[1].trim());

            String[] timeParts_off = timeText_off.split(":");
            hour_off = Integer.parseInt(timeParts_off[0].trim());
            minute_off = Integer.parseInt(timeParts_off[1].trim());

            Calendar calendar = Calendar.getInstance();
            day=calendar.get(Calendar.DAY_OF_MONTH);
            month= calendar.get(Calendar.MONTH) + 1;
            year= calendar.get(Calendar.YEAR);

            shour_on = String.valueOf(hour_on);
            sminute_on = String.valueOf(minute_on);
            shour_off=String.valueOf(hour_off);
            sminute_off=String.valueOf(minute_off);
            sday=String.valueOf(day);
            smonth=String.valueOf(month);
            syear=String.valueOf(year);

            if(hour_on<10) {
                shour_on="0"+shour_on;
            }if(minute_on<10) {
                sminute_on="0"+sminute_on;
            }
            if(hour_off<10) {
                shour_off="0"+shour_off;
            }if(minute_off<10) {
                sminute_off="0"+sminute_off;
            }
            timePicker = sday + "/"+smonth+"/"+syear+"-"+shour_on+":"+sminute_on;
            starttime=shour_on+":"+sminute_on;
            endtime=shour_off+":"+sminute_off;

            detail = "Đặt lịch tưới \"" + nameText + "\" từ " + starttime + " đến " + endtime + " thành công.";
            addItemAndReload(timePicker, detail);
            Toast.makeText(getActivity(),  "Đặt lịch tưới "+nameText+ "bắt đầu tuới từ "+starttime+" đến " +endtime+" thành công.", Toast.LENGTH_SHORT).show();
            String value="{\"cycle\": "+timeText_cycle+
                    ", \"MIXER1\": "+timeText_mix1+", \"MIXER2\": "+timeText_mix2+", \"MIXER3\": "+timeText_mix3+
                    ", \"PUMP_IN\": "+timeText_pump_in+
                    ", \"SELECTOR1\": "+timeText_area1+", \"SELECTOR2\": "+timeText_area2+", \"SELECTOR3\": "+timeText_area3+
                    ", \"PUMP_OUT\": "+timeText_pump_out+
                    ", \"is_active\": true" +
                    ", \"name\": \""+nameText+
                    "\", \"start\": \""+starttime+"\", \"stop\": \"" +endtime+"\"}";
            sendDataMQTT(link,value);
            resetFields();
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    private void resetFields() {
        scheduler_name.setText("");;

        mix1_timer.setText("");
        mix2_timer.setText("");
        mix3_timer.setText("");

        area1_timer.setText("");
        area2_timer.setText("");
        area3_timer.setText("");

        pump_in.setText("");
        pump_out.setText("");

        timePicker_cycle.setText("");

        timePicker_on.setText("");
        timePicker_off.setText("");
    }

    private void addItemAndReload(String time, String detail) {
        Item item = new Item(time, detail);
        long id = db.addItem(item);
        if (id != -1) {
            loadData();
        } else {
            Log.e("TimerFragment", "Failed to insert item");
        }
    }

    private void loadData() {
        adapter = new RecycleViewAdapter();
        List<Item> list = db.getAll();
        Log.e("TimerFragment", "FHi");
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