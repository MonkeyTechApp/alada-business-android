package com.poupock.feussom.aladabusiness.core.table;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.MenuItem;

public class GuestTableViewModel extends ViewModel {

    private final MutableLiveData<GuestTable> guessTableMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<GuestTable> selectedGuestTableMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEditMutableLiveData = new MutableLiveData<Boolean>();
    private final MutableLiveData<Business> businessMutableLiveData = new MutableLiveData<>();

    public LiveData<Business> getBusinessLiveData(){
        return businessMutableLiveData;
    }

    public void setBusinessMutableLiveData(Business business){
        this.businessMutableLiveData.setValue(business);
    }


    public LiveData<GuestTable> getGuestTableLiveData(){
        return guessTableMutableLiveData;
    }
    public LiveData<GuestTable> getSelectedGuestTableLiveData(){
        return selectedGuestTableMutableLiveData;
    }

    public LiveData<Boolean> getIsEditLiveData(){
        if(isEditMutableLiveData.getValue() == null)isEditMutableLiveData.setValue(false);
        return isEditMutableLiveData;
    }

    public void setGuestTableMutableLiveData(GuestTable guestTable){
        this.guessTableMutableLiveData.setValue(guestTable);
    }

    public void setSelectedGuestTableMutableLiveData(GuestTable guestTable){
        this.selectedGuestTableMutableLiveData.setValue(guestTable);
    }

    public void setIsEditMutableLiveData(boolean isEdit){
        this.isEditMutableLiveData.setValue(isEdit);
    }
}
