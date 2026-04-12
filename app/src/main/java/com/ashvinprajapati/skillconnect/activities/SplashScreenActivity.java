package com.ashvinprajapati.skillconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.ashvinprajapati.skillconnect.databinding.ActivitySplashScreenBinding;
import com.ashvinprajapati.skillconnect.utils.TokenManager;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000;
    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loadingAnim.playAnimation();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TokenManager tokenManager = new TokenManager(SplashScreenActivity.this);
            Class<?> destination = tokenManager.getToken() != null
                    ? MainActivity.class
                    : WelcomeScreenActivity.class;

            Intent intent = new Intent(SplashScreenActivity.this, destination);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}