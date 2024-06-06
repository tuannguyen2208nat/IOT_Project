package com.example.iot_project.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iot_project.R;

public class MixFragment extends Fragment {

    private Button btn_mix1, btn_mix2, btn_mix3;
    private ImageButton imgbtn_mix1, imgbtn_mix2, imgbtn_mix3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mix, container, false);

        btn_mix1 = view.findViewById(R.id.btn_mix_1);
        btn_mix2 = view.findViewById(R.id.btn_mix_2);
        btn_mix3 = view.findViewById(R.id.btn_mix_3);
        imgbtn_mix1 = view.findViewById(R.id.imgbtn_1);
        imgbtn_mix2 = view.findViewById(R.id.imgbtn_2);
        imgbtn_mix3 = view.findViewById(R.id.imgbtn_3);

        btn_mix1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Bộ trộn 1", Toast.LENGTH_SHORT).show();
            }
        });

        imgbtn_mix1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Bộ trộn 1", Toast.LENGTH_SHORT).show();
            }
        });

        btn_mix2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Bộ trộn 2", Toast.LENGTH_SHORT).show();
            }
        });

        imgbtn_mix2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Bộ trộn 2", Toast.LENGTH_SHORT).show();
            }
        });

        btn_mix3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Bộ trộn 3", Toast.LENGTH_SHORT).show();
            }
        });

        imgbtn_mix3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Bộ trộn 3", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
