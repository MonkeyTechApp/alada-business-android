package com.poupock.feussom.aladabusiness.core.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.business.BusinessViewModel;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.ActivityMenuItemBinding;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;
import com.poupock.feussom.aladabusiness.web.Fetch;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DataResponse;

import java.util.List;

public class MenuItemActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMenuItemBinding binding;
    MenuCreationViewModel menuCreationViewModel;
    private String tag = MenuItemActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivityMenuItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_item);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        menuCreationViewModel = new ViewModelProvider(this).get(MenuCreationViewModel.class);

        try{
            Business business = new Gson().fromJson(getIntent().getStringExtra(Constant.ACTIVE_BUSINESS_KEY), Business.class);
            if (business==null) onBackPressed();
            menuCreationViewModel.setBusinessMutableLiveData(business);
        }catch (NullPointerException ex){
            onBackPressed();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_item);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_menu_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.itemRefresh){
            fetchCategories(MenuItemActivity.this,
                    AppDataBase.getInstance(MenuItemActivity.this).businessDao().getAllBusinesses().get(0).getId());
        }
        return true;
    }

    void fetchCategories(Context context, int business_id){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.refreshing_categories_3_dots));
        new Fetch(this, ServerUrl.MENU_CATEGORY+"?business_id="+business_id+"&size=1000",
                new VolleyRequestCallback() {
                    @Override
                    public void onStart() {
                        dialog.show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        Gson gson = new Gson();
                        DataResponse dataResponse = gson.fromJson(response, DataResponse.class);
                        if (dataResponse.success){
                            long inserts = 0;
                            long updates = 0;
                            List<MenuItemCategory> categories = MenuItemCategory.buildListFromObjects(dataResponse.data);
                            for (int i = 0 ; i < categories.size(); i++){
                                if (AppDataBase.getInstance(context).menuItemCategoryDao().
                                        getSpecificMenuItemCategory(categories.get(i).getId()) == null){
                                    Log.i(tag, "Inserting : "+categories.get(i).getId());
                                    inserts = inserts +
                                    AppDataBase.getInstance(context).menuItemCategoryDao().insert(categories.get(i));
                                }else {
                                    Log.i(tag, "Updating : "+categories.get(i).getId());
                                    updates = updates + AppDataBase.getInstance(context).menuItemCategoryDao().update(categories.get(i));
                                }
                            }
                            Toast.makeText(context, getString(R.string.actualised)+" "+(updates + inserts)+" "+getString(R.string.menu_categories),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onJobFinished() {
                        dialog.dismiss();
                        fetchItems(context, business_id);
                    }
                }).execute();
    }

    void fetchItems(Context context, int business_id){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.refreshing_items_3_dots));
        new Fetch(this, ServerUrl.MENU_ITEM+"?business_id="+business_id+"&size=1000",
                new VolleyRequestCallback() {
                    @Override
                    public void onStart() {
                        dialog.show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        Gson gson = new Gson();
                        DataResponse dataResponse = gson.fromJson(response, DataResponse.class);
                        if (dataResponse.success){
                            long inserts = 0;
                            long updates = 0;
                            List<com.poupock.feussom.aladabusiness.util.MenuItem> items =
                                    com.poupock.feussom.aladabusiness.util.MenuItem.buildListFromObjects(dataResponse.data);
                            for (int i = 0 ; i < items.size(); i++){
                                if (AppDataBase.getInstance(context).menuItemDao().
                                        getSpecificMenuItem(items.get(i).getId()) == null){
                                    inserts = inserts + AppDataBase.getInstance(context).menuItemDao().insert(items.get(i));
                                }else {
                                    updates = updates + AppDataBase.getInstance(context).menuItemDao().update(items.get(i));
                                }
                            }
                            Toast.makeText(context, getString(R.string.actualised)+" "+(updates + inserts)+" "+getString(R.string.menus),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onJobFinished() {
                        dialog.dismiss();
                    }
                }).execute();
    }
}
