package com.ashvinprajapati.skillconnect.models;

public class RegisterRequest {

    private String name;
    private String username;
    private String email;
    private String password;
    private String location;
    private String skills;
    private String serviceMode;

    public RegisterRequest(String email, String location, String name, String password, String serviceMode, String skills, String username) {
        this.email = email;
        this.location = location;
        this.name = name;
        this.password = password;
        this.serviceMode = serviceMode;
        this.skills = skills;
        this.username = username;
    }

    public RegisterRequest() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
