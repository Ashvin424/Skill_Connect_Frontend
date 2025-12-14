package com.ashvinprajapati.skillconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.RegisterRequest;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.AuthApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText fullNameEditText, usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText, locationEditText, skillsEditText;
    private RadioGroup serviceModeRadioGroup;
    private MaterialButton signUpBtn;
    private TextView loginTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);


        init();
        loginTextView.setOnClickListener(v->{
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        signUpBtn.setOnClickListener(v->{
            String fullName = fullNameEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String skills = skillsEditText.getText().toString();
            int selectedServiceModeId = serviceModeRadioGroup.getCheckedRadioButtonId();
            if(selectedServiceModeId == -1){
                Toast.makeText(this, "Please select a service mode", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedServiceMode = findViewById(selectedServiceModeId);
            String serviceMode = selectedServiceMode.getText().toString();
            signUp(fullName, username, email, password, confirmPassword, location, skills, serviceMode);
        });
    }

    private void signUp(String fullName, String username, String email, String password, String confirmPassword, String location, String skills, String serviceMode) {
        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setName(fullName);
        request.setPassword(password);
        request.setUsername(username);
        request.setSkills(skills);
        request.setLocation(location);
        request.setServiceMode(serviceMode);

        AuthApiService authApi = ApiClient.getClient(this).create(AuthApiService.class);
        authApi.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration successful. Please login.", Toast.LENGTH_SHORT).show();
                    // Navigate to Login screen
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void init(){
        fullNameEditText = findViewById(R.id.fullNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        locationEditText = findViewById(R.id.locationEditText);
        skillsEditText = findViewById(R.id.skillsEditText);
        serviceModeRadioGroup = findViewById(R.id.serviceModeRadioGroup);
        signUpBtn = findViewById(R.id.signUpBtn);
        loginTextView = findViewById(R.id.loginTextView);
    }
}