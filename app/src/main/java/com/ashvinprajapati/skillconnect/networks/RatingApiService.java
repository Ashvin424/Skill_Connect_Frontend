package com.ashvinprajapati.skillconnect.networks;

import com.ashvinprajapati.skillconnect.models.PagedResponse;
import com.ashvinprajapati.skillconnect.models.Rating;
import com.ashvinprajapati.skillconnect.models.RatingRequest;
import com.ashvinprajapati.skillconnect.models.RatingResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RatingApiService {

    @POST("ratings")
    Call<Rating> createRating(@Body RatingRequest ratingRequest);

    @GET("ratings/user/{userId}")
    Call<PagedResponse<RatingResponse>> getRatingForUser(
            @Path("userId") Long userId,
            @Query("page") int page,
            @Query("size") int size
    );
}
