package com.ashvinprajapati.skillconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.RegisterRequest;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.AuthApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    // Step layouts
    private View stepOneLayout, stepTwoLayout;

    // Step 1 fields
    private TextInputEditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;

    // Step 2 fields
    private TextInputEditText usernameEditText, locationEditText, skillsEditText;
    private RadioGroup serviceModeRadioGroup;

    // Buttons
    private MaterialButton nextBtn, signUpBtn;
    private TextView loginTextView;
    private ProgressBar progressBar;

    // Temp stored data
    private String fullName, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        init();
        setupListeners();
    }

    private void setupListeners() {

        // STEP 1 → STEP 2
        nextBtn.setOnClickListener(v -> {
            if (validateStepOne()) {
                saveStepOneData();
                showStepTwo();
            }
        });

        // FINAL SUBMIT
        signUpBtn.setOnClickListener(v -> {
            if (validateStepTwo()) {
                submitRegistration();
            }
        });

        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // ---------------- STEP 1 ----------------

    private boolean validateStepOne() {
        if (fullNameEditText.getText().toString().isEmpty()) {
            fullNameEditText.setError("Required");
            return false;
        }
        if (emailEditText.getText().toString().isEmpty()) {
            emailEditText.setError("Required");
            return false;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordEditText.setError("Required");
            return false;
        }
        if (!passwordEditText.getText().toString()
                .equals(confirmPasswordEditText.getText().toString())) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void saveStepOneData() {
        fullName = fullNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
    }

    private void showStepTwo() {
        stepOneLayout.setVisibility(View.GONE);
        stepTwoLayout.setVisibility(View.VISIBLE);
        progressBar.setProgress(100);
    }

    // ---------------- STEP 2 ----------------

    private boolean validateStepTwo() {
        if (usernameEditText.getText().toString().isEmpty()) {
            usernameEditText.setError("Required");
            return false;
        }
        if (skillsEditText.getText().toString().isEmpty()) {
            skillsEditText.setError("Required");
            return false;
        }
        if (serviceModeRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select service mode", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submitRegistration() {

        int selectedId = serviceModeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedId);

        RegisterRequest request = new RegisterRequest();
        request.setName(fullName);
        request.setEmail(email);
        request.setPassword(password);
        request.setUsername(usernameEditText.getText().toString());
        request.setLocation(locationEditText.getText().toString());
        request.setSkills(skillsEditText.getText().toString());
        request.setServiceMode(selectedRadio.getText().toString());

        AuthApiService api = ApiClient.getClient(this).create(AuthApiService.class);
        api.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this,
                            "Registration successful. Please login.",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this,
                            "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignUpActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------------- INIT ----------------

    private void init() {
        stepOneLayout = findViewById(R.id.stepOneLayout);
        stepTwoLayout = findViewById(R.id.stepTwoLayout);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        usernameEditText = findViewById(R.id.usernameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        skillsEditText = findViewById(R.id.skillsEditText);
        serviceModeRadioGroup = findViewById(R.id.serviceModeRadioGroup);

        nextBtn = findViewById(R.id.nextbtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        loginTextView = findViewById(R.id.loginTextView);
        progressBar = findViewById(R.id.progressBar);
    }

    // ---------------- BACK HANDLING ----------------

    @Override
    public void onBackPressed() {
        if (stepTwoLayout.getVisibility() == View.VISIBLE) {
            stepTwoLayout.setVisibility(View.GONE);
            stepOneLayout.setVisibility(View.VISIBLE);
            progressBar.setProgress(50);
        } else {
            super.onBackPressed();
        }
    }
}
