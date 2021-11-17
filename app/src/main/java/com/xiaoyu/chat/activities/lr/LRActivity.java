package com.xiaoyu.chat.activities.lr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.activities.ActivateActivity;
import com.xiaoyu.chat.databinding.ActivityLrBinding;
import com.xiaoyu.chat.dialog.BottomSheetLogin;
import com.xiaoyu.chat.mail.JavaMailAPI;
import com.xiaoyu.chat.utilities.OnSingleClickListener;

import java.util.Calendar;
import java.util.TimeZone;

public class LRActivity extends AppCompatActivity {

    private ActivityLrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Current Time
        init();
        setListeners();
        getCurrentTime();
    }

    private void init() {
        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void setListeners() {
        binding.loginBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openLoginModal();
            }
        });

        binding.registBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right_v2,
                        R.anim.slide_out_left_v2);
            }
        });
    }

    private void getCurrentTime() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
//        Log.d("Log", "Current Time: " + timeOfDay);

//        binding.lottieBird.enableMergePathsForKitKatAndAbove(true);

        if (timeOfDay >= 0 && timeOfDay < 12)
        {
            binding.loginBackground.setBackgroundResource(R.drawable.login_background_m);
            binding.lottieBird.setAnimation(R.raw.login_background_m);
        }
        else if (timeOfDay >= 12 && timeOfDay < 18)
        {
            binding.loginBackground.setBackgroundResource(R.drawable.login_background_a);
            binding.lottieBird.setAnimation(R.raw.login_background_m);
        }
        else if (timeOfDay >= 18 && timeOfDay < 24)
        {
            binding.loginBackground.setBackgroundResource(R.drawable.login_background_n);
            binding.lottieBird.setAnimation(R.raw.login_background_n);
        }
    }

    private void openLoginModal() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);
//        View sheetView = LayoutInflater.from(getApplicationContext())
//                            .inflate(R.layout.bottom_sheet_login, (ConstraintLayout)findViewById(R.id.layout_login));
//        bottomSheetDialog.setContentView(sheetView);
//        bottomSheetDialog.show();
        BottomSheetLogin loginSheet = new BottomSheetLogin(getApplicationContext());
        loginSheet.show(getSupportFragmentManager(), "TAG");
    }

    protected void sendEmail() {
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, "xiaoyu.center.1998@gmail.com", "asdasd", "Halo");
        javaMailAPI.execute();
    }
}