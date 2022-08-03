package com.poupock.feussom.aladabusiness.core.table;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.MenuItem;

public class GuestTableViewModel extends ViewModel {

    private final MutableLiveData<GuestTable> guessTableMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEditMutableLiveData = new MutableLiveData<Boolean>();

    public LiveData<GuestTable> getGuestTableLiveData(){
        return guessTableMutableLiveData;
    }
    public LiveData<Boolean> getIsEditLiveData(){
        if(isEditMutableLiveData.getValue() == null)isEditMutableLiveData.setValue(false);
        return isEditMutableLiveData;
    }

    public void setGuestTableMutableLiveData(GuestTable guestTable){
        this.guessTableMutableLiveData.setValue(guestTable);
    }

    public void setIsEditMutableLiveData(boolean isEdit){
        this.isEditMutableLiveData.setValue(isEdit);
    }
}
