package com.poupock.feussom.aladabusiness.ui.fragment.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.Order;

public class OrderViewModel extends ViewModel {

    private MutableLiveData<GuestTable> guestTableMutableLiveData;
    private MutableLiveData<Order> orderMutableLiveData;

    public OrderViewModel() {
        guestTableMutableLiveData = new MutableLiveData<>();
        orderMutableLiveData = new MutableLiveData<>();
    }

    public void setGuestTableMutableLiveData(GuestTable guestTable){
        guestTableMutableLiveData.setValue(guestTable);
    }

    public void setOrderMutableLiveData(Order order){
        orderMutableLiveData.setValue(order);
    }

    public LiveData<Order> getOrderMutableLiveData(){return orderMutableLiveData;}

    public LiveData<GuestTable> getGuestTableMutableLiveData(){ return guestTableMutableLiveData; }
}
