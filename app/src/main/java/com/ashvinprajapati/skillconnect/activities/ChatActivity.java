package com.ashvinprajapati.skillconnect.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.adapters.MessageAdapter;
import com.ashvinprajapati.skillconnect.models.Messages;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {

    private EditText msgEditText;
    private TextView toolbarTitleTextView;
    private ImageButton sendButton;
    private RecyclerView chatsRecyclerView;
    private List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private String chatId, currentUserId, otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        // Get data from intent
        chatId = getIntent().getStringExtra("chatId");
        currentUserId = getIntent().getStringExtra("currentUserId");
        otherUserId = getIntent().getStringExtra("otherUserId");
        String otherUserName = getIntent().getStringExtra("otherUserName");

        toolbarTitleTextView.setText(otherUserName);

        // Setup RecyclerView
        messageAdapter = new MessageAdapter(messagesList, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatsRecyclerView.setLayoutManager(layoutManager);
        chatsRecyclerView.setAdapter(messageAdapter);

        // Load messages
        listenToMessages();

        // Send button
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void init() {
        msgEditText = findViewById(R.id.msgEditText);
        toolbarTitleTextView = findViewById(R.id.toolbarTitleTextView);
        sendButton = findViewById(R.id.sendButton);
        chatsRecyclerView = findViewById(R.id.chatsRecyclerView);
        toolbar = findViewById(R.id.toolbar);
        db = FirebaseFirestore.getInstance();
    }

    private void listenToMessages() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ChatActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange docChange : value.getDocumentChanges()) {
                            if (docChange.getType() == DocumentChange.Type.ADDED) {
                                Messages message = docChange.getDocument().toObject(Messages.class);
                                messagesList.add(message);
                                messageAdapter.notifyItemInserted(messagesList.size() - 1);
                                chatsRecyclerView.scrollToPosition(messagesList.size() - 1);
                            }
                        }
                    }
                });
    }

    private void sendMessage() {
        String text = msgEditText.getText().toString().trim();
        if (text.isEmpty()) return;

        Messages message = new Messages(currentUserId, otherUserId, text, Timestamp.now());

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(doc -> {
                    msgEditText.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }
}
