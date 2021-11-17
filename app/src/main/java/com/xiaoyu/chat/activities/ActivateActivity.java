package com.xiaoyu.chat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.activities.lr.RegisterActivity;
import com.xiaoyu.chat.databinding.ActivityActivateBinding;
import com.xiaoyu.chat.databinding.ActivityRegisterBinding;
import com.xiaoyu.chat.mail.MailSender;
import com.xiaoyu.chat.mail.Utils;
import com.xiaoyu.chat.utilities.Constants;
import com.xiaoyu.chat.utilities.OnSingleClickListener;
import com.xiaoyu.chat.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.sun.mail.imap.protocol.INTERNALDATE.format;

public class ActivateActivity extends AppCompatActivity {

    private ActivityActivateBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    private DocumentReference documentReference;

    // Prevent keep click otp
    private static int countTimer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
        setupOTPInputs();
        setResendOTP();
    }

    private String getUserID() {
        Intent intent = getIntent();
        // return intent.getStringExtra(Constants.GET_USER_ID_BY_VERIFY);
        return "s";
    }

    private String getUserEmail() {
        Intent intent = getIntent();
        // return intent.getStringExtra(Constants.GET_USER_EMAIL_BY_VERIFY);
        return "s";
    }

    private String getUserName() {
        Intent intent = getIntent();
        // return intent.getStringExtra(Constants.GET_USER_EMAIL_BY_VERIFY);
        return "s";
    }

    private void init() {
        database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(getUserID());
        preferenceManager = new PreferenceManager(getApplicationContext());

    }

    private void setListeners() {
        binding.buttonVerify.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) { verifyAccount(); }
        });
    }

    private void setupOTPInputs() {
        binding.inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode3.requestFocus();
                } else {
                    binding.inputCode1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode4.requestFocus();
                } else {
                    binding.inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    binding.inputCode5.requestFocus();
                } else {
                    binding.inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.inputCode5.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    verifyAccount();
                }
                return false;
            }
        });
    }

    private void verifyAccount() {
        final String verifyCode =   binding.inputCode1.getText().toString() +
                                    binding.inputCode2.getText().toString() +
                                    binding.inputCode3.getText().toString() +
                                    binding.inputCode4.getText().toString() +
                                    binding.inputCode5.getText().toString() ;

        if (verifyCode.length() == 5) {
            checkAccountStatus(verifyCode);
        } else {
            showToast("Please input the 5-digit verification code.");
        }

//        Log.e("Code : ", verifyCode);
//        documentReference.update(Constants.KEY_STATUS, Constants.KEY_SUCCESS);
    }

    private void checkAccountStatus(String code) {
        isVerify(false);
        loading(true);
//        Log.e("Answer : ", documentReference.getId());

        documentReference.get().addOnSuccessListener(task -> {
//            Log.e("Date : ", String.valueOf(task.getDate(Constants.KEY_CODE_EXPIRATION)));

            if (task.getString(Constants.KEY_STATUS).matches(Constants.KEY_PENDING)) {
                if (task.getString(Constants.KEY_CODE).matches(code)) {
                    if (checkVerifyCode(task.getDate(Constants.KEY_CODE_EXPIRATION))) {
                        isVerify(false);
//                        documentReference.update(Constants.KEY_STATUS, Constants.KEY_SUCCESS);
//                        System.out.println("ID : " + task.getId());
//                        System.out.println("Name : " + task.getString(Constants.KEY_NAME));
//                        System.out.println("Email : " + task.getString(Constants.KEY_EMAIL));
//                        System.out.println("Image : " + task.getString(Constants.KEY_IMAGE));

                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                        preferenceManager.putString(Constants.KEY_NAME, task.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, task.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_IMAGE, task.getString(Constants.KEY_IMAGE));

                        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        isVerify(true);
                        loading(false);
                    }
                } else {
                    isVerify(true);
                    loading(false);
                }
            } else if (task.getString(Constants.KEY_STATUS).matches(Constants.KEY_SUCCESS)) {
                showToast("Already Success");
                loading(false);
            } else {
                showToast("Something Wrong");
                loading(false);
            }
        });
    }

    private Boolean checkVerifyCode(Date date) {
        Calendar calUser = Calendar.getInstance();
        calUser.setTime(date);
        int min = calUser.get(Calendar.MINUTE);
        calUser.set(Calendar.MINUTE, (min + 30));
        Date expDate = calUser.getTime();
//        Log.e("EXP Date :", String.valueOf(expDate));

        Calendar calExp = Calendar.getInstance();
        Date currentDate = calExp.getTime();
//        Log.e("Current Date :", String.valueOf(currentDate));

        // date1 is a present date and date2 is tomorrow date
        if (expDate.compareTo(currentDate) > 0) {

            //  0 comes when two date are same,
            //  1 comes when expDate is higher then currentDate
            // -1 comes when expDate is lower then currentDate
            return true;
        } else {
            return false;
        }
    }

    private void setResendOTP() {
//        Log.e("Timer Count : ", String.valueOf(countTimer));
        if (countTimer <= 3) {
            SpannableString spString = new SpannableString("Didn't received the OTP? RESEND OTP");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    countDownTimerOTP();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            spString.setSpan(clickableSpan, 25, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.resendOTP.setText(spString);
            binding.resendOTP.setMovementMethod(LinkMovementMethod.getInstance());
            binding.resendOTP.setHighlightColor(Color.TRANSPARENT);
        } else {
            binding.resendOTP.setText(R.string.too_many);
            binding.resendOTP.setTextColor(getApplicationContext().getResources().getColor(R.color.error));
        }
    }

    private void countDownTimerOTP() {
        isVerify(false);
        countTimer += 1;

        String randomDigit = getRandomSixDigitNumber();

        documentReference.update(Constants.KEY_CODE, randomDigit);
        resendOTPEmail(randomDigit);

        final long duration = TimeUnit.MINUTES.toMillis(countTimer);

        new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d"
                        , TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                        , TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                String coldTimer = "Resend OTP in : <font color=#ff0000>" + sDuration + "</font>";
                binding.resendOTP.setText(Html.fromHtml(coldTimer));
            }

            public void onFinish() {
                setResendOTP();
            }
        }.start();
    }

    private void isVerify(Boolean isVerify) {
        if (isVerify) {
            binding.verify.setVisibility(View.GONE);
            binding.verifyInvalid.setVisibility(View.VISIBLE);
        } else {
            binding.verifyInvalid.setVisibility(View.GONE);
            binding.verify.setVisibility(View.VISIBLE);
        }
    }

    private String getRandomSixDigitNumber() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(99999);

        // this will convert any number sequence into 6 character.
        return String.format("%05d", number);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonVerify.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonVerify.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void resendOTPEmail(String code) {
        new MailCreator().execute(getUserEmail(), getUserName(), code);
    }

    public class MailCreator extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String email = strings[0];
            String name = strings[1];
            String code = strings[2];

//            Log.e("Log : ", email);
//            Log.e("Log : ", name);

            try {
                String subject = code + " is your Xiaoyu Chat verification code";

                MailSender sender = new MailSender(getBaseContext(), Utils.EMAIL, Utils.PASSWORD);
                sender.sendUserDetailWithImage(subject, "", email,
                        Utils.RECIPEINT_MAIL, name, code);

            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Toast.makeText(getActivity(),"Mail Sent",Toast.LENGTH_LONG).show();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}