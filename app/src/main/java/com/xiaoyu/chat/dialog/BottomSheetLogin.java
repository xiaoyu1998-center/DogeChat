package com.xiaoyu.chat.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.activities.NavigationActivity;
import com.xiaoyu.chat.databinding.BottomSheetLoginBinding;
import com.xiaoyu.chat.utilities.Constants;
import com.xiaoyu.chat.utilities.OnSingleClickListener;
import com.xiaoyu.chat.utilities.PreferenceManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BottomSheetLogin extends BottomSheetDialogFragment {

    private PreferenceManager preferenceManager;
    private BottomSheetLoginBinding binding;

    public BottomSheetLogin(Context context) {
        preferenceManager = new PreferenceManager(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetLoginBinding.inflate(getLayoutInflater());

        setListeners();
        setInputTextListeners();

        return binding.getRoot();
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    private void setListeners() {
        binding.buttonSignIn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (isValidEmailDetails() && isValidPasswordDetails()) {
                    signIn();
                } else {
                    showToast("Please Input correct email / password.");
                }
            }
        });
    }

//    private void setSignUpText() {
//        SpannableString spString = new SpannableString("Don't have an account? Sign up.");
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//                Toast.makeText(getContext(), "Halo", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setUnderlineText(false);
//            }
//        };
//        spString.setSpan(clickableSpan, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        signUpText.setText(spString);
//        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
//        signUpText.setHighlightColor(Color.TRANSPARENT);
//    }

    private void setInputTextListeners() {

//        binding.editEmailText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                //showToast(s.toString());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // showToast(s.toString());
//                if (s.length() > 0) {
//                    if (isValidEmailDetails()) {
//                        binding.inputEmailText.setEndIconDrawable(R.drawable.correct_icon);
//                    } else {
//                        binding.inputEmailText.setEndIconDrawable(R.drawable.wrong_icon);
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                //showToast(s.toString());
//            }
//        });

//        binding.editPasswordText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (isValidPasswordDetails()) {
//                    binding.inputPasswordText.setEndIconDrawable(R.drawable.correct_icon);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        binding.editPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (isValidEmailDetails() && isValidPasswordDetails()) {
                        signIn();
                    } else {
                        showToast("Please Input correct email / password.");
                    }
                }
                return false;
            }
        });
    }

    private Boolean isValidEmailDetails() {
        if (binding.inputEmailText.getEditText().getText().toString().trim().isEmpty()) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmailText.getEditText().getText().toString()).matches()) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean isValidPasswordDetails() {
        if (binding.inputPasswordText.getEditText().getText().toString().trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void clearAllInputText() {
        binding.editEmailText.setText(null);
        binding.editPasswordText.setText(null);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            this.setCancelable(false);
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            this.setCancelable(true);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void signIn() {
        loading(true);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmailText.getEditText().getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, md5(binding.inputPasswordText.getEditText().getText().toString()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getContext(), NavigationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign in.");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onResume() {
        super.onResume();
        clearAllInputText();
    }
}
