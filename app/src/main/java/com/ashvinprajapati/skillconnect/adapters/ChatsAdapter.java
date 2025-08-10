package com.ashvinprajapati.skillconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.activities.ChatActivity;
import com.ashvinprajapati.skillconnect.models.Chat;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {
    private List<Chat> chats;
    private Context context;
    public ChatsAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }


    @NonNull
    @Override
    public ChatsAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chats, parent, false);
        return new ChatsAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.lastMsgTextView.setText(chat.getLastMessage());
        holder.usernameTextView.setText(chat.getDisplayName());
        String currentUserId = getCurrentUserId();
        String otherUserId = extractOtherUserId(chat.getChatId(), currentUserId);
        if (chat.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String time = sdf.format(chat.getTimestamp().toDate());
            holder.timeStampTextView.setText(time);
        }
        if (chat.getOtherUserProfileImage() != null && !chat.getOtherUserProfileImage().isEmpty()) {
            Glide.with(context)
                    .load(chat.getOtherUserProfileImage())
                    .placeholder(R.drawable.icon_profile)
                    .into(holder.userProfileImageView);
        } else {
            holder.userProfileImageView.setImageResource(R.drawable.icon_profile);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatId", chat.getChatId());
            intent.putExtra("currentUserId", currentUserId);
            intent.putExtra("otherUserId", otherUserId);
            intent.putExtra("otherUserName", chat.getDisplayName());
            context.startActivity(intent);
        });

    }

    private String extractOtherUserId(String chatId, String currentUserId) {
        String[] ids = chatId.split("_");
        return ids[0].equals(currentUserId) ? ids[1] : ids[0];
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = context.getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfileImageView;
        TextView usernameTextView, lastMsgTextView, timeStampTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            lastMsgTextView = itemView.findViewById(R.id.lastMsgTextView);
            timeStampTextView = itemView.findViewById(R.id.timeStampTextView);

        }
    }
}
