package com.ashvinprajapati.skillconnect.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private Long userId;
    private Long serviceId;
    private Long serviceProviderId;
    private String serviceTitle;
    private String requestedByName;
    private String serviceProviderName;
    private BookingStatus status;
    @SerializedName("requestedAt")
    private String requestedAt; // Timestamp when the booking was requested
    @SerializedName("confirmedAt")
    private String confirmedAt; // Timestamp when the booking was confirmed, if applicable
    @SerializedName("cancelledAt")
    private String cancelledAt;
    @SerializedName("updatedAt")
    private String updatedAt;

    public BookingResponse(String cancelledAt,String serviceProviderName ,String confirmedAt, Long id, String requestedAt, String requestedByName, Long serviceId, Long serviceProviderId, String serviceTitle, BookingStatus status, String updatedAt, Long userId) {
        this.cancelledAt = cancelledAt;
        this.confirmedAt = confirmedAt;
        this.id = id;
        this.requestedAt = requestedAt;
        this.requestedByName = requestedByName;
        this.serviceId = serviceId;
        this.serviceProviderId = serviceProviderId;
        this.serviceTitle = serviceTitle;
        this.serviceProviderName = serviceProviderName;
        this.status = status;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    public BookingResponse() {
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public Long getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

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

    public String getRequestedByName() {
        return requestedByName;
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
