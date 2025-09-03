package com.ashvinprajapati.skillconnect.models;

import java.time.LocalDateTime;

public class RatingResponse {
    private Long id;
    private int ratingValue;
    private String comment;
    private Long reviewerId;
    private String reviewerName;
    private String createdAt;
    private String reviewerProfileImageUrl;

    public RatingResponse() {
    }

    public RatingResponse(String comment, String createdAt, Long id, int ratingValue, Long reviewerId, String reviewerName, String reviewerProfileImageUrl) {
        this.comment = comment;
        this.createdAt = createdAt;
        this.id = id;
        this.ratingValue = ratingValue;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
    }

    public String getReviewerProfileImageUrl() {
        return reviewerProfileImageUrl;
    }

    public void setReviewerProfileImageUrl(String reviewerProfileImageUrl) {
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
}
