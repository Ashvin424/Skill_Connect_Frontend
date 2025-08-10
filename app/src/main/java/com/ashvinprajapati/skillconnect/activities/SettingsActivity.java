package com.ashvinprajapati.skillconnect.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ashvinprajapati.skillconnect.R;
import com.google.android.material.button.MaterialButton;

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
            //TODO : Navigate to Profile Page
        });

        //editProfileLayout Click Listener
        editProfileLayout.setOnClickListener(v->{
            Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
            //TODO : Navigate to Edit Profile Page
        });

        //passwordChangeLayout Click Listener
        passwordChangeLayout.setOnClickListener(v->{
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
            //TODO : Navigate to Change Password Page
        });

        //appSettingLayout Click Listener
        appSettingLayout.setOnClickListener(v->{
            Toast.makeText(this, "App Settings", Toast.LENGTH_SHORT).show();
            //TODO : Navigate to App Settings Page
        });

        //supportLayout Click Listener
        supportLayout.setOnClickListener(v->{
            Toast.makeText(this, "Help & Support", Toast.LENGTH_SHORT).show();
            //TODO : Navigate to Help & Support Page
        });

        //logoutBtn Click Listener
        logoutBtn.setOnClickListener(v->{
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            //TODO : Navigate to Login Page
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