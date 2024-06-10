package com.example.iot_project.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.iot_project.R;
import com.example.iot_project.adapter.RecycleViewAdapter;
import com.example.iot_project.database.MQTTHelper;
import com.example.iot_project.database.SQLiteHelper;
import com.example.iot_project.model.Item;
import com.squareup.picasso.Picasso;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    MQTTHelper mqttHelper;
    private static final String API_KEY = "37294f583d2e566162db243302715283";
    private RecycleViewAdapter adapter;
    RecyclerView recyclerView;
    private SQLiteHelper db;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageView imgWeatherIcon = view.findViewById(R.id.imgWeatherIcon);
        TextView txtTemp = view.findViewById(R.id.txtTemp);
        TextView txtHumidity = view.findViewById(R.id.txtHumidity);
        TextView txtSpeed = view.findViewById(R.id.txtSpeed);
        TextView txtDate = view.findViewById(R.id.txtDate);
        TextView textview1=view.findViewById(R.id.textview1);
        TextView textview2=view.findViewById(R.id.textview2);
        getJsonWeather(imgWeatherIcon,txtSpeed, txtDate);
        startMQTT(txtTemp, txtHumidity);
        startDataUpdate(textview1,textview2);
        refresh_auto();
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void getJsonWeather(ImageView imgWeatherIcon,  TextView txtSpeed, TextView txtDate) {
        String url = "https://api.openweathermap.org/data/2.5/weather?id=1566083&appid=" + API_KEY;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray weatherArray = response.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String icon = weatherObject.getString("icon");
                        String urlIcon = "http://openweathermap.org/img/wn/" + icon + "@2x.png";
                        Picasso.get().load(urlIcon).into(imgWeatherIcon);

                        JSONObject wind = response.getJSONObject("wind");
                        String speed = wind.getString("speed") + "m/s";

                        String sNgay = response.getString("dt");
                        long lNgay = Long.parseLong(sNgay) * 1000L;
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE , dd/MM/yyyy");
                        Date date = new Date(lNgay);
                        String currentTime = sdf.format(date);

                        txtDate.setText(currentTime);
                        txtSpeed.setText(speed);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    Log.d("My error:", error.toString());
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new RecycleViewAdapter();
        db = new SQLiteHelper(getContext());
        List<Item> list = db.getAll();
        adapter.setList(list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void updateDataFromDatabase() {
        List<Item> list = db.getAll();
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void startMQTT(TextView txtTemp, TextView txtHumidity) {
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (topic.contains("temp")) {
                                txtTemp.setText(message.toString() + "°C");
                            } else if (topic.contains("humid")) {
                                txtHumidity.setText(message.toString() + "%");
                            } else if (topic.contains("routine")) {
                            }
                        } catch (NumberFormatException e) {
                            Log.e("Error", "Failed to parse message to integer: " + e.getMessage());
                        }
                    }
                });
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void startDataUpdate(TextView textview1, TextView textview2) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int Data1, Data2;
                int randomIncrement1 = random.nextInt(500) + 1;
                int randomIncrement2 = random.nextInt(500) + 1;

                String oldData1 = textview1.getText().toString();
                String oldData2 = textview2.getText().toString();
                boolean containsML1 = oldData1.contains("ml");
                boolean containsML2 = oldData2.contains("ml");

                int data1 = containsML1 ? Integer.parseInt(oldData1.replaceAll("\\D+", "")) : 0;
                int data2 = containsML2 ? Integer.parseInt(oldData2.replaceAll("\\D+", "")) : 0;

                Data1 = data1 - randomIncrement1;
                Data2 = data2 - randomIncrement2;
                if(Data1<=0)
                {
                    Data1=5000;
                }
                if(Data2<=0)
                {
                    Data2=5000;
                }
                String newData1 = Data1 + "ml";
                String newData2 = Data2 + "ml";
                textview1.setText(newData1);
                textview2.setText(newData2);
                handler.postDelayed(this, 10000);
            }
        }, 0);
    }
    private void refresh_auto() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onResume();
                handler.postDelayed(this, 100);
            }
        }, 0);
    }



}