package com.ashvinprajapati.skillconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.models.Messages;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> messageList;
    private String currentUserId;

    public MessageAdapter(List<Messages> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_messages, parent, false);  // Use item_message.xml layout
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Messages message = messageList.get(position);

        boolean isSent = message.getSenderId().equals(currentUserId);
        holder.showMessage(message.getMessage(), isSent);

        if (message.getTimestamp() != null) {
            Date date = message.getTimestamp().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String time = sdf.format(date);
            holder.setTime(time, isSent);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        View leftLayout, rightLayout;
        TextView leftMessage, rightMessage, leftTime, rightTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.left_message_layout);
            rightLayout = itemView.findViewById(R.id.right_message_layout);
            leftMessage = itemView.findViewById(R.id.left_message_text);
            rightMessage = itemView.findViewById(R.id.right_message_text);
            leftTime = itemView.findViewById(R.id.left_message_time);
            rightTime = itemView.findViewById(R.id.right_message_time);
        }

        void showMessage(String message, boolean isSent) {
            if (isSent) {
                rightLayout.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.GONE);
                rightMessage.setText(message);
            } else {
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.GONE);
                leftMessage.setText(message);
            }
        }

        void setTime(String time, boolean isSent) {
            if (isSent) {
                rightTime.setText(time);
            } else {
                leftTime.setText(time);
            }
        }
    }
}
