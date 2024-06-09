package com.example.iot_project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.iot_project.databinding.ActivityMainBinding;
import com.example.iot_project.fragment.AreaFragment;
import com.example.iot_project.fragment.HomeFragment;
import com.example.iot_project.fragment.MixFragment;
import com.example.iot_project.fragment.WaterFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment(), "HOME");

        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                replaceFragment(new HomeFragment(), "HOME");
            } else if (item.getItemId() == R.id.nav_water) {
                replaceFragment(new WaterFragment(), "WATER");
            } else if (item.getItemId() == R.id.nav_mixer) {
                replaceFragment(new MixFragment(), "MIX");
            } else if (item.getItemId() == R.id.nav_area) {
                replaceFragment(new AreaFragment(), "AREA");
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, fragment, tag);
        fragmentTransaction.commit();
    }

}