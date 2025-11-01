package com.ashvinprajapati.skillconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.activities.BookingsActivity;
import com.ashvinprajapati.skillconnect.activities.RatingsForCurrentUserActivity;
import com.ashvinprajapati.skillconnect.activities.ServiceDetailActivity;
import com.ashvinprajapati.skillconnect.activities.SettingsActivity;
import com.ashvinprajapati.skillconnect.adapters.ProfileServiceAdapter;
import com.ashvinprajapati.skillconnect.models.ProfileResponse;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.LocalDateTime;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView fullNameTextView, usernameTextView, userCreatedAtTextView, skillCountTextView, serviceCountTextView, reviewCountTextView, bioTextView, userRatingTextView, bookingsTextview;
    private CircleImageView userProfileImageView;
    private ChipGroup chipGroupSkills;
    private CardView ratingsCardView;
    private RecyclerView recyclerViewServices;
    private ProgressBar progressBar;
    private ProfileServiceAdapter adapter;
    private View noInternetLayout;
    private Button btnRetry;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        userCreatedAtTextView = view.findViewById(R.id.userCreatedAtTextView);
        userProfileImageView = view.findViewById(R.id.userProfileImageView);
        chipGroupSkills = view.findViewById(R.id.chipGroupSkills);
        recyclerViewServices = view.findViewById(R.id.recyclerViewServices);
        skillCountTextView = view.findViewById(R.id.skillCountTextView);
        serviceCountTextView = view.findViewById(R.id.serviceCountTextView);
        bookingsTextview = view.findViewById(R.id.bookingsTextview);
        ratingsCardView = view.findViewById(R.id.ratingsCardView);
        reviewCountTextView = view.findViewById(R.id.reviewCountTextView);
        bioTextView = view.findViewById(R.id.bioTextView);
        userRatingTextView = view.findViewById(R.id.userRatingTextView);
        progressBar = view.findViewById(R.id.progressBar);
        noInternetLayout = view.findViewById(R.id.noInternetLayout);
        btnRetry = view.findViewById(R.id.btnRetry);

        recyclerViewServices.setLayoutManager(new LinearLayoutManager(requireContext()));

        bookingsTextview.setOnClickListener(v -> startActivity(new Intent(getActivity(), BookingsActivity.class)));
        ratingsCardView.setOnClickListener(v -> startActivity(new Intent(requireContext(), RatingsForCurrentUserActivity.class)));
        view.findViewById(R.id.settingBtn).setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));

        btnRetry.setOnClickListener(v -> loadProfile());

        loadProfile();
        return view;
    }

    private void loadProfile() {
        showLoader();

        TokenManager tokenManager = new TokenManager(requireContext());
        UserApiService userApiService = ApiClient.getClient(requireContext()).create(UserApiService.class);

        userApiService.getMyProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (!isAdded()) return;

                hideLoader();

                if (response.isSuccessful() && response.body() != null) {
                    showProfileUI();
                    ProfileResponse profile = response.body();

                    fullNameTextView.setText(profile.getName());
                    usernameTextView.setText(profile.getDisplayUsername());

                    if (profile.getCreatedAt() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        userCreatedAtTextView.setText("Joined in " + LocalDateTime.parse(profile.getCreatedAt()).getYear());
                    }

                    skillCountTextView.setText(String.valueOf(profile.getSkillCount()));
                    serviceCountTextView.setText(String.valueOf(profile.getServiceCount()));
                    reviewCountTextView.setText("(" + profile.getReviewCount() + ")");
                    bioTextView.setText(profile.getBio());
                    userRatingTextView.setText(String.valueOf(profile.getAverageRating()));

                    chipGroupSkills.removeAllViews();
                    for (String skill : profile.getSkills().split(",")) {
                        Chip chip = new Chip(requireContext());
                        chip.setText(skill.trim());
                        chip.setChipBackgroundColorResource(R.color.teal_200);
                        chip.setTextColor(Color.BLACK);
                        chip.setClickable(false);
                        chip.setCheckable(false);
                        chipGroupSkills.addView(chip);
                    }

                    Glide.with(userProfileImageView)
                            .load(profile.getProfileImageUrl())
                            .placeholder(R.drawable.icon_profile)
                            .error(R.drawable.icon_help)
                            .into(userProfileImageView);

                    adapter = new ProfileServiceAdapter(profile.getServices(), service -> {
                        Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
                        intent.putExtra("serviceId", service.getId());
                        intent.putExtra("userId", Long.parseLong(getCurrentUserId()));
                        startActivity(intent);
                    });

                    recyclerViewServices.setAdapter(adapter);

                } else {
                    showNoInternet();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("ProfileFragment", "Error: " + t.getMessage());
                showNoInternet();
            }
        });
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }

    private void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
    }

    private void hideLoader() {
        progressBar.setVisibility(View.GONE);
    }

    private void showNoInternet() {
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    private void showProfileUI() {
        noInternetLayout.setVisibility(View.GONE);
    }
}
