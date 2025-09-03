package com.ashvinprajapati.skillconnect.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.RatingResponse;
import com.bumptech.glide.Glide;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserRatingAdapter extends RecyclerView.Adapter<UserRatingAdapter.RatingViewHolder> {
    private Context context;
    private List<RatingResponse> ratingsList = new ArrayList<>();

    public UserRatingAdapter(List<RatingResponse> ratingsList) {
        this.ratingsList = ratingsList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ratings, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        RatingResponse rating = ratingsList.get(position);
        holder.reviewerName.setText(rating.getReviewerName());
        holder.ratingTextView.setText(String.valueOf(rating.getRatingValue()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime dateTime = LocalDateTime.parse(rating.getCreatedAt());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
            String formatted = dateTime.format(formatter);
            holder.createdAtTextView.setText("Rated on "+formatted);
        }
        else {
            holder.createdAtTextView.setText(rating.getCreatedAt());
        }



        holder.commentTextView.setText(rating.getComment());
        Glide.with(holder.reviewerProfileImage)
                .load(rating.getReviewerProfileImageUrl())
                .placeholder(R.drawable.icon_profile)
                .error(R.drawable.icon_help)
                .into(holder.reviewerProfileImage);


    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    public void updateData(List<RatingResponse> ratings) {
        ratingsList.clear();
        ratingsList.addAll(ratings);
        notifyDataSetChanged();
    }

    static class RatingViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewerProfileImage;
        TextView reviewerName, ratingTextView, createdAtTextView, commentTextView;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerProfileImage = itemView.findViewById(R.id.reviewerProfileImage);
            reviewerName = itemView.findViewById(R.id.reviewerName);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            createdAtTextView = itemView.findViewById(R.id.createdAtTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }


}
