package com.ashvinprajapati.skillconnect.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.fragments.BrowseFragment;
import com.ashvinprajapati.skillconnect.fragments.MessageFragment;
import com.ashvinprajapati.skillconnect.fragments.PostServiceFragment;
import com.ashvinprajapati.skillconnect.fragments.ProfileFragment;
import com.ashvinprajapati.skillconnect.models.User;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.AuthApiService;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.utils.Refreshable;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    private Fragment browseFragment;
    private Fragment postFragment;
    private Fragment messageFragment;
    private Fragment profileFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔐 Token check
        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        initViews();
        setupNotifications();
        storeCurrentUserId();
        setupFragments();
        setupBottomNavigation();
        setupFcmToken();
    }

    // ---------------- INIT ----------------

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        frameLayout = findViewById(R.id.frameLayout);
    }

    // ---------------- FRAGMENTS ----------------

    private void setupFragments() {

        browseFragment = new BrowseFragment();
        postFragment = new PostServiceFragment();
        messageFragment = new MessageFragment();
        profileFragment = new ProfileFragment();

        activeFragment = browseFragment;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, profileFragment, "PROFILE").hide(profileFragment)
                .add(R.id.frameLayout, messageFragment, "MESSAGE").hide(messageFragment)
                .add(R.id.frameLayout, postFragment, "POST").hide(postFragment)
                .add(R.id.frameLayout, browseFragment, "BROWSE")
                .commit();

        bottomNavigationView.setSelectedItemId(R.id.browseNavigation);
    }

    // ---------------- BOTTOM NAV ----------------

    private void setupBottomNavigation() {

        bottomNavigationView.setOnItemSelectedListener(item -> {

            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();

            if (item.getItemId() == R.id.browseNavigation) {
                toolbar.setTitle("Browse");
                transaction.hide(activeFragment).show(browseFragment).commit();
                activeFragment = browseFragment;
                return true;
            }

            if (item.getItemId() == R.id.postNavigation) {
                toolbar.setTitle("Post Service");
                transaction.hide(activeFragment).show(postFragment).commit();
                activeFragment = postFragment;
                return true;
            }

            if (item.getItemId() == R.id.messageNavigation) {
                toolbar.setTitle("Messages");
                transaction.hide(activeFragment).show(messageFragment).commit();
                activeFragment = messageFragment;
                return true;
            }

            if (item.getItemId() == R.id.profileNavigation) {
                toolbar.setTitle("Profile");
                transaction.hide(activeFragment).show(profileFragment).commit();
                activeFragment = profileFragment;
                return true;
            }

            return false;
        });

        // 🔁 Refresh on reselect
        bottomNavigationView.setOnItemReselectedListener(item -> {
            if (activeFragment instanceof Refreshable) {
                ((Refreshable) activeFragment).onRefresh();
            }
        });
    }

    // ---------------- USER / FCM ----------------

    private void storeCurrentUserId() {
        AuthApiService authApiService = ApiClient.getClient(this).create(AuthApiService.class);
        authApiService.getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences prefs =
                            getSharedPreferences("SkillConnectPrefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("currentUserId", String.valueOf(response.body().getId()))
                            .apply();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("USER", "Failed to fetch user");
            }
        });
    }

    private void setupFcmToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    SharedPreferences prefs =
                            getSharedPreferences("SkillConnectPrefs", MODE_PRIVATE);
                    prefs.edit().putString("fcm_token", token).apply();

                    String email = prefs.getString("email", null);
                    if (email != null) {
                        ApiClient.getClient(this)
                                .create(UserApiService.class)
                                .updateFcmToken(email, token)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("FCM", "Token updated");
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("FCM", "Token update failed");
                                    }
                                });
                    }
                });
    }

    // ---------------- NOTIFICATIONS ----------------

    private void setupNotifications() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1001
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "FCM_CHANNEL",
                    "Firebase Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
