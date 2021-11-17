package com.xiaoyu.chat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.xiaoyu.chat.databinding.ActivityActivateBinding;
import com.xiaoyu.chat.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}