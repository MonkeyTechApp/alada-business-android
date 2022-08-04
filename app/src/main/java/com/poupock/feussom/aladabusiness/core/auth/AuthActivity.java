package com.poupock.feussom.aladabusiness.core.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.databinding.ActivityAuthBinding;
import com.poupock.feussom.aladabusiness.databinding.ActivityMenuItemBinding;

public class AuthActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_auth);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }
}
