package com.ashvinprajapati.skillconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.ashvinprajapati.skillconnect.models.Booking;
import com.ashvinprajapati.skillconnect.models.BookingRequest;
import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.BookingApiService;
import com.ashvinprajapati.skillconnect.networks.ServicesApiService;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.repository.ServiceRepository;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView titleTextView, descriptionTextView, usernameTextView, userRatingTextView, categoryTextView;
    private ImageView userProfileImageView, editServiceImageView;
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

//        if (serviceId != -1L){
//            ServicesApiService servicesApiService = ApiClient.getClient(this).create(ServicesApiService.class);
//            servicesApiService.getServiceById(serviceId).enqueue(new Callback<Service>() {
//                @Override
//                public void onResponse(Call<Service> call, Response<Service> response) {
//                    if (response.isSuccessful() && response.body() != null){
//                        Service service = response.body();
//                        if (service.getUserId() == Long.parseLong(getCurrentUserId())){
//                            editServiceImageView.setVisibility(View.VISIBLE);
//                        }
//                        bindServiceDetails(service);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Service> call, Throwable t) {
//                    Toast.makeText(ServiceDetailActivity.this, "Failed to load service details", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

        // OnClick Listeners
        serviceProviderCardView.setOnClickListener(v -> gotoOtherUserProfile());
        editServiceImageView.setOnClickListener(v -> gotoEditService());
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
        Long userId = getIntent().getLongExtra("userId", -1L);
        Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();
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

//    private void bookService() {
//        Long serviceId = getIntent().getLongExtra("serviceId", -1L);
//        Long userId = getIntent().getLongExtra("userId", -1L);
//        Log.d("BookingRequest", "userId: " + userId + ", serviceId: " + serviceId);
//        BookingApiService bookingApiService = ApiClient.getClient(this).create(BookingApiService.class);
//        bookingApiService.createBooking(new BookingRequest(userId,serviceId)).enqueue(new Callback<Booking>() {
//            @Override
//            public void onResponse(Call<Booking> call, Response<Booking> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(ServiceDetailActivity.this, "Booking Successful", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        String errorBody = response.errorBody().string();
//                        Toast.makeText(ServiceDetailActivity.this, errorBody, Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Toast.makeText(ServiceDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Booking> call, Throwable t) {
//                Toast.makeText(ServiceDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.d("DEBUG", "Error: " + t.getMessage());
//                Log.e("API_BOOKING_FAILURE", "Error:", t);
//
//            }
//        });
//    }

    public void init(){
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        userProfileImageView = findViewById(R.id.userProfileImageView);
        categoryTextView = findViewById(R.id.categoryTextView);
        userRatingTextView = findViewById(R.id.userRatingTextView);
        viewPager2 = findViewById(R.id.viewPagerSlider);
        bookServiceButton = findViewById(R.id.bookServiceButton);
        tabLayout = findViewById(R.id.tabLayoutDots);
        editServiceImageView = findViewById(R.id.editService);
        serviceProviderCardView = findViewById(R.id.serviceProviderCardView);
        toolbar = findViewById(R.id.toolbar);
    }
    private String getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }
}