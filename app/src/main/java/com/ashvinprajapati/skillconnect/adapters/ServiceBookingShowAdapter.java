package com.ashvinprajapati.skillconnect.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.Booking;
import com.ashvinprajapati.skillconnect.models.BookingResponse;
import com.ashvinprajapati.skillconnect.models.BookingStatus;
import com.ashvinprajapati.skillconnect.models.BookingStatusUpdateRequest;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.BookingApiService;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceBookingShowAdapter extends RecyclerView.Adapter<ServiceBookingShowAdapter.bookingViewHolder> {

    private Context context;
    private List<BookingResponse> bookings = new ArrayList<>();

    public ServiceBookingShowAdapter(Context context, List<BookingResponse> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public ServiceBookingShowAdapter.bookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_bookings, parent, false);
        return new bookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceBookingShowAdapter.bookingViewHolder holder, int position) {
        BookingResponse booking = bookings.get(position);
        holder.bookingTitleTV.setText(booking.getServiceTitle());
        holder.bookingStatusTV.setText(booking.getStatus().toString());
        holder.bookedByTV.setText("Booked by "+booking.getRequestedByName());
        holder.setStatusBtn.setText(booking.getStatus().toString());
        if (booking.getStatus() == BookingStatus.CONFIRMED){
            holder.setStatusBtn.setEnabled(false);
            holder.setStatusBtn.setTextColor(Color.parseColor("#00A300"));
            holder.setStatusBtn.setAlpha(0.5f);

        }
        if (booking.getStatus() == BookingStatus.CANCELLED){
            holder.setStatusBtn.setEnabled(false);
            holder.setStatusBtn.setTextColor(Color.parseColor("#FF0000"));
            holder.setStatusBtn.setAlpha(0.5f);
        }
        holder.setStatusBtn.setOnClickListener(v -> {
            String[] options = {"CONFIRMED", "CANCELLED"};

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Update Booking Status")
                    .setItems(options, (dialog, which) -> {
                        String selectedStatus = options[which];
                        updateBookingStatus(booking, selectedStatus, holder.getAdapterPosition());
                    });
            builder.show();
        });


    }

    private void updateBookingStatus(BookingResponse booking, String selectedStatus, int adapterPosition) {
        BookingStatusUpdateRequest requestDto = new BookingStatusUpdateRequest(selectedStatus);
        TokenManager tokenManager = new TokenManager(context.getApplicationContext());
        String token = "Bearer " + tokenManager.getToken();
        BookingApiService bookingApiService = ApiClient.getClient(context).create(BookingApiService.class);
        bookingApiService.updateBookingStatus(booking.getId(), requestDto, token).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful() && response.body() != null){
                    Booking updateBooking = response.body();
                    BookingResponse bookingResponse = new BookingResponse();
                    bookingResponse.setId(updateBooking.getId());
                    bookingResponse.setStatus(updateBooking.getStatus());
                    bookingResponse.setRequestedByName(updateBooking.getRequestedBy().getName());
                    bookingResponse.setServiceTitle(updateBooking.getService().getTitle());
                    bookingResponse.setRequestedAt(updateBooking.getRequestedAt());
                    bookingResponse.setConfirmedAt(updateBooking.getConfirmedAt());
                    bookingResponse.setCancelledAt(updateBooking.getCancelledAt());
                    bookingResponse.setUpdatedAt(updateBooking.getUpdatedAt());
                    bookingResponse.setUserId(updateBooking.getRequestedBy().getId());
                    bookingResponse.setServiceId(updateBooking.getService().getId());
                    bookingResponse.setServiceProviderId(updateBooking.getService().getUserId());

                    bookings.set(adapterPosition, bookingResponse);
                    notifyItemChanged(adapterPosition);

                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return bookings.size();
    }
    public void updateData(List<BookingResponse> newBookings) {
        bookings.clear();
        bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

    public static class bookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingTitleTV, bookedByTV, bookingStatusTV;
        MaterialButton setStatusBtn;
        public bookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingTitleTV = itemView.findViewById(R.id.bookingTitleTV);
            bookedByTV = itemView.findViewById(R.id.bookedByTV);
            bookingStatusTV = itemView.findViewById(R.id.bookingStatusTV);
            setStatusBtn = itemView.findViewById(R.id.setStatusBtn);
        }
    }
}
