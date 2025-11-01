package com.ashvinprajapati.skillconnect.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.adapters.MyBookingsShowAdapter;
import com.ashvinprajapati.skillconnect.models.BookingResponse;
import com.ashvinprajapati.skillconnect.models.PagedResponse;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.BookingApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingsFragment extends Fragment {

    private RecyclerView bookingsRV;
    private TextView noBookingTV;
    private LinearLayout noInternetLayout;
    private Button btnRetry;

    private MyBookingsShowAdapter myBookingsShowAdapter;
    private List<BookingResponse> bookingsList = new ArrayList<>();

    public MyBookingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);

        bookingsRV = view.findViewById(R.id.bookingsRV);
        noBookingTV = view.findViewById(R.id.noBookingTV);
        noInternetLayout = view.findViewById(R.id.noInternetLayout);
        btnRetry = view.findViewById(R.id.btnRetry);

        bookingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        myBookingsShowAdapter = new MyBookingsShowAdapter(getContext(), bookingsList);
        bookingsRV.setAdapter(myBookingsShowAdapter);

        btnRetry.setOnClickListener(v -> fetchBookings());

        fetchBookings();
        return view;
    }

    private void fetchBookings() {
        if (!isInternetAvailable()) {
            showNoInternet();
            return;
        }

        hideAllStates();

        long currentUserId = Long.parseLong(getCurrentUserId());
        TokenManager tokenManager = new TokenManager(getContext());
        String token = "Bearer " + tokenManager.getToken();

        BookingApiService bookingApiService = ApiClient.getClient(getContext()).create(BookingApiService.class);
        bookingApiService.getBookingsByRequester(currentUserId,0,10, token).enqueue(new Callback<PagedResponse<BookingResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<BookingResponse>> call, Response<PagedResponse<BookingResponse>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<BookingResponse> bookings = response.body().getContent();

                    if (bookings == null || bookings.isEmpty()) {
                        showNoBookings();
                    } else {
                        bookingsList.clear();
                        bookingsList.addAll(bookings);
                        myBookingsShowAdapter.notifyDataSetChanged();

                        bookingsRV.setVisibility(View.VISIBLE);
                    }
                } else {
                    showNoBookings();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<BookingResponse>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("Booking", "Error fetching bookings", t);
                showNoInternet();
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void hideAllStates() {
        noInternetLayout.setVisibility(View.GONE);
        noBookingTV.setVisibility(View.GONE);
        bookingsRV.setVisibility(View.GONE);
    }

    private void showNoInternet() {
        noInternetLayout.setVisibility(View.VISIBLE);
        noBookingTV.setVisibility(View.GONE);
        bookingsRV.setVisibility(View.GONE);
    }

    private void showNoBookings() {
        noBookingTV.setVisibility(View.VISIBLE);
        bookingsRV.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.GONE);
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = getContext().getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", "0");
    }
}
