package com.poupock.feussom.aladabusiness.core.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.databinding.ActivityDashboardBinding;
import com.poupock.feussom.aladabusiness.util.User;

public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;
    private String tag = DashboardActivity.class.getSimpleName();

    TextView txtUser, txtIdentifier, txtRole;
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarDashboard.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_user, R.id.nav_menu,
                R.id.nav_orders, R.id.nav_table)
                .setOpenableLayout(drawer)
                .build();
        Log.i(tag,"The User connected : "+ new Gson().toJson(User.currentUser(this)));
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        txtIdentifier = binding.navView.getHeaderView(0).findViewById(R.id.txtIdentifier);
        txtUser = binding.navView.getHeaderView(0).findViewById(R.id.txtUser);
        txtRole = binding.navView.getHeaderView(0).findViewById(R.id.txtRole);

        User currentUser = User.currentUser(this);

        if (currentUser.getBusinesses().size() > 0){
            txtRole.setText(currentUser.getRoles().get(0).getName());
            txtRole.setVisibility(View.VISIBLE);
        }
        else {
            txtRole.setVisibility(View.GONE);
            txtUser.setText(currentUser.getName());
            txtIdentifier.setText(currentUser.getEmail());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}