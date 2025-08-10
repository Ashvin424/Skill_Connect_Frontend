package com.ashvinprajapati.skillconnect.networks;

import com.ashvinprajapati.skillconnect.models.FcmTokenRequest;
import com.ashvinprajapati.skillconnect.models.ProfileResponse;
import com.ashvinprajapati.skillconnect.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    @GET("users/{id}")
    Call<User> getUserById(
            @Path("id") Long userId
    );

    @GET("users/profile/me")
    Call<ProfileResponse> getMyProfile();

    @PUT("users/update-fcm-token")
    Call<Void> updateFcmToken(@Query("email") String email, @Query("token") String token);

    @PUT("users/{id}")
    Call<User> updateUser(
            @Path("id") Long userId,
            @Body User user
    );
}
