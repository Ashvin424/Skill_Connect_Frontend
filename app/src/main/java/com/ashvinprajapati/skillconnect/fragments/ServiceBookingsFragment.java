package com.ashvinprajapati.skillconnect.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.ashvinprajapati.skillconnect.adapters.ServiceBookingShowAdapter;
import com.ashvinprajapati.skillconnect.models.BookingResponse;
import com.ashvinprajapati.skillconnect.models.PagedResponse;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.BookingApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceBookingsFragment extends Fragment {

    private RecyclerView bookingsRV;
    private ServiceBookingShowAdapter serviceBookingShowAdapter;
    private List<BookingResponse> bookingsList = new ArrayList<>();

    private View noInternetLayout;
    private Button btnRetry;
    private TextView noDataText;

    public ServiceBookingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_bookings, container, false);

        bookingsRV = view.findViewById(R.id.bookingsRV);
        noInternetLayout = view.findViewById(R.id.noInternetLayout);
        btnRetry = view.findViewById(R.id.btnRetry);
        noDataText = view.findViewById(R.id.noDataText);

        bookingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceBookingShowAdapter = new ServiceBookingShowAdapter(getContext(), bookingsList);
        bookingsRV.setAdapter(serviceBookingShowAdapter);

        btnRetry.setOnClickListener(v -> fetchBookings());

        fetchBookings();
        return view;
    }

    private void fetchBookings() {

        long currentUserId = Long.parseLong(getCurrentUserId());
        BookingApiService bookingApiService = ApiClient.getClient(getContext()).create(BookingApiService.class);

        bookingApiService.getBookingsByProvider(currentUserId, 0, 10)
                .enqueue(new Callback<PagedResponse<BookingResponse>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<BookingResponse>> call, Response<PagedResponse<BookingResponse>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            List<BookingResponse> bookings = response.body().getContent();

                            if (bookings.isEmpty()) {
                                showNoDataUI();
                            } else {
                                showDataUI();
                                serviceBookingShowAdapter.updateData(bookings);
                            }
                        } else {
                            showNoInternetUI();
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<BookingResponse>> call, Throwable t) {
                        if (!isAdded()) return;
                        Log.e("ServiceBookings", "Error fetching", t);
                        showNoInternetUI();
                    }
                });
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = getContext().getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }



    private void showNoInternetUI() {
        noInternetLayout.setVisibility(View.VISIBLE);
        noDataText.setVisibility(View.GONE);
        bookingsRV.setVisibility(View.GONE);
    }

    private void showNoDataUI() {
        noDataText.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
        bookingsRV.setVisibility(View.GONE);
    }

    private void showDataUI() {
        bookingsRV.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
        noDataText.setVisibility(View.GONE);
    }
}
