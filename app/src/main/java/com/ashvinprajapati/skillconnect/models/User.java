package com.ashvinprajapati.skillconnect.models;



public class User {

    public User(){}
    public User(String bio,String username, String email, Long id, String location, String name, String profileImageUrl, String serviceMode, String skills, String fcmToken) {
        this.bio = bio;
        this.email = email;
        this.id = id;
        this.location = location;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.serviceMode = serviceMode;
        this.skills = skills;
        this.fcmToken = fcmToken;
        this.username = username;
    }

    private Long id;
    private String name;

    private String username;
    private String email;
    private String bio;
    private String location;
    private String skills;
    private String profileImageUrl;
    private String serviceMode;
    private String fcmToken;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getBio() {
        return bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
