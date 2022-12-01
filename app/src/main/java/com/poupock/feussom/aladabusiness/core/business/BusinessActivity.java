package com.poupock.feussom.aladabusiness.core.business;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.core.restaurant.BusinessCreationViewModel;
import com.poupock.feussom.aladabusiness.databinding.ActivityBusinessBinding;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.util.Business;

public class BusinessActivity extends AppCompatActivity {

    public static final String ACTIVE_BUSINESS = "ACTIVE_BUSINESS";
    private AppBarConfiguration appBarConfiguration;
    private ActivityBusinessBinding binding;
    BusinessViewModel businessViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivityBusinessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_business);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        businessViewModel = new ViewModelProvider(this).get(BusinessViewModel.class);

        try{
            Business business = new Gson().fromJson(getIntent().getStringExtra(ACTIVE_BUSINESS), Business.class);
            if (business==null) onBackPressed();
            businessViewModel.setBusinessMutableLiveData(business);
        }catch (NullPointerException ex){
            onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_business);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
