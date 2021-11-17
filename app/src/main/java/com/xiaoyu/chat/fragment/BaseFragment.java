package com.xiaoyu.chat.fragment;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.utilities.Constants;
import com.xiaoyu.chat.utilities.PreferenceManager;

import java.util.List;

import me.ibrahimsn.lib.SmoothBottomBar;

public abstract class BaseFragment extends Fragment {

    private DocumentReference documentReference;
    private static final int TIME_INTERVAL = 2000;
    boolean doubleBackToExitPressedOnce = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));

        checkEnabledBackPressed();
    }

    private void checkCurrentFragment() {
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Fragment mapFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();

        if (mapFragment instanceof HomeFragment || mapFragment instanceof SettingFragment) {
            visibleBottomBar(false);
        } else {
            visibleBottomBar(true);
        }

        Log.d(TAG, "Current Fragment :" + mapFragment);
    }

    private void visibleBottomBar(Boolean isHide){
        SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);

        if (smoothBottomBar != null) {
            if (isHide) {
                if (smoothBottomBar.getVisibility() != View.GONE) {
                    smoothBottomBar.setVisibility(View.GONE);
                }
            } else {
                if (smoothBottomBar.getVisibility() != View.VISIBLE) {
                    smoothBottomBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    public String getTag(Fragment fragment) {
        return fragment.getTag();
    }

    @Override
    public void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_AVAILABILITY, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        documentReference.update(Constants.KEY_AVAILABILITY, 1);

        checkCurrentFragment();
    }

    private void checkEnabledBackPressed(){
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Fragment mapFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();

        if (mapFragment instanceof HomeFragment || mapFragment instanceof SettingFragment) {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        }
    }

    private OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                getActivity().finishAffinity();
            }

            doubleBackToExitPressedOnce = true;
            showToast("Please click BACK again to exit");

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, TIME_INTERVAL);
        }
    };
}