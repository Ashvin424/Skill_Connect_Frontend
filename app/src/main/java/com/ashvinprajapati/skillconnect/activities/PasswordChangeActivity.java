package com.ashvinprajapati.skillconnect.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.ChangePasswordDTO;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordChangeActivity extends AppCompatActivity {
    TextInputEditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    MaterialButton changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_change);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        changePassword.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            if (currentPassword.equals(newPassword)){
                Toast.makeText(this, "New Password and Current Password cannot be same", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPassword.equals(confirmPassword)){
                Toast.makeText(this, "New Password and Confirm Password does not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPassword.length() < 8){
                Toast.makeText(this, "New Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if(currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
            changeNewPassword(currentPassword, newPassword);

        });
    }

    private void changeNewPassword(String currentPassword, String newPassword) {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword(currentPassword);
        dto.setNewPassword(newPassword);
        TokenManager tokenManager = new TokenManager(this);

        UserApiService userApiService = ApiClient.getClient(this).create(UserApiService.class);
        userApiService.changePassword(dto,tokenManager.getToken()).enqueue(new Callback<ChangePasswordDTO>() {
            @Override
            public void onResponse(Call<ChangePasswordDTO> call, Response<ChangePasswordDTO> response) {
                if (response.isSuccessful()){
                    Toast.makeText(PasswordChangeActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(PasswordChangeActivity.this, "Password change failed", Toast.LENGTH_SHORT).show();
                    Log.e("PasswordChangeActivity", "Password change failed: " + response.code() + "" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordDTO> call, Throwable t) {
                Toast.makeText(PasswordChangeActivity.this, "Response failed", Toast.LENGTH_SHORT).show();
                Log.e("PasswordChangeActivity", "Response failed: " + t.getMessage());
            }
        });
    }

    private void init(){
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePassword = findViewById(R.id.changePassword);
    }
}