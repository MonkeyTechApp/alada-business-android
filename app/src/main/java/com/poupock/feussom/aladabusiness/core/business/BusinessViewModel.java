package com.poupock.feussom.aladabusiness.core.business;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.poupock.feussom.aladabusiness.util.Business;

public class BusinessViewModel extends ViewModel {

    private final MutableLiveData<Business> businessMutableLiveData = new MutableLiveData<>();

    public LiveData<Business> getBusinessLiveData(){
        return businessMutableLiveData;
    }

    public void setBusinessMutableLiveData(Business business){
        this.businessMutableLiveData.setValue(business);
    }
}
