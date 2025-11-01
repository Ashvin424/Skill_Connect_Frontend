package com.ashvinprajapati.skillconnect.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ashvinprajapati.skillconnect.models.Booking;
import com.ashvinprajapati.skillconnect.models.BookingRequest;
import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.networks.BookingApiService;
import com.ashvinprajapati.skillconnect.networks.ServicesApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceRepository {
    private ServicesApiService servicesApiService;
    private BookingApiService bookingApiService;

    public ServiceRepository(BookingApiService bookingApiService, ServicesApiService servicesApiService) {
        this.bookingApiService = bookingApiService;
        this.servicesApiService = servicesApiService;
    }

    public LiveData<Service> getServiceById(Long serviceId) {
        MutableLiveData<Service> data = new MutableLiveData<>();

        servicesApiService.getServiceById(serviceId).enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                }
                else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Boolean> bookService(Long userId, Long serviceId) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        bookingApiService.createBooking(new BookingRequest(userId, serviceId)).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                data.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                data.setValue(false);
            }
        });
        return data;
    }
}
