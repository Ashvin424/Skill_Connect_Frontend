package com.ashvinprajapati.skillconnect.models;

import java.time.LocalDateTime;
import java.util.List;

public class ProfileResponse {
    private String displayUsername;
    private String name;
    private String email;
    private String bio;
    private String location;
    private String skills;
    private String profileImageUrl;
    private String createdAt;
    private int skillCount;
    private int serviceCount;
    private int reviewCount;
    private double averageRating;
    private List<Service> services;

    public ProfileResponse() {
    }

    public ProfileResponse(double averageRating, String bio, String createdAt, String displayUsername, String email, String location, String name, String profileImageUrl, int reviewCount, int serviceCount, List<Service> services, int skillCount, String skills) {
        this.averageRating = averageRating;
        this.bio = bio;
        this.createdAt = createdAt;
        this.displayUsername = displayUsername;
        this.email = email;
        this.location = location;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.reviewCount = reviewCount;
        this.serviceCount = serviceCount;
        this.services = services;
        this.skillCount = skillCount;
        this.skills = skills;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(int serviceCount) {
        this.serviceCount = serviceCount;
    }

    public int getSkillCount() {
        return skillCount;
    }

    public void setSkillCount(int skillCount) {
        this.skillCount = skillCount;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getDisplayUsername() {
        return displayUsername;
    }

    public void setDisplayUsername(String displayUsername) {
        this.displayUsername = displayUsername;
    }
}
