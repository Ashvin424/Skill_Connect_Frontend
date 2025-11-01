package com.ashvinprajapati.skillconnect.ViewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ashvinprajapati.skillconnect.repository.ServiceRepository;

public class ServiceViewModelFactory implements ViewModelProvider.Factory {
    private ServiceRepository serviceRepository;

    public ServiceViewModelFactory(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ServiceViewModel.class)){
            return (T) new ServiceViewModel(serviceRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
