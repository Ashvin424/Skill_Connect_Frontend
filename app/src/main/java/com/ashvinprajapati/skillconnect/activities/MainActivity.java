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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.fragments.BrowseFragment;
import com.ashvinprajapati.skillconnect.fragments.MessageFragment;
import com.ashvinprajapati.skillconnect.fragments.PostServiceFragment;
import com.ashvinprajapati.skillconnect.fragments.ProfileFragment;
import com.ashvinprajapati.skillconnect.models.User;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.AuthApiService;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView titleTextView;
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    Fragment browseFragment = new BrowseFragment();
    Fragment postFragment = new PostServiceFragment();
    Fragment messageFragment = new MessageFragment();
    Fragment profileFragment = new ProfileFragment();

    Fragment active = browseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
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

        AuthApiService authApiService = ApiClient.getClient(this).create(AuthApiService.class);
        authApiService.getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    User user = response.body();
                    SharedPreferences prefs = getSharedPreferences("SkillConnectPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("currentUserId", String.valueOf(user.getId()));
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to store UserId", Toast.LENGTH_SHORT).show();
            }
        });


        // ✅ Token check first
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        Log.d("TOKEN_CHECK", "Token in MainActivity: " + token);
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        String email = getIntent().getStringExtra("email");
        if (email != null) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String fcmToken = task.getResult();
                        Log.d("FCM", "FCM Token: " + fcmToken);

                        // Save token in shared prefs (optional)
                        SharedPreferences preferences = getSharedPreferences("SkillConnectPrefs", MODE_PRIVATE);
                        preferences.edit().putString("fcm_token", fcmToken).apply();

                        // Upload token to backend
                        String emailLogin = preferences.getString("email", null); // make sure this is saved during login
                        if (emailLogin != null) {
                            updateFcmTokenOnBackend(email, fcmToken);
                        }
                    });
        }



        init();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, profileFragment, "4").hide(profileFragment).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, messageFragment, "3").hide(messageFragment).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, postFragment, "2").hide(postFragment).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, browseFragment, "1").commit();

        titleTextView.setText("Browse");
        active = browseFragment;

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.browseNavigation) {
                titleTextView.setText("Browse");
                fragment = new BrowseFragment();
            } else if (item.getItemId() == R.id.postNavigation) {
                titleTextView.setText("Post Service");
                fragment = new PostServiceFragment();
            } else if (item.getItemId() == R.id.messageNavigation) {
                titleTextView.setText("Message");
                fragment = new MessageFragment();
            } else if (item.getItemId() == R.id.profileNavigation) {
                titleTextView.setText("Profile");
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, fragment)
                        .commitAllowingStateLoss();
            }

            return true;
        });



    }


    public void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        titleTextView = findViewById(R.id.titleTextView);
        frameLayout = findViewById(R.id.frameLayout);
    }
    private void updateFcmTokenOnBackend(String email, String token) {
        ApiClient.getClient(this).create(UserApiService.class).updateFcmToken(email, token)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("FCM", "FCM token updated successfully on backend");
                        } else {
                            Log.e("FCM", "Failed to update FCM token on backend");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("FCM", "Error updating FCM token: " + t.getMessage());
                    }
                });
    }
    private void updateFcmTokenToBackend(String userEmail) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM_TOKEN", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM_TOKEN", "Token: " + token);


                    ApiClient.getClient(this).create(AuthApiService.class).updateFcmToken(userEmail, token).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("FCM_TOKEN", "Token updated successfully.");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("FCM_TOKEN", "Token update failed: " + t.getMessage());
                        }
                    });
                });
    }
}
