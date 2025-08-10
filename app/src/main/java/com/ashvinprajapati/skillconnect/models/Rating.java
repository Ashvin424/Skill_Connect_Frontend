package com.ashvinprajapati.skillconnect.models;

import java.time.LocalDateTime;

public class Rating {
    public Rating(){}
    public Rating(String comment, Long id, int ratingValue, User reviewee, User reviewer, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.comment = comment;
        this.id = id;
        this.ratingValue = ratingValue;
        this.reviewee = reviewee;
        this.reviewer = reviewer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private Long id;

    private int ratingValue;

    private String comment;

    private User reviewer;

    private User reviewee;

    private LocalDateTime createdAt; // Automatically set to the current date and time when the entity is created
    private LocalDateTime updatedAt; // Automatically set to the current date and time when the entity is updated

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public User getReviewee() {
        return reviewee;
    }

    public void setReviewee(User reviewee) {
        this.reviewee = reviewee;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }
}
