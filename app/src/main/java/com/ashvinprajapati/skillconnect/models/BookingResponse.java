package com.ashvinprajapati.skillconnect.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private Long userId;
    private Long serviceId;
    private String status;
    @SerializedName("requestedAt")
    private String requestedAt; // Timestamp when the booking was requested
    @SerializedName("confirmedAt")
    private String confirmedAt; // Timestamp when the booking was confirmed, if applicable
    @SerializedName("cancelledAt")
    private String cancelledAt;
    @SerializedName("updatedAt")
    private String updatedAt;

}
