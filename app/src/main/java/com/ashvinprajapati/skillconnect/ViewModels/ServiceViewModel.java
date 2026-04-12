package com.ashvinprajapati.skillconnect.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.ashvinprajapati.skillconnect.models.Service;
import com.ashvinprajapati.skillconnect.repository.ServiceRepository;

public class ServiceViewModel extends ViewModel {
    private ServiceRepository serviceRepository;
    private MutableLiveData<Service> serviceLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> bookingLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();

    private Observer<Service> serviceObserver;
    private Observer<Boolean> bookingObserver;
    private Observer<Boolean> deleteObserver;

    private LiveData<Service> serviceSource;
    private LiveData<Boolean> bookingSource;
    private LiveData<Boolean> deleteSource;

    public ServiceViewModel(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public void loadService(Long serviceId) {
        // Remove previous observer if exists
        if (serviceSource != null && serviceObserver != null) {
            serviceSource.removeObserver(serviceObserver);
        }
        serviceObserver = service -> serviceLiveData.setValue(service);
        serviceSource = serviceRepository.getServiceById(serviceId);
        serviceSource.observeForever(serviceObserver);
    }

    public LiveData<Service> getService() {
        return serviceLiveData;
    }

    public void bookService(Long userId, Long serviceId) {
        // Remove previous observer if exists
        if (bookingSource != null && bookingObserver != null) {
            bookingSource.removeObserver(bookingObserver);
        }
        bookingObserver = status -> bookingLiveData.setValue(status);
        bookingSource = serviceRepository.bookService(userId, serviceId);
        bookingSource.observeForever(bookingObserver);
    }

    public LiveData<Boolean> getBookingStatus() {
        return bookingLiveData;
    }

    public LiveData<Boolean> getDeleteResult() {
        return deleteResult;
    }

    public void deleteService(Long serviceId) {
        // Remove previous observer if exists
        if (deleteSource != null && deleteObserver != null) {
            deleteSource.removeObserver(deleteObserver);
        }
        deleteObserver = deleteResult::setValue;
        deleteSource = serviceRepository.deleteService(serviceId);
        deleteSource.observeForever(deleteObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (serviceSource != null && serviceObserver != null) {
            serviceSource.removeObserver(serviceObserver);
        }
        if (bookingSource != null && bookingObserver != null) {
            bookingSource.removeObserver(bookingObserver);
        }
        if (deleteSource != null && deleteObserver != null) {
            deleteSource.removeObserver(deleteObserver);
        }
    }
}