package com.poupock.feussom.aladabusiness.core.dashboard.ui.home;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.core.business.BusinessActivity;
import com.poupock.feussom.aladabusiness.core.menu.ListMenuFragment;
import com.poupock.feussom.aladabusiness.core.menu.MenuItemActivity;
import com.poupock.feussom.aladabusiness.core.order.OrderActivity;
import com.poupock.feussom.aladabusiness.core.restaurant.BusinessCreationActivity;
import com.poupock.feussom.aladabusiness.core.table.TableActivity;
import com.poupock.feussom.aladabusiness.core.user.UsersActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentHomeBinding;
import com.poupock.feussom.aladabusiness.job.DownloadImage;
import com.poupock.feussom.aladabusiness.ui.adapter.BusinessAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.GuestTableOrdersAdapter;
import com.poupock.feussom.aladabusiness.ui.dialog.ListDialogFragment;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;
    AppDataBase appDataBase ;
    private String tag = HomeFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        appDataBase = AppDataBase.getInstance(requireContext());
        View root = binding.getRoot();

//        final TextView textView = binding.txtLegend;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.list.setLayoutManager(new GridLayoutManager(requireContext(), getResources().getInteger(R.integer.menu_grid_column_count)));
        Log.i(tag, "the tables are : "+new Gson().toJson(AppDataBase.getInstance(requireContext()).guestTableDao().getAllGuestTables()));
        binding.list.setAdapter(new GuestTableOrdersAdapter(requireContext(), AppDataBase.getInstance(requireContext()).guestTableDao().getAllGuestTables(),
                new ListItemClickCallback() {
                    @Override
                    public void onItemClickListener(Object o, boolean isLong) {
                        Log.i(tag, "The item clicked");
                        Intent intent = new Intent(requireContext(), OrderActivity.class);
                        intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, new Gson().toJson(appDataBase.businessDao().getAllBusinesses().get(0)));
                        Gson gson = new Gson();
                        GuestTable guestTable = GuestTable.getFromObject(o);
                        Log.i(tag, "The guest table is : "+guestTable.getId());
                        intent.putExtra(Constant.ACTIVE_TABLE_KEY, gson.toJson(guestTable));
                        startActivity(intent);
                    }
                }));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try{
//        new DownloadImage(requireContext(), "https://alada.poupock.com/img/dinner.png").execute();
            Log.i(tag, "The path is :" + ServerUrl.BASE_URL+AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getPath());
            if (!Methods.runtimeWritePermissions(requireActivity()) ){
                new DownloadImage(requireContext(),
                        ServerUrl.BASE_URL+AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getPath()).execute();
            }
        }catch (IndexOutOfBoundsException e){

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            binding.list.setLayoutManager(new GridLayoutManager(requireContext(), getResources().getInteger(R.integer.menu_grid_column_count)));
            Log.i(tag, "the tables are : "+new Gson().toJson(AppDataBase.getInstance(requireContext()).guestTableDao().getAllGuestTables()));
            binding.list.setAdapter(new GuestTableOrdersAdapter(requireContext(), AppDataBase.getInstance(requireContext()).guestTableDao().getAllGuestTables(),
                    new ListItemClickCallback() {
                        @Override
                        public void onItemClickListener(Object o, boolean isLong) {
                            Log.i(tag, "The item clicked");
                            Intent intent = new Intent(requireContext(), OrderActivity.class);
                            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, new Gson().toJson(appDataBase.businessDao().getAllBusinesses().get(0)));
                            Gson gson = new Gson();
                            GuestTable guestTable = GuestTable.getFromObject(o);
                            Log.i(tag, "The guest table is : "+guestTable.getId());
                            intent.putExtra(Constant.ACTIVE_TABLE_KEY, gson.toJson(guestTable));
                            startActivity(intent);
                        }
                    }));
        }catch (Exception e){}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        Gson gson = new Gson();
    }


}
