package com.poupock.feussom.aladabusiness.core.menu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;

public class MenuCreationViewModel extends ViewModel {

    private final MutableLiveData<MenuItem> menuItemMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<MenuItemCategory> menuItemCategoryMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<MenuItemCategory> selectedCategoryMutableLiveData = new MutableLiveData<>();

    public LiveData<MenuItemCategory> getMenuItemCategoryLiveData(){
        return menuItemCategoryMutableLiveData;
    }

    public LiveData<MenuItemCategory> getSelectedCategoryLiveData(){
        return selectedCategoryMutableLiveData;
    }

    public LiveData<MenuItem> getMenuItemLiveData(){
        if(menuItemMutableLiveData.getValue() == null) menuItemMutableLiveData.setValue(new MenuItem());
        return menuItemMutableLiveData;
    }

    public void setMenuItemCategoryMutableLiveData(MenuItemCategory menuItemCategory){
        this.menuItemCategoryMutableLiveData.setValue(menuItemCategory);
    }

    public void setSelectedCategoryMutableLiveData(MenuItemCategory menuItemCategory){
        this.selectedCategoryMutableLiveData.setValue(menuItemCategory);
    }

    public void setMenuItemMutableLiveData(MenuItem menuItem){
        this.menuItemMutableLiveData.setValue(menuItem);
    }
}
