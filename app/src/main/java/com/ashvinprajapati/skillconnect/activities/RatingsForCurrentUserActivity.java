package com.ashvinprajapati.skillconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.adapters.UserRatingAdapter;
import com.ashvinprajapati.skillconnect.models.PagedResponse;
import com.ashvinprajapati.skillconnect.models.Rating;
import com.ashvinprajapati.skillconnect.models.RatingResponse;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.RatingApiService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingsForCurrentUserActivity extends AppCompatActivity {
    private RecyclerView ratingsRV;
    private UserRatingAdapter userRatingAdapter;
    private MaterialToolbar toolbar;
    LinearLayout emptyStateLayout;
    private List<RatingResponse> ratingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ratings_for_current_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        ratingsRV = findViewById(R.id.ratingsRV);
        ratingsRV.setLayoutManager(new LinearLayoutManager(this));
        userRatingAdapter = new UserRatingAdapter(ratingsList);
        toolbar = findViewById(R.id.toolbar);
        ratingsRV.setAdapter(userRatingAdapter);
        toolbar.setNavigationOnClickListener(v -> finish());

        getRatings();

    }

    private void getRatings() {
        Long currentUserId = Long.parseLong(getCurrentUserId());
        Toast.makeText(this, "UID : " + currentUserId, Toast.LENGTH_SHORT).show();

        RatingApiService ratingApiService = ApiClient.getClient(this).create(RatingApiService.class);
        ratingApiService.getRatingForUser(currentUserId, 0, 10).enqueue(new Callback<PagedResponse<RatingResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<RatingResponse>> call, Response<PagedResponse<RatingResponse>> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<RatingResponse> ratings = response.body().getContent();
                    userRatingAdapter.updateData(ratings);
                    Log.d("RatingActivity", "Ratings fetched successfully" + ratings);
                }else {
                    Toast.makeText(RatingsForCurrentUserActivity.this, "No Ratings Found", Toast.LENGTH_SHORT).show();
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    ratingsRV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<RatingResponse>> call, Throwable t) {
                Log.e("RatingsActivity", "Error fetching ratings", t);
                AlertDialog.Builder builder = new AlertDialog.Builder(RatingsForCurrentUserActivity.this);
                builder.setTitle("Error")
                        .setMessage("Failed to fetch ratings")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }
}