package com.ashvinprajapati.skillconnect.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.repository.ServiceRepository;

public class ServiceViewModel extends ViewModel {
    private ServiceRepository serviceRepository;
    private MutableLiveData<Service> serviceLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> bookingLiveData = new MutableLiveData<>();

    public ServiceViewModel(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    //load service details
    public void loadService(Long serviceId) {
        serviceRepository.getServiceById(serviceId).observeForever(service -> {
            serviceLiveData.setValue(service);
        });
    }

    public LiveData<Service> getService(){
        return serviceLiveData;
    }

    //Book Service
    public void bookService(Long userId, Long serviceId) {
        serviceRepository.bookService(userId, serviceId).observeForever(status ->{
            bookingLiveData.setValue(status);
        });
    }

    public LiveData<Boolean> getBookingStatus(){
        return bookingLiveData;
    }
}
