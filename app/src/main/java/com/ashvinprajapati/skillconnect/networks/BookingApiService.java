package com.ashvinprajapati.skillconnect.networks;

import com.ashvinprajapati.skillconnect.models.Booking;
import com.ashvinprajapati.skillconnect.models.BookingRequest;
import com.ashvinprajapati.skillconnect.models.BookingResponse;
import com.ashvinprajapati.skillconnect.models.BookingStatusUpdateRequest;
import com.ashvinprajapati.skillconnect.models.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingApiService {
    @POST("bookings")
    Call<Booking> createBooking(@Body BookingRequest request);

    @GET("bookings/user/{userId}")
    Call<List<Booking>> getBookingsByUserId(@Path("userId") Long userId);

    @GET("bookings/service/{serviceId}")
    Call<List<Booking>> getBookingsByServiceId(@Path("serviceId") Long serviceId);

    @GET("bookings/service-provider/{providerId}")
    Call<PagedResponse<BookingResponse>> getBookingsByProvider(
            @Path("providerId") Long providerId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("bookings/requested-by/{userId}")
    Call<PagedResponse<BookingResponse>> getBookingsByRequester(
            @Path("userId") Long userId,
            @Query("page") int page,
            @Query("size") int size,
            @Header("Authorization") String token
    );

    @PUT("bookings/{id}")
    Call<Booking> updateBookingStatus(
            @Path("id") Long bookingId,
            @Body BookingStatusUpdateRequest request,
            @Header("Authorization") String token
    );
}
