package com.ashvinprajapati.skillconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.ViewModels.ServiceViewModel;
import com.ashvinprajapati.skillconnect.ViewModels.ServiceViewModelFactory;
import com.ashvinprajapati.skillconnect.adapters.ViewPagerSliderAdapter;
import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.BookingApiService;
import com.ashvinprajapati.skillconnect.networks.ServicesApiService;
import com.ashvinprajapati.skillconnect.repository.ServiceRepository;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView titleTextView, descriptionTextView, usernameTextView, userRatingTextView, categoryTextView, deactivatedTagTV, providerModeTextView;
    private ImageView userProfileImageView, editServiceImageView, deleteServiceImageView;
    private CardView serviceProviderCardView;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager2 viewPager2;
    private MaterialButton bookServiceButton;

    private ServiceViewModel serviceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        Long serviceId = getIntent().getLongExtra("serviceId", -1L);
        Long userId = getIntent().getLongExtra("userId", -1L);

        // Set up View Model
        ServicesApiService servicesApiService = ApiClient.getClient(this).create(ServicesApiService.class);
        BookingApiService bookingApiService = ApiClient.getClient(this).create(BookingApiService.class);
        ServiceRepository repository = new ServiceRepository(bookingApiService, servicesApiService);
        serviceViewModel = new ViewModelProvider(this, new ServiceViewModelFactory(repository)).get(ServiceViewModel.class);

        // Observe Service Details
        serviceViewModel.getService().observe(this, service -> {
            if (service != null){
                bindServiceDetails(service);
            }
            else {
                Toast.makeText(this, "Failed to load service details", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe Booking Status
        serviceViewModel.getBookingStatus().observe(this, status ->{
            if (status){
                Toast.makeText(this, "Booking Successful", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Booking Failed or You Already Booked This Service", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch Data
        if (serviceId != -1L){
            serviceViewModel.loadService(serviceId);
        }

        // Click Listeners
        bookServiceButton.setOnClickListener(v -> serviceViewModel.bookService(userId, serviceId));
        toolbar.setNavigationOnClickListener(v -> finish());

        serviceProviderCardView.setOnClickListener(v -> gotoOtherUserProfile());
        editServiceImageView.setOnClickListener(v -> gotoEditService());
        deleteServiceImageView.setOnClickListener(v -> deleteService(serviceId));
    }

    private void deleteService(Long serviceId) {
        TokenManager tokenManager = new TokenManager(this);
        String token = "Bearer " + tokenManager.getToken();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ServiceDetailActivity.this);
        alertDialog.setTitle("Success")
                .setMessage("Profile Updated Successfully")
                .setPositiveButton("OK", (dialog, which) -> {
                    serviceViewModel.deleteService(serviceId, token);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
//        finish();
    }


    private void gotoEditService() {
        Long serviceId = getIntent().getLongExtra("serviceId", -1L);
        Toast.makeText(this, ""+serviceId, Toast.LENGTH_SHORT).show();
        Long userId = Long.parseLong(getCurrentUserId());

        startActivity(
                new Intent(ServiceDetailActivity.this, EditProfileActivity.class)
                        .putExtra("serviceId",serviceId)
                        .putExtra("userId", userId)
        );
    }

    private void gotoOtherUserProfile() {
        long userId = getIntent().getLongExtra("userId", -1L);

        if (userId == -1L) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, OtherUserProfileActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void bindServiceDetails(Service service) {
        titleTextView.setText(service.getTitle());
        descriptionTextView.setText(service.getDescription());
        usernameTextView.setText(service.getUsername());
        userRatingTextView.setText(String.valueOf(service.getUserRating()));
        categoryTextView.setText("Category: " + service.getCategory());
        providerModeTextView.setText("Mode: "+service.getProviderMode());

        if(getCurrentUserId() != null && getCurrentUserId().equals(service.getUserId().toString())){
            editServiceImageView.setVisibility(View.VISIBLE);
            deleteServiceImageView.setVisibility(View.VISIBLE);
            bookServiceButton.setVisibility(View.GONE);
        }
        if (!Boolean.TRUE.equals(service.getActive())){
            deactivatedTagTV.setVisibility(View.VISIBLE);
            editServiceImageView.setVisibility(View.GONE);
            deleteServiceImageView.setVisibility(View.GONE);
        }
        editServiceImageView.setOnClickListener(v -> startActivity(new Intent(ServiceDetailActivity.this, EditServiceActivity.class).putExtra("serviceId", service.getId())));

        Glide.with(userProfileImageView)
                .load(service.getUserProfileImageUrl())
                .placeholder(R.drawable.icon_profile)
                .error(R.drawable.icon_help)
                .into(userProfileImageView);

        ViewPagerSliderAdapter adapter = new ViewPagerSliderAdapter(service.getImageUrls());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {}).attach();

    }


    public void init(){
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        userProfileImageView = findViewById(R.id.userProfileImageView);
        categoryTextView = findViewById(R.id.categoryTextView);
        userRatingTextView = findViewById(R.id.providerRatingTextView);
        viewPager2 = findViewById(R.id.viewPagerSlider);
        bookServiceButton = findViewById(R.id.bookServiceButton);
        tabLayout = findViewById(R.id.tabLayoutDots);
        editServiceImageView = findViewById(R.id.editService);
        deleteServiceImageView = findViewById(R.id.deleteService);
        deactivatedTagTV = findViewById(R.id.deactivatedTagTV);
        serviceProviderCardView = findViewById(R.id.serviceProviderCardView);
        providerModeTextView = findViewById(R.id.providerModeTextView);
        toolbar = findViewById(R.id.toolbar);
    }
    private String getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }
}