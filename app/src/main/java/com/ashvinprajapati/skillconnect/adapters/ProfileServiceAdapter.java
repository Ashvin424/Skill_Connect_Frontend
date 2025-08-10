package com.ashvinprajapati.skillconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.Service;

import java.util.List;

public class ProfileServiceAdapter extends RecyclerView.Adapter<ProfileServiceAdapter.ViewHolder> {
    private List<Service> serviceList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Service service);
    }
    public ProfileServiceAdapter(List<Service> serviceList, OnItemClickListener onItemClickListener) {
        this.serviceList = serviceList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProfileServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_profile, parent, false);

        return new ProfileServiceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileServiceAdapter.ViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceTitleTextView.setText(service.getTitle());
        holder.firstCharTextView.setText(service.getTitle() != null && !service.getTitle().isEmpty() ? service.getTitle().substring(0, 1) : "?");

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceTitleTextView, firstCharTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTitleTextView = itemView.findViewById(R.id.serviceTitleTextView);
            firstCharTextView = itemView.findViewById(R.id.firstCharTextView);
        }
    }
}
