package com.ashvinprajapati.skillconnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.AuthApiService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout editProfileLayout, passwordChangeLayout, appSettingLayout, supportLayout;
    private MaterialButton logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init(); // Initialize views
        onClickListeners(); // All click listeners
    }

    private void onClickListeners() {

        //Toolbar Navigation Icon Click Listener
        toolbar.setNavigationOnClickListener(view -> {
            Toast.makeText(this, "Back to Profile Page", Toast.LENGTH_SHORT).show();
            finish();
        });

        //editProfileLayout Click Listener
        editProfileLayout.setOnClickListener(v->{
            Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class));
        });

        //passwordChangeLayout Click Listener
        passwordChangeLayout.setOnClickListener(v->{
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
            //TODO : Navigate to Change Password Page
            startActivity(new Intent(SettingsActivity.this, PasswordChangeActivity.class));
        });

        //appSettingLayout Click Listener
        appSettingLayout.setOnClickListener(v->{
            Toast.makeText(this, "App Setting Coming Soon", Toast.LENGTH_SHORT).show();
            //TODO : Navigate to App Settings Page
        });

        //supportLayout Click Listener
        supportLayout.setOnClickListener(v->{
            Toast.makeText(this, "Help & Support Coming Soon", Toast.LENGTH_SHORT).show();
            //TODO : Navigate to Help & Support Page
        });

        //logoutBtn Click Listener
        logoutBtn.setOnClickListener(v->{
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            logout();
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("SkillConnectPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);
        if (email == null){
            Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
            return;
        }
        AuthApiService authApiService = ApiClient.getClient(this).create(AuthApiService.class);
        authApiService.logout(email).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    SharedPreferences preferences1 = getSharedPreferences("SkillConnectPrefs", MODE_PRIVATE);
                    preferences1.edit().clear().apply();

                    SharedPreferences preferences2 = getSharedPreferences("MY_APP_PREF", MODE_PRIVATE);
                    preferences2.edit().clear().apply();

                    // 4. Clear FCM token
                    FirebaseMessaging.getInstance().deleteToken()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Logout", "FCM token deleted successfully");
                                } else {
                                    Log.e("Logout", "Failed to delete FCM token", task.getException());
                                }
                            });

                    // 5. Navigate to Login screen & clear back stack
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Logout failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Logout Error", "Failed to logout: " + t.getMessage(), t);
            }
        });

    }

    public void init(){
        toolbar = findViewById(R.id.toolbar);
        editProfileLayout = findViewById(R.id.editProfileLayout);
        passwordChangeLayout = findViewById(R.id.passwordChangeLayout);
        appSettingLayout = findViewById(R.id.appSettingLayout);
        supportLayout = findViewById(R.id.supportLayout);
        logoutBtn = findViewById(R.id.logoutBtn);
    }
}