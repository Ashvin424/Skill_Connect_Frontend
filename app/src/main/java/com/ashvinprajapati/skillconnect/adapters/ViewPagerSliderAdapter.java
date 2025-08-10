package com.ashvinprajapati.skillconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.ashvinprajapati.skillconnect.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ViewPagerSliderAdapter extends RecyclerView.Adapter<ViewPagerSliderAdapter.SliderViewHolder> {

    List<String> imageUrls;

    public ViewPagerSliderAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView sliderImageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            sliderImageView = itemView.findViewById(R.id.sliderImageView);
        }
    }

    @NonNull
    @Override
    public ViewPagerSliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerSliderAdapter.SliderViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.icon_profile)
                .error(R.drawable.icon_profile)
                .fitCenter()
                .into(holder.sliderImageView);

    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}
