package com.ashvinprajapati.skillconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Service implements Serializable {
    public Service() {
    }

    public Service(String category, String createdAt, String description, Long id, List<String> imageUrls, String title, Double userRating, Long userId, String username, String userProfileImageUrl, Boolean isActive, String providerMode) {
        this.category = category;
        this.createdAt = createdAt;
        this.description = description;
        this.id = id;
        this.imageUrls = imageUrls;
        this.title = title;
        this.userRating = userRating;
        this.userId = userId;
        this.username = username;
        this.userProfileImageUrl = userProfileImageUrl;
        this.isActive = isActive;
        this.providerMode = providerMode;
    }

    private Long id;

    private String title;

    private String description;

    private String category;


    private List<String> imageUrls;

    @SerializedName("createdAt")
    private String createdAt; // Automatically set to the current date and time when the entity is created

    private Long userId;
    private String username;

    private Double userRating;

    private String userProfileImageUrl;

    private Boolean isActive;
    private String providerMode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getUserRating() {
        return userRating;
    }

    public void setUserRating(Double userRating) {
        this.userRating = userRating;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getProviderMode() {
        return providerMode;
    }

    public void setProviderMode(String providerMode) {
        this.providerMode = providerMode;
    }
}

