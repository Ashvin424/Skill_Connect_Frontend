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
import android.widget.Toast;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.adapters.MyBookingsShowAdapter;
import com.ashvinprajapati.skillconnect.models.BookingResponse;
import com.ashvinprajapati.skillconnect.models.PagedResponse;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.BookingApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyBookingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBookingsFragment extends Fragment {
    private RecyclerView bookingsRV;
    private MyBookingsShowAdapter myBookingsShowAdapter;
    private List<BookingResponse> bookingsList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyBookingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBookingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyBookingsFragment newInstance(String param1, String param2) {
        MyBookingsFragment fragment = new MyBookingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);
        // Inflate the layout for this fragment
        bookingsRV = view.findViewById(R.id.bookingsRV);
        bookingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        myBookingsShowAdapter = new MyBookingsShowAdapter(getContext(), bookingsList);
        bookingsRV.setAdapter(myBookingsShowAdapter);

        fetchBookings();
        return view;
    }

    private void fetchBookings() {
        long currentUserId = Long.parseLong(getCurrentUserId());
        Toast.makeText(getContext(), "Current User ID: " + currentUserId, Toast.LENGTH_SHORT).show();

        BookingApiService bookingApiService = ApiClient.getClient(getContext()).create(BookingApiService.class);
        bookingApiService.getBookingsByRequester(currentUserId,0,10).enqueue(new Callback<PagedResponse<BookingResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<BookingResponse>> call, Response<PagedResponse<BookingResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookingResponse> bookings = response.body().getContent();
                    myBookingsShowAdapter.updateData(bookings);
                }else {
                    Toast.makeText(getContext(), "Failed to fetch bookings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<BookingResponse>> call, Throwable t) {
                Log.e("BookingActivity", "Error fetching bookings", t);
                Toast.makeText(getContext(), "Failed to fetch bookings", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getCurrentUserId() {
        SharedPreferences prefs = getContext().getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }
}