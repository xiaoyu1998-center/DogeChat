package com.xiaoyu.chat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

import com.xiaoyu.chat.databinding.ActivityNavigationBinding;
import com.xiaoyu.chat.fragment.HomeFragment;
import com.xiaoyu.chat.utilities.Constants;

import me.ibrahimsn.lib.SmoothBottomBar;

public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;
    private HomeFragment HomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setupNavigationComponent();
        init();
    }

    private void init() {
//        setupNavigationBar();
    }

    @SuppressLint("ResourceType")
    private void setupNavigationComponent() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(binding.navHostFragment.getId());
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(binding.bottomBar.getItemMenuRes());
        Menu menu = popupMenu.getMenu();

        binding.bottomBar.setupWithNavController(menu, navController);
    }

    public void hideNavigationBar(Boolean isHide){
        if (isHide) {
            binding.bottomBar.setVisibility(View.GONE);
        } else {
            binding.bottomBar.setVisibility(View.VISIBLE);
        }
    }

    public SmoothBottomBar getNav() {
        return binding.bottomBar;
    }

//    private void setupNavigationBar() {
//        replacedFragment(new HomeFragment());
//        binding.bottomBar.setOnItemSelectedListener(selectedListener);
//    }
//
//    private final OnItemSelectedListener selectedListener = new OnItemSelectedListener() {
//        @Override
//        public boolean onItemSelect(int i) {
////            System.out.println(i);
//
//            switch (i) {
//                case 0:
//                    replacedFragment(new HomeFragment());
//                    break;
//
//                case 1:
//                    replacedFragment(new SettingFragment());
//                    break;
//
//                default:
//                    break;
//            }
//            return true;
//        }
//    };
//
//    private void replacedFragment(Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(binding.fragmentLayout.getId(), fragment);
//        transaction.commit();
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        // I removed the below condition check because I still want to hide System UI even if activity
//        // loses focus. Such cases include spinner dropdown list is open, dialog box is displayed, etc.
//        // if(hasFocus)
//        // hideSystemUI();
//    }
//
//    public SmoothBottomBar getNav() {
//        return binding.bottomBar;
//    }
//
//    private void hideSystemUI() {
//        // Enables sticky immersive mode.
//        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE_STICKY.
//        // Or for "regular immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
//    }
//
//    // Shows the system bars by removing all the flags
//    // except for the ones that make the content appear under the system bars.
//    private void showSystemUI() {
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//    }
}