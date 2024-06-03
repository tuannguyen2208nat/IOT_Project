package com.example.iot_project;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

public class WaterFragment extends Fragment {

    private LinearLayout layout_btn_on, layout_tu_dong;
    private Button btn_thu_cong, btn_tu_dong, btnTuoi;
    private boolean chedo = false;
    private String gui = "testcode";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water, container, false);

        CheckBox area1 = view.findViewById(R.id.area1);
        CheckBox area2 = view.findViewById(R.id.area2);
        CheckBox area3 = view.findViewById(R.id.area3);
        layout_btn_on = view.findViewById(R.id.layout_btn_on);
        layout_tu_dong = view.findViewById(R.id.layout_tu_dong);
        btn_thu_cong = view.findViewById(R.id.btn_thu_cong);
        btn_tu_dong = view.findViewById(R.id.btn_tu_dong);
        btnTuoi = view.findViewById(R.id.btn_tuoi);
        layout_btn_on.setVisibility(View.GONE);
        layout_tu_dong.setVisibility(View.GONE);

        btn_thu_cong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_btn_on.setVisibility(View.VISIBLE);
                layout_tu_dong.setVisibility(View.VISIBLE);
                chedo = true;
            }
        });

        btn_tu_dong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_btn_on.setVisibility(View.VISIBLE);
                layout_tu_dong.setVisibility(View.GONE);
                chedo = true;
            }
        });

        btnTuoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!area1.isChecked() && !area2.isChecked() && !area3.isChecked()) {
                    showAlert("Bạn chưa chọn khu vực");
                } else {
                    if (!chedo) {
                        showAlert("Bạn chưa chọn chế độ");
                    } else {

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
