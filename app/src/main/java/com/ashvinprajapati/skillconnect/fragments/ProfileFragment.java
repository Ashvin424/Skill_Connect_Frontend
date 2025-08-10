package com.ashvinprajapati.skillconnect.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

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
import com.ashvinprajapati.skillconnect.activities.LoginActivity;
import com.ashvinprajapati.skillconnect.activities.MainActivity;
import com.ashvinprajapati.skillconnect.activities.ServiceDetailActivity;
import com.ashvinprajapati.skillconnect.adapters.ProfileServiceAdapter;
import com.ashvinprajapati.skillconnect.models.ProfileResponse;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private TextView fullNameTextView, usernameTextView, userCreatedAtTextView,skillCountTextView, serviceCountTextView, reviewCountTextView, bioTextView, userRatingTextView;
    private CircleImageView userProfileImageView;
    private ChipGroup chipGroupSkills;
    private RecyclerView recyclerViewServices;
    private Button logoutButton;
    private ProgressBar progressBar;
    private ProfileServiceAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        userCreatedAtTextView = view.findViewById(R.id.userCreatedAtTextView);
        userProfileImageView = view.findViewById(R.id.userProfileImageView);
        chipGroupSkills = view.findViewById(R.id.chipGroupSkills);
        recyclerViewServices = view.findViewById(R.id.recyclerViewServices);
        skillCountTextView = view.findViewById(R.id.skillCountTextView);
        serviceCountTextView = view.findViewById(R.id.serviceCountTextView);
        logoutButton = view.findViewById(R.id.btnLogout);
        reviewCountTextView = view.findViewById(R.id.reviewCountTextView);
        bioTextView = view.findViewById(R.id.bioTextView);
        userRatingTextView = view.findViewById(R.id.userRatingTextView);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerViewServices.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadProfile();
        return view;
    }


    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);
        TokenManager tokenManager = new TokenManager(requireContext());
        UserApiService userApiService = ApiClient.getClient(requireContext()).create(UserApiService.class);
        Log.d("TOKEN", "Token in Profile "+tokenManager.getToken());

        userApiService.getMyProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profileResponse = response.body();
                    fullNameTextView.setText(profileResponse.getName());
                    usernameTextView.setText(profileResponse.getDisplayUsername());
                    Log.d("ProfileFragment", "Services count: " + profileResponse.getServices().size());
                    Log.d("ProfileFragment", "Display Username: " + (profileResponse.getDisplayUsername() != null ? profileResponse.getDisplayUsername() : "null"));
                    if (profileResponse.getCreatedAt() != null) {
                        userCreatedAtTextView.setText("Joined " + profileResponse.getCreatedAt());
                    } else {
                        userCreatedAtTextView.setText("Joined 20XX");
                    }
                    skillCountTextView.setText(String.valueOf(profileResponse.getSkillCount()));
                    serviceCountTextView.setText(String.valueOf(profileResponse.getServiceCount()));
                    reviewCountTextView.setText(String.valueOf(profileResponse.getReviewCount()));
                    bioTextView.setText(profileResponse.getBio());
                    userRatingTextView.setText(String.valueOf(profileResponse.getAverageRating()));
                    String skills = profileResponse.getSkills();
                    String[] skillArray = skills.split(",");
                    chipGroupSkills.removeAllViews();
                    for (String skill : skillArray) {
                        Chip chip = new Chip(requireContext());
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
                        Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
                        intent.putExtra("serviceId", service.getId());
                        startActivity(intent);
                    });
                    recyclerViewServices.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("ProfileFragment", "Failed to load profile: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Failed To Load Profile", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}