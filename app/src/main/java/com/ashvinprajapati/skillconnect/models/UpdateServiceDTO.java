package com.ashvinprajapati.skillconnect.models;

import java.util.List;

public class UpdateServiceDTO {
    private String title;
    private String description;
    private String category;

    public UpdateServiceDTO() {
    }

    public UpdateServiceDTO(String category, String description, String title) {
        this.category = category;
        this.description = description;
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
