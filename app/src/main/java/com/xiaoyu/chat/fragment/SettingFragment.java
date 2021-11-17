package com.xiaoyu.chat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.activities.NavigationActivity;
import com.xiaoyu.chat.activities.lr.LRActivity;
import com.xiaoyu.chat.databinding.FragmentSettingBinding;
import com.xiaoyu.chat.utilities.Constants;
import com.xiaoyu.chat.utilities.NavController;
import com.xiaoyu.chat.utilities.OnSingleClickListener;
import com.xiaoyu.chat.utilities.PreferenceManager;

import java.util.HashMap;

import me.ibrahimsn.lib.SmoothBottomBar;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    private FragmentSettingBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_setting, container, false);
        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        loadUserDetails();
        setListeners();

        String reqString = Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();

        Log.e("Log Phone: ", reqString);
    }

    private void init() {
        preferenceManager = new PreferenceManager(getContext());
        database = FirebaseFirestore.getInstance();

        binding.appBarLayout.addOnOffsetChangedListener(isCollapsed);
    }

    private void setListeners() {
        binding.logoutBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {  signOut();
            }
        });

        binding.securityConstraintLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_settingFragment_to_securityFragment);
            }
        });

        binding.deviceConstraintLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_settingFragment_to_deviceFragment);
            }
        });
    }

    private void loadUserDetails() {
        binding.titleToolbar.setTitle(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void signOut() {
        showToast("Signing Out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    Intent intent = new Intent(getContext(), LRActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    private AppBarLayout.OnOffsetChangedListener isCollapsed = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset == 0) {
                // Fully expanded
                // System.out.println("Fully");
            } else {
                // Not fully expanded or collapsed
                // System.out.println("Not Fully");
            }
        }
    };

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}