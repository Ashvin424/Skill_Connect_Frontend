package com.ashvinprajapati.skillconnect.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Chat {
    private String chatId;
    private String lastMessage;
    private Timestamp timestamp;
    private List<String> participants;
    private String displayName;
    private String otherUserProfileImage;

    public Chat() {}

    public Chat(String chatId, String lastMessage, String displayName, String otherUserProfileImage, List<String> participants, Timestamp timestamp) {
        this.chatId = chatId;
        this.lastMessage = lastMessage;
        this.displayName = displayName;
        this.otherUserProfileImage = otherUserProfileImage;
        this.participants = participants;
        this.timestamp = timestamp;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOtherUserProfileImage() {
        return otherUserProfileImage;
    }

    public void setOtherUserProfileImage(String otherUserProfileImage) {
        this.otherUserProfileImage = otherUserProfileImage;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Getters and Setters
}

