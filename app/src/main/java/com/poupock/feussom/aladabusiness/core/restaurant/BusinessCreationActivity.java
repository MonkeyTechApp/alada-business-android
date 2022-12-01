package com.poupock.feussom.aladabusiness.core.restaurant;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.poupock.feussom.aladabusiness.databinding.ActivityBusinessCreationBinding;

import com.poupock.feussom.aladabusiness.R;

public class BusinessCreationActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityBusinessCreationBinding binding;
    BusinessCreationViewModel viewModel;
    private static final String TAG = BusinessCreationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivityBusinessCreationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(BusinessCreationViewModel.class);
        viewModel.getBusinessLiveData().observe(this, item -> {
            Log.i(TAG, "The business variable has been updated!");
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_business_creation);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_business_creation);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
