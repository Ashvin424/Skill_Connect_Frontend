package com.ashvinprajapati.skillconnect.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.ProfileImageUploadResponseDTO;
import com.ashvinprajapati.skillconnect.models.ProfileResponse;
import com.ashvinprajapati.skillconnect.models.UpdateUserDTO;
import com.ashvinprajapati.skillconnect.models.User;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.ashvinprajapati.skillconnect.utils.UriToFileUtil;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView userProfileImageView;
    private TextInputEditText fullNameEditText, usernameEditText, bioEditText, locationEditText, skillsEditText;
    private RadioGroup serviceModeRadioGroup;
    private MaterialButton updateProfileBtn;

    private Uri imageUri;
    private static final int IMAGE_PICK_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        getUserProfile();
        userProfileImageView.setOnClickListener(v -> updateProfileImage());

    }



    private void updateProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Set the type of files you want to allow
        intent.setAction(Intent.ACTION_GET_CONTENT);    // Set the action to GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Images"), IMAGE_PICK_CODE);

        updateProfileBtn.setOnClickListener(v -> uploadProfileImage());
    }

    private void uploadProfileImage() {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setName(fullNameEditText.getText().toString());
        updateUserDTO.setUsername(usernameEditText.getText().toString());
        updateUserDTO.setBio(bioEditText.getText().toString());
        updateUserDTO.setLocation(locationEditText.getText().toString());
        updateUserDTO.setSkills(skillsEditText.getText().toString());
        updateUserDTO.setProfileImageUrl(userProfileImageView.getTag().toString());

        String serviceMode = "";
        int selectedRadioButtonId = serviceModeRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedServiceMode = findViewById(selectedRadioButtonId);
            serviceMode = selectedServiceMode.getText().toString();
        }
        updateUserDTO.setServiceMode(serviceMode);
        Uri uri = Uri.parse(updateUserDTO.getProfileImageUrl());

        File file = UriToFileUtil.getFileFromUri(this, uri);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        TokenManager tokenManager = new TokenManager(this);


        //TODO : Upload profile image and update profile
        UserApiService userApiService = ApiClient.getClient(this).create(UserApiService.class);
        userApiService.uploadProfileImage(part, "Bearer "+tokenManager.getToken()).enqueue(new Callback<ProfileImageUploadResponseDTO>() {
            @Override
            public void onResponse(Call<ProfileImageUploadResponseDTO> call, Response<ProfileImageUploadResponseDTO> response) {
                if (response.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "Image Upload Successfully",Toast.LENGTH_SHORT).show();
                    ProfileImageUploadResponseDTO profileImageUploadResponseDTO = response.body();
                    updateUserDTO.setProfileImageUrl(profileImageUploadResponseDTO.getProfileImageUrl());
                    updateProfile(updateUserDTO, tokenManager.getToken());
                }
                else {
                    if (response.code() == 413) {
                        // Check for 413 "Payload Too Large"
                        Toast.makeText(EditProfileActivity.this, "Image file too large. Please select image smaller than 1MB.", Toast.LENGTH_LONG).show();
                    } else {
                        // Handle any other HTTP error
                        Toast.makeText(EditProfileActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileImageUploadResponseDTO> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });

    }

    private void updateProfile(UpdateUserDTO updateUserDTO, String token) {
        UserApiService userApiService = ApiClient.getClient(this).create(UserApiService.class);
        userApiService.userProfileUpdate(updateUserDTO, "Bearer "+token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProfileActivity.this);
                    alertDialog.setTitle("Success")
                            .setMessage("Profile Updated Successfully")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            });
                }
                else {
                    Toast.makeText(EditProfileActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Error", t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK){
            if (data.getData() != null){
                Glide.with(userProfileImageView)
                        .load(data.getData())
                        .placeholder(R.drawable.icon_profile)
                        .error(R.drawable.icon_help)
                        .into(userProfileImageView);

                imageUri = data.getData();
                userProfileImageView.setTag(imageUri);
            }
        }
    }

    private void getUserProfile() {
        UserApiService userApiService = ApiClient.getClient(this).create(UserApiService.class);
        userApiService.getMyProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()){
                    ProfileResponse profileResponse = response.body();
                    if (profileResponse != null){
                        fullNameEditText.setText(profileResponse.getName());
                        usernameEditText.setText(profileResponse.getDisplayUsername());
                        bioEditText.setText(profileResponse.getBio());
                        locationEditText.setText(profileResponse.getLocation());
                        skillsEditText.setText(profileResponse.getSkills());
                        Glide.with(userProfileImageView)
                                .load(profileResponse.getProfileImageUrl())
                                .placeholder(R.drawable.icon_profile)
                                .error(R.drawable.icon_help)
                                .into(userProfileImageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("Error", t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void init(){
        userProfileImageView = findViewById(R.id.userProfileImageView);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        bioEditText = findViewById(R.id.bioEditText);
        locationEditText = findViewById(R.id.locationEditText);
        skillsEditText = findViewById(R.id.skillsEditText);
        serviceModeRadioGroup = findViewById(R.id.serviceModeRadioGroup);
        updateProfileBtn = findViewById(R.id.updateProfile);
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }
}