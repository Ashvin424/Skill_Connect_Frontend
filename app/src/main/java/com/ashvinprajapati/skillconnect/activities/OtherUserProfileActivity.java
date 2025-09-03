package com.ashvinprajapati.skillconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.adapters.ProfileServiceAdapter;
import com.ashvinprajapati.skillconnect.models.ProfileResponse;
import com.ashvinprajapati.skillconnect.models.Rating;
import com.ashvinprajapati.skillconnect.models.RatingRequest;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.RatingApiService;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.time.LocalDateTime;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherUserProfileActivity extends AppCompatActivity {
    private TextView fullNameTextView, usernameTextView, userCreatedAtTextView,skillCountTextView, serviceCountTextView, reviewCountTextView, bioTextView, userRatingTextView;
    private CircleImageView userProfileImageView;
    private ChipGroup chipGroupSkills;
    private TextInputEditText commentEditText;
    private Spinner ratingSpinner;
    private MaterialButton rateBtn;
    private Toolbar toolbar;
    private RecyclerView recyclerViewServices;
    private LinearLayout ratingLayout;
    private ProgressBar progressBar;
    private ProfileServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        loadProfile();
        toolbar.setNavigationOnClickListener(v -> finish());
        String[] ratings = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ratings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(adapter);
        Long revieweeId = getIntent().getLongExtra("userId", -1L);
        rateBtn.setOnClickListener(v -> rateUser(ratingSpinner.getSelectedItem().toString(), revieweeId, getCurrentUserId()));

    }

    private void rateUser(String rating, Long revieweeId, String currentUserId) {

        String comment = commentEditText.getText().toString().isEmpty() || commentEditText.getText().toString().isBlank() ? "No Comment" : commentEditText.getText().toString();
        RatingRequest request = new RatingRequest();
        request.setRevieweeId(revieweeId);
        request.setReviewerId(Long.parseLong(currentUserId));
        request.setRatingValue(Integer.parseInt(rating));
        request.setComment(comment);

        RatingApiService ratingApiService = ApiClient.getClient(this).create(RatingApiService.class);
        ratingApiService.createRating(request).enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                if (response.isSuccessful()){
                    Toast.makeText(OtherUserProfileActivity.this, "Rating Successful", Toast.LENGTH_SHORT).show();
                    loadProfile();
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(OtherUserProfileActivity.this, errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(OtherUserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                Log.e("ProfileFragment", "Failed to load profile: " + t.getMessage(), t);
                Toast.makeText(OtherUserProfileActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void loadProfile() {
        Long currentUserId = Long.parseLong(getCurrentUserId());
        Long userId = getIntent().getLongExtra("userId", -1L);
        if (currentUserId.equals(userId)){
            ratingLayout.setVisibility(View.GONE);
        }
        Toast.makeText(this, "User ID: "+userId, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        TokenManager tokenManager = new TokenManager(this);
        UserApiService userApiService = ApiClient.getClient(this).create(UserApiService.class);
        Log.d("TOKEN", "Token in Profile "+tokenManager.getToken());

        userApiService.getUserById(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profileResponse = response.body();
                    fullNameTextView.setText(profileResponse.getName());
                    usernameTextView.setText(profileResponse.getDisplayUsername());
                    if (profileResponse.getCreatedAt() != null) {
                        String date = profileResponse.getCreatedAt();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            userCreatedAtTextView.setText("Joined in " + String.valueOf(LocalDateTime.parse(date).getYear()));
                        }else {
                            userCreatedAtTextView.setText(profileResponse.getCreatedAt());
                        }

                    } else {
                        userCreatedAtTextView.setText("Joined 20XX");
                    }
                    skillCountTextView.setText(String.valueOf(profileResponse.getSkillCount()));
                    serviceCountTextView.setText(String.valueOf(profileResponse.getServiceCount()));
                    reviewCountTextView.setText("("+String.valueOf(profileResponse.getReviewCount())+")");
                    bioTextView.setText(profileResponse.getBio());
                    userRatingTextView.setText(String.valueOf(profileResponse.getAverageRating()));
                    String skills = profileResponse.getSkills();
                    String[] skillArray = skills.split(",");
                    chipGroupSkills.removeAllViews();
                    for (String skill : skillArray) {
                        Chip chip = new Chip(OtherUserProfileActivity.this);
                        chip.setText(skill.trim());
                        chip.setChipBackgroundColorResource(R.color.teal_200);  // Optional: set background color
                        chip.setTextColor(Color.BLACK);  // Optional: set text color
                        chip.setClickable(false);
                        chip.setCheckable(false);
                        chipGroupSkills.addView(chip);
                    }
                    Glide.with(userProfileImageView)
                            .load(profileResponse.getProfileImageUrl())
                            .placeholder(R.drawable.icon_profile)
                            .error(R.drawable.icon_help)
                            .into(userProfileImageView);

                    adapter = new ProfileServiceAdapter(profileResponse.getServices(), service -> {
                        Intent intent = new Intent(OtherUserProfileActivity.this, ServiceDetailActivity.class);
                        intent.putExtra("serviceId", service.getId());
                        startActivity(intent);
                    });
                    recyclerViewServices.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("ProfileFragment", "Failed to load profile: " + t.getMessage(), t);
                Toast.makeText(OtherUserProfileActivity.this, "Failed To Load Profile", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    public void init(){
        fullNameTextView = findViewById(R.id.fullNameTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        userCreatedAtTextView = findViewById(R.id.userCreatedAtTextView);
        userProfileImageView = findViewById(R.id.userProfileImageView);
        chipGroupSkills = findViewById(R.id.chipGroupSkills);
        recyclerViewServices = findViewById(R.id.recyclerViewServices);
        skillCountTextView = findViewById(R.id.skillCountTextView);
        serviceCountTextView = findViewById(R.id.serviceCountTextView);
        reviewCountTextView = findViewById(R.id.reviewCountTextView);
        ratingLayout = findViewById(R.id.ratingLayout);
        bioTextView = findViewById(R.id.bioTextView);
        userRatingTextView = findViewById(R.id.userRatingTextView);
        commentEditText = findViewById(R.id.commentEditText);
        ratingSpinner = findViewById(R.id.ratingSpinner);
        toolbar = findViewById(R.id.toolbar);
        rateBtn = findViewById(R.id.rateBtn);
        progressBar = findViewById(R.id.progressBar);

        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));
    }
    private String getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }
}