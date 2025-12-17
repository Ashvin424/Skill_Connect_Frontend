package com.ashvinprajapati.skillconnect.networks;

import com.ashvinprajapati.skillconnect.models.ImageUploadResponse;
import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.models.UpdateServiceDTO;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicesApiService {
    @GET("services")
    Call<List<Service>> getAllServices();

    @GET("services/{id}")
    Call<Service> getServiceById(@Path("id") Long id);



    @POST("services")
    Call<Service> createService(@Body Service service);

    @Multipart
    @POST("services/{id}/upload-images")
    Call<ImageUploadResponse> uploadServiceImages(
            @Path("id") Long serviceId,
            @Part List<MultipartBody.Part> files
    );

    @GET("services/search")
    Call<List<Service>> searchServices(
            @Query("searchBy") String searchBy,
            @Query("query") String query
    );

    @PUT("services/{id}")
    Call<UpdateServiceDTO> updateService(
            @Path("id") Long serviceId,
            @Body UpdateServiceDTO serviceDTO,
            @Header("Authorization") String token
    );

    @DELETE("services/{id}")
    Call<Void> deleteService(
            @Path("id") Long serviceId,
            @Header("Authorization") String token
    );

    @PUT("services/{id}/deactivate")
    Call<Void> deactivateService(
            @Path("id") Long serviceId,
            @Header("Authorization") String token
    );

}
