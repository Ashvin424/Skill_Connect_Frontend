package com.ashvinprajapati.skillconnect.models;

public class UpdateUserDTO {
    private Long id;
    private String name;
    private String username;
    private String bio;
    private String location;
    private String skills; // comma-separated list of skills
    private String profileImageUrl;
    private String serviceMode; // this could be "online", "offline", or "both"

    public UpdateUserDTO() {
    }

    public UpdateUserDTO(String bio, Long id, String location, String name, String profileImageUrl, String serviceMode, String skills, String username) {
        this.bio = bio;
        this.id = id;
        this.location = location;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.serviceMode = serviceMode;
        this.skills = skills;
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(String serviceMode) {
        this.serviceMode = serviceMode;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
