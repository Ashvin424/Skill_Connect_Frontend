package com.ashvinprajapati.skillconnect.models;

public class BookingRequest {
    private Long userId;
    private Long serviceId;

    public BookingRequest(Long userId, Long serviceId) {
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
