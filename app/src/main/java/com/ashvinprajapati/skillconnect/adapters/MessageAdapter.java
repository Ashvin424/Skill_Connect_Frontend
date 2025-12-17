package com.ashvinprajapati.skillconnect.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

    private final List<Messages> messageList;
    private final String currentUserId;

    public MessageAdapter(List<Messages> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_messages, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Messages message = messageList.get(position);

        boolean isMyMessage = message.getSenderId().equals(currentUserId);

        holder.messageText.setText(message.getMessage());

        // -------- Time formatting --------
        if (message.getTimestamp() != null) {
            Date date = message.getTimestamp().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            holder.messageTime.setText(sdf.format(date));
        }

        // -------- Alignment + style --------
        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams) holder.messageBubble.getLayoutParams();

        if (isMyMessage) {
            params.gravity = Gravity.END;
            holder.messageBubble.setBackgroundResource(R.drawable.bg_message_sent);
            holder.messageTime.setGravity(Gravity.END);
        } else {
            params.gravity = Gravity.START;
            holder.messageBubble.setBackgroundResource(R.drawable.bg_message_received);
            holder.messageTime.setGravity(Gravity.START);
        }

        holder.messageBubble.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ================= VIEW HOLDER =================

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout messageBubble;
        TextView messageText, messageTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBubble = itemView.findViewById(R.id.messageBubble);
            messageText = itemView.findViewById(R.id.messageText);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }
}
