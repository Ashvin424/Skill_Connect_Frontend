package com.ashvinprajapati.skillconnect.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        BookingStatus status = booking.getStatus();

        holder.setStatusBtn.setOnClickListener(null);

        if (status.equals(BookingStatus.PENDING)) {

            holder.setStatusBtn.setVisibility(View.VISIBLE);
            holder.setStatusBtn.setText("ACTION");

            holder.setStatusBtn.setOnClickListener(v -> showPendingActionsDialog(
                    booking,
                    holder.getAdapterPosition()
            ));

        }
        else if (status.equals(BookingStatus.CONFIRMED)) {

            holder.setStatusBtn.setVisibility(View.VISIBLE);
            holder.setStatusBtn.setText("COMPLETE");
            holder.setStatusBtn.setTextColor(Color.parseColor("#00A300"));

            holder.setStatusBtn.setOnClickListener(v ->
                    showCompleteConfirmation(booking, holder.getAdapterPosition())
            );

        }
        else {
            holder.setStatusBtn.setVisibility(View.GONE);
        }



    }

    private void showPendingActionsDialog(BookingResponse booking, int position) {

        String[] options = {"CONFIRM", "CANCEL"};

        new AlertDialog.Builder(context)
                .setTitle("Booking Action")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        updateBookingStatus(booking,"CONFIRMED" ,position);
                    } else {
                        updateBookingStatus(booking,"CANCELLED" ,position);
                    }
                })
                .show();
    }

    private void showCompleteConfirmation(BookingResponse booking, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Complete Booking")
                .setMessage("Are you sure the service is completed?")
                .setPositiveButton("Yes", (d, w) ->
                        updateBookingStatus(booking,"COMPLETED" ,position))
                .setNegativeButton("No", null)
                .show();
    }


    private void updateBookingStatus(BookingResponse booking, String selectedStatus, int position) {

        BookingStatusUpdateRequest requestDto =
                new BookingStatusUpdateRequest(selectedStatus);

        TokenManager tokenManager = new TokenManager(context);
        String token = "Bearer " + tokenManager.getToken();

        BookingApiService api =
                ApiClient.getClient(context).create(BookingApiService.class);

        // 🔒 Disable button to avoid double click
        notifyItemChanged(position);

        api.updateBookingStatus(booking.getId(), requestDto, token)
                .enqueue(new Callback<Booking>() {

                    @Override
                    public void onResponse(Call<Booking> call, Response<Booking> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(context,
                                    "Failed to update booking status",
                                    Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                            return;
                        }

                        BookingResponse updated =
                                mapToBookingResponse(response.body());

                        // 🔁 Find item by ID (SAFE)
                        for (int i = 0; i < bookings.size(); i++) {
                            if (bookings.get(i).getId().equals(updated.getId())) {
                                bookings.set(i, updated);
                                notifyItemChanged(i);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Booking> call, Throwable t) {
                        Toast.makeText(context,
                                "Network error. Try again.",
                                Toast.LENGTH_SHORT).show();
                        notifyItemChanged(position);
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

    private BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse br = new BookingResponse();
        br.setId(booking.getId());
        br.setStatus(booking.getStatus());
        br.setRequestedByName(booking.getRequestedBy().getName());
        br.setServiceTitle(booking.getService().getTitle());
        br.setRequestedAt(booking.getRequestedAt());
        br.setConfirmedAt(booking.getConfirmedAt());
        br.setCancelledAt(booking.getCancelledAt());
        br.setUpdatedAt(booking.getUpdatedAt());
        br.setUserId(booking.getRequestedBy().getId());
        br.setServiceId(booking.getService().getId());
        br.setServiceProviderId(booking.getService().getUserId());
        return br;
    }

}
