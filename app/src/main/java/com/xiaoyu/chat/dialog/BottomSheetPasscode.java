package com.xiaoyu.chat.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.databinding.BottomSheetPasscodeBinding;

public class BottomSheetPasscode extends BottomSheetDialogFragment {

    private BottomSheetPasscodeBinding binding;

    public BottomSheetPasscode() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetPasscodeBinding.inflate(getLayoutInflater());

        setListeners();

        return binding.getRoot();
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    private void setListeners() {
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
