package com.ashvinprajapati.skillconnect.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.BookingResponse;
import com.ashvinprajapati.skillconnect.models.BookingStatus;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsShowAdapter extends RecyclerView.Adapter<MyBookingsShowAdapter.bookingViewHolder> {

    private Context context;
    private List<BookingResponse> bookings = new ArrayList<>();

    public MyBookingsShowAdapter(Context context, List<BookingResponse> bookings) {
        this.bookings = bookings;
        this.context = context;
    }

    @NonNull
    @Override
    public bookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_bookings, parent, false);
        return new bookingViewHolder(view);
    }
    public void updateData(List<BookingResponse> newBookings) {
        bookings.clear();
        bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull bookingViewHolder holder, int position) {
        BookingResponse booking = bookings.get(position);
        holder.bookingTitleTV.setText(booking.getServiceTitle());
        holder.postedByTV.setText("Posted By "+booking.getServiceProviderName());
        holder.setStatusBtn.setText(booking.getStatus().toString());
        if (booking.getStatus() == BookingStatus.CONFIRMED){
            holder.setStatusBtn.setTextColor(Color.parseColor("#00A300"));

        }
        if (booking.getStatus() == BookingStatus.CANCELLED){
            holder.setStatusBtn.setTextColor(Color.parseColor("#FF0000"));
        }

    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class bookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingTitleTV, postedByTV;
        MaterialButton setStatusBtn;
        public bookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingTitleTV = itemView.findViewById(R.id.bookingTitleTV);
            postedByTV = itemView.findViewById(R.id.postedByTV);
            setStatusBtn = itemView.findViewById(R.id.setStatusBtn);
        }
    }
}
