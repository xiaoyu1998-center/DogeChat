package com.xiaoyu.chat.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoyu.chat.databinding.FragmentDeviceBinding;
import com.xiaoyu.chat.utilities.OnSingleClickListener;

public class DeviceFragment extends Fragment {

    private FragmentDeviceBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDeviceBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.toolbarText.setText("Devices");

        init();
        setListeners();
    }

    private void init() {

    }

    private void setListeners() {
        binding.toolbar.backArrow.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
    }
}