package com.poupock.feussom.aladabusiness.core.order;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.databinding.ActivityOrderBinding;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.GuestTable;

public class OrderActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityOrderBinding binding;
    OrderViewModel orderTableViewModel;
    private String tag = OrderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_AlAdaBusiness);
        super.onCreate(savedInstanceState);

        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_order);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        orderTableViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

//        try{
            Gson gson = new Gson();
            Business business = gson.fromJson(getIntent().getStringExtra(Constant.ACTIVE_BUSINESS_KEY), Business.class);
            if (business==null) onBackPressed();
            String s = getIntent().getStringExtra(Constant.ACTIVE_TABLE_KEY);
            Log.i(tag, s);
            if (s != null){
                if (s.length() > 0){
                    GuestTable guestTable = gson.fromJson(s, GuestTable.class);
                    Log.i(tag, "The table is defined");
                    orderTableViewModel.setGuestTableMutableLiveData(guestTable);
                }
            }
//        }catch (NullPointerException ex){
//            Log.e(tag, ex.getMessage());
//            onBackPressed();
//        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_order);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
