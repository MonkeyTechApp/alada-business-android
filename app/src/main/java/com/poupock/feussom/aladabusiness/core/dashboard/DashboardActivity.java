package com.poupock.feussom.aladabusiness.core.dashboard;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.core.profile.ProfileActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.ActivityDashboardBinding;
import com.poupock.feussom.aladabusiness.job.OrderService;
import com.poupock.feussom.aladabusiness.job.OrderSyncService;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.User;

public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityDashboardBinding binding;
    private String tag = DashboardActivity.class.getSimpleName();

    TextView txtUser, txtIdentifier, txtRole;
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//
//        txtIdentifier = binding.navView.getHeaderView(0).findViewById(R.id.txtIdentifier);
//        txtUser = binding.navView.getHeaderView(0).findViewById(R.id.txtUser);
//        txtRole = binding.navView.getHeaderView(0).findViewById(R.id.txtRole);

        User currentUser = User.currentUser(this);

        if (currentUser.getBusinesses().size() > 0){
//            txtRole.setText(currentUser.getRoles().get(0).getName());
//            txtRole.setVisibility(View.VISIBLE);
//            launchService();
            startService(this);
            Methods.runtimeWritePermissions(this);
        }
        else {
            txtRole.setVisibility(View.GONE);
            txtUser.setText(currentUser.getName());
            txtIdentifier.setText(currentUser.getEmail());
        }
    }

    private void launchService() {
        ComponentName componentName = new ComponentName(this, OrderSyncService.class);

        JobInfo jobInfo = new JobInfo.Builder(OrderSyncService.JOB_ID, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        int resultCode = jobScheduler.schedule(jobInfo);

        if (resultCode == JobScheduler.RESULT_SUCCESS)
            Log.i(tag, "Job scheduled");
        else Log.i(tag, "Job scheduling failed");
    }

    private void startService(Context context){
        Intent serviceIntent = new Intent(context, OrderService.class);
        serviceIntent.putExtra(User.LAST_SYNC_TIME, User.getLastSyncTime(context));
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void stopService(Context context){
        Intent serviceIntent = new Intent(context, OrderService.class);
        stopService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_profile){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY,
                    new Gson().toJson(AppDataBase.getInstance(this).businessDao().getAllBusinesses().get(0)));
            startActivity(intent);
        }
        return true;
    }

    //    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

}