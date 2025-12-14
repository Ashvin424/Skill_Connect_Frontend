package com.ashvinprajapati.skillconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.models.UpdateServiceDTO;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.ServicesApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditServiceActivity extends AppCompatActivity {
    private TextInputEditText serviceTitleEditText, categoryTitleEditText, serviceDescriptionEditText;
    private MaterialButton updateServiceBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_service);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        Intent intent = getIntent();
        Toast.makeText(this, intent.getLongExtra("userId", -1L)+" | "+intent.getLongExtra("serviceId", -1L), Toast.LENGTH_SHORT).show();
        getExistingService();
        updateServiceBtn.setOnClickListener(v -> updateService());
    }

    private void updateService() {
        Long serviceId = getIntent().getLongExtra("serviceId", -1L);
        String title = serviceTitleEditText.getText().toString();
        String category = categoryTitleEditText.getText().toString();
        String description = serviceDescriptionEditText.getText().toString();

        TokenManager tokenManager = new TokenManager(this);
        UpdateServiceDTO updateServiceDTO = new UpdateServiceDTO(category, description, title);
        ServicesApiService servicesApiService = ApiClient.getClient(this).create(ServicesApiService.class);
        servicesApiService.updateService(serviceId, updateServiceDTO, tokenManager.getToken()).enqueue(new Callback<UpdateServiceDTO>() {
            @Override
            public void onResponse(Call<UpdateServiceDTO> call, Response<UpdateServiceDTO> response) {
                if (response.isSuccessful()){
                    Toast.makeText(EditServiceActivity.this, "Service Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();

                }else {
                    Toast.makeText(EditServiceActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateServiceDTO> call, Throwable t) {
                Log.d("TAG", "onFailure: "+t.getMessage());
                Toast.makeText(EditServiceActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getExistingService() {
        Long serviceId = getIntent().getLongExtra("serviceId", -1L);
        ServicesApiService servicesApiService = ApiClient.getClient(this).create(ServicesApiService.class);
        servicesApiService.getServiceById(serviceId).enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()){
                    Service service = response.body();
                    if (service != null){
                        Toast.makeText(EditServiceActivity.this, service.getTitle(), Toast.LENGTH_SHORT).show();
                        // Populate the EditText fields with existing service details
                        serviceTitleEditText.setText(service.getTitle());
                        categoryTitleEditText.setText(service.getCategory());
                        serviceDescriptionEditText.setText(service.getDescription());
                    }
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {

            }
        });
    }
    private void init() {
        serviceTitleEditText = findViewById(R.id.serviceTitleEditText);
        categoryTitleEditText = findViewById(R.id.categoryTitleEditText);
        serviceDescriptionEditText = findViewById(R.id.serviceDescriptionEditText);
        updateServiceBtn = findViewById(R.id.updateServiceBtn);
    }
}