package com.ashvinprajapati.skillconnect.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.activities.ServiceDetailActivity;
import com.ashvinprajapati.skillconnect.adapters.ServiceAdapter;
import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.ServicesApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment {
    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private ProgressBar progressBar;
    private Spinner spinner;
    private EditText searchEditText;
    private ImageButton searchBtn;

    public BrowseFragment() {
        // Required empty public constructor
    }
    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerViewServices);
        spinner = view.findViewById(R.id.searchBySpinner);
        searchEditText = view.findViewById(R.id.searchEditText);
        searchBtn = view.findViewById(R.id.searchBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadServices();
        String[] searchOptions = {"Search By","Title", "Category", "Username"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, searchOptions);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
        searchBtn.setOnClickListener(v -> searchService());

        return view;
    }

    private void searchService() {
        String searchBy = spinner.getSelectedItem().toString().toLowerCase();
        String query = searchEditText.getText().toString().toLowerCase();

        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
        }

        if (searchBy.equals("search by")) {
            Toast.makeText(requireContext(), "Please select a search option", Toast.LENGTH_SHORT).show();
            return;
        }

        ServicesApiService servicesApiService = ApiClient.getClient(requireContext()).create(ServicesApiService.class);
        servicesApiService.searchServices(searchBy,query).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Service> services = response.body();
                    // TODO: Update RecyclerView with services
                    adapter = new ServiceAdapter(response.body(), new ServiceAdapter.OnServiceClickListener() {
                        @Override
                        public void onServiceClick(Service service) {
                            Toast.makeText(requireContext(), "Clicked on: " + service.getTitle(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
                            intent.putExtra("serviceId", service.getId());
                            intent.putExtra("userId", service.getUserId());
                            Log.d("userId", "userId: " + service.getUserId());
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "No results found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadServices() {
        progressBar.setVisibility(View.VISIBLE);


        TokenManager tokenManager = new TokenManager(requireContext());
        ServicesApiService servicesApiService = ApiClient.getClient(requireContext())
                .create(ServicesApiService.class);
        Log.d("TOKEN", "Using token: " + tokenManager.getToken());
        Log.d("DEBUG", "Calling services API...");
        servicesApiService.getAllServices().enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new ServiceAdapter(response.body(), new ServiceAdapter.OnServiceClickListener() {
                        @Override
                        public void onServiceClick(Service service) {
                            Toast.makeText(requireContext(), "Clicked on: " + service.getTitle(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
                            intent.putExtra("serviceId", service.getId());
                            intent.putExtra("userId", service.getUserId());
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                // TODO : Handle failure
                Toast.makeText(requireContext(), "Failed to load services", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "Network error: " + t.getMessage());
            }
        });
    }
}