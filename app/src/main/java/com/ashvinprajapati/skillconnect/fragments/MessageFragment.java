package com.ashvinprajapati.skillconnect.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.ashvinprajapati.skillconnect.R;
import com.ashvinprajapati.skillconnect.adapters.ChatsAdapter;
import com.ashvinprajapati.skillconnect.models.Chat;
import com.ashvinprajapati.skillconnect.models.ProfileResponse;
import com.ashvinprajapati.skillconnect.models.User;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.ashvinprajapati.skillconnect.utils.NetworkUtils;
import com.ashvinprajapati.skillconnect.utils.Refreshable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment implements Refreshable {

    private RecyclerView chatRecyclerView;
    private ChatsAdapter chatsAdapter;
    private List<Chat> chatList = new ArrayList<>();

    private LottieAnimationView loadingAnim;
    private View noInternetLayout, emptyStateLayout;
    private Button btnRetry;

    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        chatRecyclerView = view.findViewById(R.id.chatsRecyclerView);
        loadingAnim = view.findViewById(R.id.loadingAnim);
        noInternetLayout = view.findViewById(R.id.noInternetLayout);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        btnRetry = view.findViewById(R.id.btnRetry);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatsAdapter = new ChatsAdapter(requireContext(), chatList);
        chatRecyclerView.setAdapter(chatsAdapter);

        db = FirebaseFirestore.getInstance();
        currentUserId = getCurrentUserId();

        btnRetry.setOnClickListener(v -> loadChats());

        loadChats();

        return view;
    }

    private void loadChats() {
        // Internet check
        if (!NetworkUtils.isConnected(requireContext())) {
            showNoInternet();
            return;
        }

        showLoading();
        fetchChats();
    }

    private void fetchChats() {
        chatList.clear();

        db.collection("chats")
                .whereArrayContains("participants", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!isAdded()) return;

                    if (querySnapshot.isEmpty()) {
                        showEmpty();
                        return;
                    }

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String chatId = doc.getId();
                        List<String> participants = (List<String>) doc.get("participants");

                        doc.getReference().collection("messages")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1)
                                .addSnapshotListener((messageSnapshot, e) -> {

                                    if (!isAdded()) return;

                                    loadingAnim.setVisibility(View.GONE);
                                    loadingAnim.pauseAnimation();

                                    if (e != null || messageSnapshot == null || messageSnapshot.isEmpty())
                                        return;

                                    DocumentSnapshot lastMessage = messageSnapshot.getDocuments().get(0);
                                    String message = lastMessage.getString("message");
                                    Timestamp timestamp = lastMessage.getTimestamp("timestamp");

                                    String otherUserId = extractOtherUserId(chatId, currentUserId);
                                    Long userId = Long.parseLong(otherUserId);

                                    UserApiService api = ApiClient.getClient(requireContext()).create(UserApiService.class);
                                    api.getUserById(userId).enqueue(new Callback<ProfileResponse>() {
                                        @Override
                                        public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                                            if (!isAdded()) return;
                                            if (!response.isSuccessful() || response.body() == null) return;

                                            ProfileResponse user = response.body();

                                            Chat chat = new Chat(chatId, message, user.getName(),
                                                    user.getProfileImageUrl(), participants, timestamp);

                                            boolean updated = false;
                                            for (int i = 0; i < chatList.size(); i++) {
                                                if (chatList.get(i).getChatId().equals(chatId)) {
                                                    chatList.set(i, chat);
                                                    updated = true;
                                                    break;
                                                }
                                            }

                                            if (!updated) chatList.add(chat);

                                            showList();
                                            chatsAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(Call<ProfileResponse> call, Throwable t) {}
                                    });
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    showNoInternet();
                });
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("SkillConnectPrefs", Context.MODE_PRIVATE);
        return prefs.getString("currentUserId", null);
    }

    private String extractOtherUserId(String chatId, String currentUserId) {
        String[] ids = chatId.split("_");
        return ids[0].equals(currentUserId) ? ids[1] : ids[0];
    }

    // UI Helpers
    private void showLoading() {
        loadingAnim.setVisibility(View.VISIBLE);
        loadingAnim.playAnimation();
        noInternetLayout.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
        chatRecyclerView.setVisibility(View.GONE);
    }

    private void showNoInternet() {
        loadingAnim.setVisibility(View.GONE);
        loadingAnim.pauseAnimation();
        noInternetLayout.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
        chatRecyclerView.setVisibility(View.GONE);
    }

    private void showEmpty() {
        loadingAnim.setVisibility(View.GONE);
        loadingAnim.pauseAnimation();
        emptyStateLayout.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
        chatRecyclerView.setVisibility(View.GONE);
    }

    private void showList() {
        if (chatList.isEmpty()) {
            showEmpty();
        } else {
            loadingAnim.setVisibility(View.GONE);
            loadingAnim.pauseAnimation();
            chatRecyclerView.setVisibility(View.VISIBLE);
            noInternetLayout.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        loadChats();
    }
}
