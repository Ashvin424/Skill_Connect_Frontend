package com.ashvinprajapati.skillconnect.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.Service;
import com.bumptech.glide.Glide;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private OnServiceClickListener onServiceClickListener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(List<Service> services, OnServiceClickListener listener) {
        this.serviceList = services;
        this.onServiceClickListener = listener;
    }

    @NonNull
    @Override
    public ServiceAdapter.ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceAdapter.ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.textTitle.setText("Service: "+service.getTitle());
        if (service.getUsername() != null) {
            holder.textPostedBy.setText("Posted By "+service.getUsername());
        } else {
            holder.textPostedBy.setText("Posted By Unknown User");
        }
        Log.d("DEBUG", "Service ID: " + service.getId());
        Log.d("DEBUG", "ImageUrls: " + service.getImageUrls());
        if (service.getImageUrls() != null && !service.getImageUrls().isEmpty()) {
            Log.d("GLIDE_IMAGE_URL", service.getImageUrls().get(0));
            Glide.with(holder.imageService.getContext())
                    .load(service.getImageUrls().get(0))
                    .placeholder(R.drawable.icon_profile)
                    .error(R.drawable.icon_help)
                    .into(holder.imageService);
        }else {
            holder.imageService.setImageResource(R.drawable.icon_profile);
        }

        holder.itemView.setOnClickListener(v-> onServiceClickListener.onServiceClick(service));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textPostedBy;
        ImageView imageService;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPostedBy = itemView.findViewById(R.id.textPostedBy);
            imageService = itemView.findViewById(R.id.imageService);
        }
    }
}
