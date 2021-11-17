package com.xiaoyu.chat.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoyu.chat.R;
import com.xiaoyu.chat.databinding.FragmentPasscodeBinding;
import com.xiaoyu.chat.databinding.FragmentRulePasscodeBinding;
import com.xiaoyu.chat.utilities.OnSingleClickListener;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RulePasscodeFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    private FragmentRulePasscodeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRulePasscodeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set Toolbar Title
        binding.toolbar.toolbarText.setText(binding.toolText.getText());

        setListeners();
    }

    private void setListeners() {
        binding.buttonPasscode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_rulePasscodeFragment_to_passcodeFragment2);
            }
        });

        binding.toolbar.backArrow.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
    }
}