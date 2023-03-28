package com.poupock.feussom.aladabusiness.core.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.poupock.feussom.aladabusiness.util.Order;

public class ProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<Order> orderMutableLiveData = new MutableLiveData<>();

    public ProfileViewModel() {
        orderMutableLiveData = new MutableLiveData<>();
    }

    public void setOrderMutableLiveData(Order order){
        orderMutableLiveData.setValue(order);
    }
    public LiveData<Order> getOrderMutableLiveData(){return orderMutableLiveData;}
}
