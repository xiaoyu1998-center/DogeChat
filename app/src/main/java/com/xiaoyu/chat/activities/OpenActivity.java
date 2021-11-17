package com.xiaoyu.chat.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.airbnb.lottie.RenderMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.xiaoyu.chat.R;
import com.xiaoyu.chat.activities.lr.LRActivity;
import com.xiaoyu.chat.databinding.ActivityOpenBinding;
import com.xiaoyu.chat.utilities.Constants;
import com.xiaoyu.chat.utilities.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class OpenActivity extends AppCompatActivity {

    private ActivityOpenBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        checkAppStatus();
    }

    private void init() {
        handler = new Handler();
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void checkAppStatus() {
        loading(true);
        database.collection(Constants.KEY_COLLECTION_APP)
                .whereEqualTo(Constants.KEY_APP, Constants.KEY_APP_NAME)
                .get()
                .addOnCompleteListener(OnCompleteListener)
                .addOnFailureListener(exception -> {
                    // showToast(exception.getMessage());
                    Log.e(TAG, exception.getMessage());
                    loading(false);
                    binding.textStatus.setText(R.string.fail_update);
                });
    }

    private final OnCompleteListener<QuerySnapshot> OnCompleteListener = task -> {
        loading(false);

        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

            // Get Status
            final String status = documentSnapshot.getString(Constants.KEY_APP_STATUS);
            if (status.equals("100")) {
                binding.textStatus.setText(R.string.status_success);
                handler.postDelayed(() -> {
                    if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
                        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LRActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }, 1500);

            } else if (status.equals("200")) {
                binding.textStatus.setText(R.string.new_version);
                startedDownload(documentSnapshot.getString(Constants.KEY_APP_LINK));
            } else {
                binding.textStatus.setText(R.string.maintenance);
            }

            //showToast(documentSnapshot.getString(Constants.KEY_APP_STATUS));
        } else {
            binding.textStatus.setText(R.string.cant_find);
        }
    };

    private void loading(Boolean isLoading) {
        binding.lottieBird.setRenderMode(RenderMode.HARDWARE);

        if (isLoading) {
            binding.lottieBird.setAnimation(R.raw.loading_circle);
            binding.textStatus.setText(R.string.loading);
        } else {
            binding.lottieBird.setAnimation(R.raw.loading_finish);
            binding.lottieBird.setRepeatCount(0);
            binding.lottieBird.playAnimation();
        }
    }

    private void startedDownload(String url) {
        DownloadManager.Request manager = new DownloadManager.Request(Uri.parse(url));
        String title = URLUtil.guessFileName(url, null, null);
        manager.setTitle(title);
        manager.setDescription("Downloading File please wait.....");
        String cookie = CookieManager.getInstance().getCookie(url);
        manager.addRequestHeader("cookie", cookie);
        manager.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(manager);

        showToast("Downloading Started.");
    }

}