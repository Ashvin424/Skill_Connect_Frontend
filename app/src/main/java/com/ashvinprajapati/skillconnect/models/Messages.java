package com.ashvinprajapati.skillconnect.models;

import com.google.firebase.Timestamp;

public class Messages {
    private String senderId;
    private String receiverId;
    private String message;
    private Timestamp timestamp;


    public Messages() {
    }

    public Messages(String senderId, String receiverId, String message, Timestamp timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
