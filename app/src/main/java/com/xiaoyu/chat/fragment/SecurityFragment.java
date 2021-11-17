package com.xiaoyu.chat.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoyu.chat.R;
import com.xiaoyu.chat.databinding.FragmentSecurityBinding;
import com.xiaoyu.chat.utilities.OnSingleClickListener;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SecurityFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    private FragmentSecurityBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSecurityBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set Toolbar Title
        binding.toolbar.toolbarText.setText(binding.toolText.getText());
        //R.string.privacy_and_security

        init();
        setListeners();
    }

    private void init() {

    }

    private void setListeners() {
        binding.toolbar.backArrow.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // handleOnBackPressed();
                // getActivity().getSupportFragmentManager().popBackStack();
                Navigation.findNavController(v).navigateUp();
            }
        });

        binding.passcodeLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_securityFragment_to_rulePasscodeFragment);
            }
        });
    }

//    public boolean handleOnBackPressed() {
//        //Do your job here
//        //use next line if you just need navigate up
//        //NavHostFragment.findNavController(this).navigateUp();
//        //Log.e(getClass().getSimpleName(), "handleOnBackPressed");
//        return true;
//    }

//    OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//        @Override
//        public void handleOnBackPressed() {
//            // Handle the back button event
//            requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
//            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_securityFragment_to_passcodeFragment);
//        }
//    };
}