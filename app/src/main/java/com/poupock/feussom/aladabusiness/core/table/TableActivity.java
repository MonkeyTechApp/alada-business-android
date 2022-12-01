package com.poupock.feussom.aladabusiness.core.table;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.databinding.ActivityTableBinding;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;

public class TableActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityTableBinding binding;
    GuestTableViewModel guestTableViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivityTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_table);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        guestTableViewModel = new ViewModelProvider(this).get(GuestTableViewModel.class);

        try{
            Business business = new Gson().fromJson(getIntent().getStringExtra(Constant.ACTIVE_BUSINESS_KEY), Business.class);
            if (business==null) onBackPressed();
            guestTableViewModel.setBusinessMutableLiveData(business);
        }catch (NullPointerException ex){
            onBackPressed();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_table);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
