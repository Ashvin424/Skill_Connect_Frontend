package com.ashvinprajapati.skillconnect.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;

import java.util.List;

public class SelectImageAdapter extends RecyclerView.Adapter<SelectImageAdapter.ViewHolder>{
    private List<Uri> imageUris;
    private Context context;
    public SelectImageAdapter(List<Uri> imageUris, Context context) {
        this.imageUris = imageUris;
        this.context = context;
    }

    @NonNull
    @Override
    public SelectImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_preview, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SelectImageAdapter.ViewHolder holder, int position) {
        holder.imagePreview.setImageURI(imageUris.get(position));

    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imagePreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePreview = itemView.findViewById(R.id.imagePreview);  // Make sure this ID matches your XML
        }
    }

}
