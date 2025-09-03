package com.ashvinprajapati.skillconnect.models;

public class ProfileImageUploadResponseDTO {
    private String profileImageUrl;

    public ProfileImageUploadResponseDTO() {
    }

    public ProfileImageUploadResponseDTO(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
