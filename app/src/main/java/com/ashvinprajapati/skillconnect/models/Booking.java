package com.ashvinprajapati.skillconnect.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Booking {
    public Booking(){}

    public Booking(String cancelledAt, String confirmedAt, Long id, String requestedAt, User requestedBy, Service service, BookingStatus status, String updatedAt) {
        this.cancelledAt = cancelledAt;
        this.confirmedAt = confirmedAt;
        this.id = id;
        this.requestedAt = requestedAt;
        this.requestedBy = requestedBy;
        this.service = service;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    private Long id;

    private Service service;

    private User requestedBy;

    private BookingStatus status; // e.g., "pending", "confirmed", "cancelled"


    @SerializedName("requestedAt")
    private String requestedAt; // Timestamp when the booking was requested
    @SerializedName("confirmedAt")
    private String confirmedAt; // Timestamp when the booking was confirmed, if applicable
    @SerializedName("cancelledAt")
    private String cancelledAt;
    @SerializedName("updatedAt")
    private String updatedAt;

    public String getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(String cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(String confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}

