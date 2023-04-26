package com.poupock.feussom.aladabusiness.core.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.SettingActivity;
import com.poupock.feussom.aladabusiness.callback.ProcessCallback;
import com.poupock.feussom.aladabusiness.core.auth.AuthActivity;
import com.poupock.feussom.aladabusiness.core.setting.SettingsActivity;
import com.poupock.feussom.aladabusiness.core.table.GuestTableViewModel;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.ActivityProfileBinding;
import com.poupock.feussom.aladabusiness.ui.dialog.OrderDetailDialogFragment;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_profile);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        try{
            Business business = new Gson().fromJson(getIntent().getStringExtra(Constant.ACTIVE_BUSINESS_KEY), Business.class);
            if (business==null) onBackPressed();
        }catch (NullPointerException ex){
            onBackPressed();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_profile);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
//            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which){
//                        case DialogInterface.BUTTON_POSITIVE:
////                            User.disconnectConnectedUser(ProfileActivity.this);
////                            User.storeToken("", ProfileActivity.this);
////
////                            AppDataBase.getInstance(ProfileActivity.this).orderItemDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).courseDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).orderDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).menuItemDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).menuItemCategoryDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).guestTableDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).internalPointDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).businessDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).roleDao().emptyTable();
////                            AppDataBase.getInstance(ProfileActivity.this).userDao().emptyTable();
////                            Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
//
//                            break;
//                        case DialogInterface.BUTTON_NEGATIVE:
//                            dialog.dismiss();
//                    }
//                }
//            };
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
//            builder.setMessage(getString(R.string.confirm_logout)).setPositiveButton(getString(R.string.yes), dialogClickListener)
//                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();

        }
        return true;
    }
}
