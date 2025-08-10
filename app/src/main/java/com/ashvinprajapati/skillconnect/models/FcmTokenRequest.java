package com.ashvinprajapati.skillconnect.models;

public class FcmTokenRequest {
    private String email;
    private String fcmToken;

    public FcmTokenRequest(String email, String fcmToken) {
        this.email = email;
        this.fcmToken = fcmToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
