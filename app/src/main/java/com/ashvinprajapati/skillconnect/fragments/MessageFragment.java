package com.ashvinprajapati.skillconnect.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.adapters.ChatsAdapter;
import com.ashvinprajapati.skillconnect.models.Chat;
import com.ashvinprajapati.skillconnect.models.ProfileResponse;
import com.ashvinprajapati.skillconnect.models.User;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private ChatsAdapter chatsAdapter;
    private List<Chat> chatList = new ArrayList<>();
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String currentUserId;

    public MessageFragment() {
        // Required empty public constructor
    }
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        chatRecyclerView = view.findViewById(R.id.chatsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        chatsAdapter = new ChatsAdapter(requireContext(),chatList);
        chatRecyclerView.setAdapter(chatsAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        currentUserId = getCurrentUserId();
        fetchChats();
        return view;
    }

    private void fetchChats() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("FIREBASE", "Current User ID: " + currentUserId);
        chatList.clear();
        db.collection("chats")
                .whereArrayContains("participants", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                  for (DocumentSnapshot doc : querySnapshot.getDocuments()){
                      String chatId = doc.getId();
                      List<String> participants = (List<String>) doc.get("participants");
                      doc.getReference().collection("messages")
                              .orderBy("timestamp", Query.Direction.DESCENDING)
                              .limit(1)
                              .addSnapshotListener((messageSnapshot, e) -> {
                                  if (e != null) {
                                      Log.e("FIREBASE", "Listen failed", e);
                                      return;
                                  }
                                  progressBar.setVisibility(View.GONE);
                                  if (messageSnapshot != null && !messageSnapshot.isEmpty()) {
                                      DocumentSnapshot lastMessage = messageSnapshot.getDocuments().get(0);
                                      String message = lastMessage.getString("message");
                                      Timestamp timeStamp = lastMessage.getTimestamp("timestamp");

                                      String otherUserId = extractOtherUserId(chatId, currentUserId);
                                      Long userId = Long.parseLong(otherUserId);
                                      UserApiService userApiService = ApiClient.getClient(getContext()).create(UserApiService.class);
                                      userApiService.getUserById(userId).enqueue(new Callback<ProfileResponse>() {
                                          @Override
                                          public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                                              if (response.isSuccessful()) {
                                                  Chat chat = new Chat(chatId, message, response.body().getName(),
                                                          response.body().getProfileImageUrl(), participants, timeStamp);

                                                  // 🧠 Optional: Check if chat already exists and update instead of re-adding
                                                  boolean updated = false;
                                                  for (int i = 0; i < chatList.size(); i++) {
                                                      if (chatList.get(i).getChatId().equals(chatId)) {
                                                          chatList.set(i, chat);
                                                          updated = true;
                                                          break;
                                                      }
                                                  }
                                                  if (!updated) {
                                                      chatList.add(chat);
                                                  }
                                                  chatsAdapter.notifyDataSetChanged();
                                              }
                                          }

                                          @Override
                                          public void onFailure(Call<ProfileResponse> call, Throwable t) {
                                              Log.e("API", "Failed to fetch user info", t);
                                          }
                                      });
                                  }
                              });

                  }
                });
    }

    private String extractOtherUserId(String chatId, String currentUserId) {
        String[] ids = chatId.split("_");
        return ids[0].equals(currentUserId) ? ids[1] : ids[0];
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = getActivity().getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);

        return prefs.getString("currentUserId", null);

    }

}