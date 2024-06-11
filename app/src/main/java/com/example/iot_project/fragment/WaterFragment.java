package com.example.iot_project.fragment;

import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.iot_project.R;
import com.example.iot_project.database.MQTTHelper;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

public class WaterFragment extends Fragment {
    MQTTHelper mqttHelper;
    String link="tuannguyen2208natIOT/feeds/routine";
    private LinearLayout layout_pump_in, layout_pump_out;
    Button btn_pump_in,btn_pump_out,btnTuoi, btnTat;
    ImageButton imgbtn_pump_in, imgbtn_pump_out;
    private LinearLayout currentSelectedLayout = null;
    EditText water;
    int waterInt = 0;
    int area=0,mode=0;
    CheckBox area1, area2, area3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water, container, false);
        mqttHelper = new MQTTHelper(getContext());

        layout_pump_in = view.findViewById(R.id.layout_pump_in);
        layout_pump_out = view.findViewById(R.id.layout_pump_out);
        btn_pump_in=view.findViewById(R.id.btn_pump_in);
        btn_pump_out=view.findViewById(R.id.btn_pump_out);
        imgbtn_pump_in=view.findViewById(R.id.imgbtn_pump_in);
        imgbtn_pump_out=view.findViewById(R.id.imgbtn_pump_out);

        area1 = view.findViewById(R.id.area1);
        area2 = view.findViewById(R.id.area2);
        area3 = view.findViewById(R.id.area3);

        btnTuoi = view.findViewById(R.id.btn_tuoi);
        btnTat = view.findViewById(R.id.btn_tat);
        water = view.findViewById(R.id.water);

        setupButtonClickListener(btn_pump_in, layout_pump_in);
        setupButtonClickListener(btn_pump_out, layout_pump_out);

        setupImageButtonClickListener(imgbtn_pump_in, layout_pump_in);
        setupImageButtonClickListener(imgbtn_pump_out, layout_pump_out);

        btnTuoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mode != 1) && (mode != 2)) {
                    showAlert("Vui lòng chọn chế độ.");
                    return;
                    }
                if (!area1.isChecked() && !area2.isChecked() && !area3.isChecked()) {
                    showAlert("Vui lòng chọn khu vực.");
                    return;
                }
                String waterText = water.getText().toString();
                if (waterText.isEmpty()) {
                    showAlert("Vui lòng nhập lượng nước.");
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
                int val=mode+6;
                sendDataMQTT(link,val+"/on");
                Toast.makeText(getActivity(), "Máy bơm tưới cây khu vực "+ area + " bắt đầu tuới", Toast.LENGTH_SHORT).show();
                resetFields();
            }

        });

        btnTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!area1.isChecked() && !area2.isChecked() && !area3.isChecked()) {
                    showAlert("Bạn chưa chọn khu vực");
                    return;}
                Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
                    if (area1.isChecked()) {
                        area = 1;
                    } else if (area2.isChecked()) {
                        area = 2;
                    } else if (area3.isChecked()) {
                        area = 3;
                    }
                    Toast.makeText(getActivity(), "Đang xử lý...", Toast.LENGTH_SHORT).show();
                    int val=mode+6;
                    sendDataMQTT(link,val+"/off");
                    Toast.makeText(getActivity(), "Máy bơm tưới cây khu vực "+ area + " kết thúc tuới", Toast.LENGTH_SHORT).show();
                    resetFields();
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

    private void setupButtonClickListener(Button button, LinearLayout layout) {
        button.setOnClickListener(v -> changeLayoutColor(layout));
    }

    private void setupImageButtonClickListener(ImageButton imageButton, LinearLayout layout) {
        imageButton.setOnClickListener(v -> changeLayoutColor(layout));
    }
    private void changeLayoutColor(LinearLayout newLayout) {
        if (currentSelectedLayout != null) {
            currentSelectedLayout.setBackgroundResource(R.drawable.border_black_2dp);
        }
        newLayout.setBackgroundResource(R.drawable.border_black_2dp_background);
        currentSelectedLayout = newLayout;
        if (currentSelectedLayout == layout_pump_in) {
            mode = 1;
        } else if (currentSelectedLayout == layout_pump_out) {
            mode = 2;
        }
    }

    private void resetFields() {
        water.setText("");
        area1.setChecked(false);
        area2.setChecked(false);
        area3.setChecked(false);
        area=0;

    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
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
            Log.e("WaterFragment","WaterFragment can't send data");
        }
    }
}
