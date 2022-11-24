package com.poupock.feussom.aladabusiness.core.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;

import com.poupock.feussom.aladabusiness.core.dashboard.DashboardActivity;
import com.poupock.feussom.aladabusiness.HomeActivity;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.core.restaurant.BusinessCreationActivity;
import com.poupock.feussom.aladabusiness.databinding.ActivityAuthBinding;
import com.poupock.feussom.aladabusiness.databinding.ActivityMenuItemBinding;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.List;

public class AuthActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(User.currentUser(this) != null){
            Intent intent = new Intent(this, DashboardActivity.class);;

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            setTheme(R.style.Theme_AlAdaBusiness);

            binding = ActivityAuthBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

//        setSupportActionBar(binding.toolbar);

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_auth);
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }

    }
}
