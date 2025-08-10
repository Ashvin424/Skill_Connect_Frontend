package com.ashvinprajapati.skillconnect.networks;

import com.ashvinprajapati.skillconnect.models.Booking;
import com.ashvinprajapati.skillconnect.models.BookingRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingApiService {
    @POST("bookings")
    Call<Booking> createBooking(@Body BookingRequest request);

    @GET("bookings/user/{userId}")
    Call<List<Booking>> getBookingsByUserId(@Path("userId") Long userId);

    @GET("bookings/service/{serviceId}")
    Call<List<Booking>> getBookingsByServiceId(@Path("serviceId") Long serviceId);
}
