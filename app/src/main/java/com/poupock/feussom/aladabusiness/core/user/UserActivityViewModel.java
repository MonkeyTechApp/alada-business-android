package com.poupock.feussom.aladabusiness.core.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;
import com.poupock.feussom.aladabusiness.util.Role;
import com.poupock.feussom.aladabusiness.util.User;

public class UserActivityViewModel extends ViewModel {
    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Role> roleMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEditMutableLiveData = new MutableLiveData<Boolean>();


    public LiveData<User> getUserLiveData(){
        return userMutableLiveData;
    }

    public void setUserMutableLiveData(User datum){
        this.userMutableLiveData.setValue(datum);
    }

    public LiveData<Role> getRoleLiveData(){
        return this.roleMutableLiveData;
    }

    public void setRoleMutableLiveData(Role datum){
        this.roleMutableLiveData.setValue(datum);
    }

    public void setIsEditMutableLiveData(boolean isEdit){
        this.isEditMutableLiveData.setValue(isEdit);
    }
    public LiveData<Boolean> getIsEditLiveData(){
        if(isEditMutableLiveData.getValue() == null)isEditMutableLiveData.setValue(false);
        return isEditMutableLiveData;
    }

}
