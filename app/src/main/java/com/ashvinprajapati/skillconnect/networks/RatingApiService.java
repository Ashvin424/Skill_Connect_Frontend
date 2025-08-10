package com.ashvinprajapati.skillconnect.networks;

import com.ashvinprajapati.skillconnect.models.Rating;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RatingApiService {

    @POST("ratings")
    Call<Rating> createRating(@Body Rating rating);

    @GET("ratings/user/{userId}")
    Call<List<Rating>> getRatingsByUserId(@Path("userId") Long userId);
}
