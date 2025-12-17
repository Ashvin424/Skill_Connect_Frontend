package com.ashvinprajapati.skillconnect.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ashvinprajapati.skillconnect.adapters.MessageAdapter;
import com.ashvinprajapati.skillconnect.databinding.ActivityChatBinding;
import com.ashvinprajapati.skillconnect.models.Messages;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private FirebaseFirestore db;
    private String chatId, currentUserId, otherUserId;
    private MaterialToolbar toolbar;

    private List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FIXED VIEW BINDING
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // INIT FIRESTORE (IMPORTANT)
        db = FirebaseFirestore.getInstance();

        // GET INTENT DATA
        chatId = getIntent().getStringExtra("chatId");
        currentUserId = getIntent().getStringExtra("currentUserId");
        otherUserId = getIntent().getStringExtra("otherUserId");
        String otherUserName = getIntent().getStringExtra("otherUserName");

        binding.toolbar.setTitle(otherUserName);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // SETUP RECYCLER VIEW
        messageAdapter = new MessageAdapter(messagesList, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.chatsRecyclerView.setLayoutManager(layoutManager);
        binding.chatsRecyclerView.setAdapter(messageAdapter);

        // LISTEN TO MESSAGES
        listenToMessages();

        // SEND MESSAGE
        binding.sendButton.setOnClickListener(v -> sendMessage());
    }

    private void listenToMessages() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Toast.makeText(ChatActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentChange docChange : value.getDocumentChanges()) {
                        if (docChange.getType() == DocumentChange.Type.ADDED) {
                            Messages message = docChange.getDocument().toObject(Messages.class);
                            messagesList.add(message);
                            messageAdapter.notifyItemInserted(messagesList.size() - 1);
                            binding.chatsRecyclerView.scrollToPosition(messagesList.size() - 1);
                        }
                    }
                });
    }

    private void sendMessage() {
        String text = binding.msgEditText.getText().toString().trim();
        if (text.isEmpty()) return;

        Messages message = new Messages(currentUserId, otherUserId, text, Timestamp.now());

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(doc -> {
                    binding.msgEditText.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }
}
