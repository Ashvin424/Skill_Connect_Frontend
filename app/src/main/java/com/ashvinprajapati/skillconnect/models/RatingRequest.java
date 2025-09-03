package com.ashvinprajapati.skillconnect.models;

public class RatingRequest {
    private Long reviewerId;
    private Long revieweeId;
    private int ratingValue;
    private String comment;

    public RatingRequest() {
    }

    public RatingRequest(String comment, int ratingValue, Long revieweeId, Long reviewerId) {
        this.comment = comment;
        this.ratingValue = ratingValue;
        this.revieweeId = revieweeId;
        this.reviewerId = reviewerId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Long getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }
}
