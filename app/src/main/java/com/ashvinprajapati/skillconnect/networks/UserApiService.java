package com.ashvinprajapati.skillconnect.networks;

import com.ashvinprajapati.skillconnect.models.FcmTokenRequest;
import com.ashvinprajapati.skillconnect.models.ProfileImageUploadResponseDTO;
import com.ashvinprajapati.skillconnect.models.ProfileResponse;
import com.ashvinprajapati.skillconnect.models.UpdateUserDTO;
import com.ashvinprajapati.skillconnect.models.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    @GET("users/{id}")
    Call<ProfileResponse> getUserById(
            @Path("id") Long userId
    );

    @GET("users/profile/me")
    Call<ProfileResponse> getMyProfile();

    @PUT("users/update-fcm-token")
    Call<Void> updateFcmToken(@Query("email") String email, @Query("token") String token);

    @PUT("users/profile/update")
    Call<User> userProfileUpdate(
            @Body UpdateUserDTO updateUserDTO,
            @Header("Authorization") String token
            );

    @Multipart
    @POST("users/profile/upload-image")
    Call<ProfileImageUploadResponseDTO> uploadProfileImage(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token
    );
}
