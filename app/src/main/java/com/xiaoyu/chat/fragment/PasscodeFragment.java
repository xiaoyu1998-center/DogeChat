package com.xiaoyu.chat.fragment;

import static com.google.firebase.messaging.Constants.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.databinding.FragmentPasscodeBinding;
import com.xiaoyu.chat.utilities.Constants;
import com.xiaoyu.chat.utilities.OnSingleClickListener;
import com.xiaoyu.chat.utilities.PreferenceManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PasscodeFragment extends BaseFragment{

    private FragmentPasscodeBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    private final ArrayList<String> passcodeList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPasscodeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar Text
//        binding.toolbar.toolbarText.setText(binding.toolText.getText());

        init();
        setListeners();
    }

    private void init() {
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(requireContext());
        preferenceManager.putString(Constants.KEY_PASSCODE_TYPE, "FourDigit");
    }

    private void setListeners() {
        binding.passcodeOne.setOnClickListener(v -> addPlusORMinus(true, "1"));
        binding.passcodeTwo.setOnClickListener(v -> addPlusORMinus(true, "2"));
        binding.passcodeThree.setOnClickListener(v -> addPlusORMinus(true, "3"));
        binding.passcodeFour.setOnClickListener(v -> addPlusORMinus(true, "4"));
        binding.passcodeFive.setOnClickListener(v -> addPlusORMinus(true, "5"));
        binding.passcodeSix.setOnClickListener(v -> addPlusORMinus(true, "6"));
        binding.passcodeSeven.setOnClickListener(v -> addPlusORMinus(true, "7"));
        binding.passcodeEight.setOnClickListener(v -> addPlusORMinus(true, "8"));
        binding.passcodeNine.setOnClickListener(v -> addPlusORMinus(true, "9"));
        binding.passcodeZero.setOnClickListener(v -> addPlusORMinus(true, "0"));
        binding.passcodeReturn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_passcodeFragment_to_securityFragment);
            }
        });
        binding.passcodeDelete.setOnClickListener(v -> addPlusORMinus(false, ""));
        binding.passcodeType.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openDialog();
//                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
//                binding.passcodeLayout.startAnimation(animation);
            }
        });
    }

    private void openDialog() {
        String type = preferenceManager.getString(Constants.KEY_PASSCODE_TYPE);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(),
                R.style.AppBottomSheetDialogPasscodeTheme);

        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_passcode,
                        bottomSheetDialog.findViewById(R.id.PasscodeTypeLayout));

        if (type.matches("FourDigit")) {
            bottomSheetView.findViewById(R.id.sixDigitType).setVisibility(View.VISIBLE);
            bottomSheetView.findViewById(R.id.fourDigitType).setVisibility(View.GONE);
        } else {
            bottomSheetView.findViewById(R.id.sixDigitType).setVisibility(View.GONE);
            bottomSheetView.findViewById(R.id.fourDigitType).setVisibility(View.VISIBLE);
        }

        bottomSheetView.findViewById(R.id.sixDigitType).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                preferenceManager.putString(Constants.KEY_PASSCODE_TYPE, "6");
                changePasscodeType("SixDigit");
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.fourDigitType).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                changePasscodeType("FourDigit");
                bottomSheetDialog.dismiss();

            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void changePasscodeType(String digit) {
        switch (digit) {
            case "SixDigit":
                binding.codeFive.setVisibility(View.VISIBLE);
                binding.codeSix.setVisibility(View.VISIBLE);

                preferenceManager.putString(Constants.KEY_PASSCODE_TYPE, "SixDigit");

                passcodeList.clear();
                break;

            case "FourDigit":
                binding.codeFive.setVisibility(View.GONE);
                binding.codeSix.setVisibility(View.GONE);

                preferenceManager.putString(Constants.KEY_PASSCODE_TYPE, "FourDigit");

                passcodeList.clear();
                break;
        }

        checkPasscode();
    }

    private void checkPasscode() {
        Log.d(TAG, String.valueOf(passcodeList));

//        if (passcodeList == null) {
//            Log.d(TAG, "Tak ada");
//        } else {
            switch (passcodeList.size()) {
                case 0:
                    binding.codeOne.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    binding.codeTwo.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    binding.codeThree.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    binding.codeFour.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    break;

                case 1:
                    binding.codeOne.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeTwo.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    binding.codeThree.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    binding.codeFour.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    break;

                case 2:
                    binding.codeOne.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeTwo.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeThree.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    binding.codeFour.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    break;

                case 3:
                    binding.codeOne.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeTwo.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeThree.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeFour.setBackgroundResource(R.drawable.bg_drawable_passcode_disabled);
                    break;

                case 4:
                    binding.codeFour.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeOne.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeOne.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    binding.codeOne.setBackgroundResource(R.drawable.bg_drawable_passcode_enabled);
                    break;
                }
        //}

//        String code = "";
//
//        for (String object: passcodeList) {
//            System.out.println(object);
//            code += object;
//        }
//
//        Log.d(TAG, code);
    }

    private void addPlusORMinus(Boolean isAdd, String number) {
        if (isAdd) {
            if (passcodeList.size() < 4) {
                passcodeList.add(number);
            }
        } else {
            if (passcodeList.size() > 0) {
                int remove = passcodeList.size() - 1;
                passcodeList.remove(remove);
            }
        }

        checkPasscode();
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}