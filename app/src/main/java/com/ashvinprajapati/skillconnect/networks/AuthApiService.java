package com.ashvinprajapati.skillconnect.networks;

import com.ashvinprajapati.skillconnect.models.AuthRequest;
import com.ashvinprajapati.skillconnect.models.AuthResponse;
import com.ashvinprajapati.skillconnect.models.RegisterRequest;
import com.ashvinprajapati.skillconnect.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AuthApiService {

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest registerRequest);

    @GET("auth/me")
    Call<User> getMe();

    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest authRequest);

    @PUT("/users/update-fcm-token")
    Call<Void> updateFcmToken(@Query("email") String email, @Query("token") String fcmToken);

}
