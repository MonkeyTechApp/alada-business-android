package com.poupock.feussom.aladabusiness.core.dashboard.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.poupock.feussom.aladabusiness.ui.adapter.BusinessAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.GuestTableOrdersAdapter;
import com.poupock.feussom.aladabusiness.ui.dialog.ListDialogFragment;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;
    AppDataBase appDataBase ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        appDataBase = AppDataBase.getInstance(requireContext());
        View root = binding.getRoot();

        final TextView textView = binding.txtLegend;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.list.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.list.setAdapter(new GuestTableOrdersAdapter(requireContext(), AppDataBase.getInstance(requireContext()).guestTableDao().getAllGuestTables(),
                new ListItemClickCallback() {
                    @Override
                    public void onItemClickListener(Object o, boolean isLong) {
                        Intent intent = new Intent(requireContext(), OrderActivity.class);
                        intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, new Gson().toJson(appDataBase.businessDao().getAllBusinesses().get(0)));
                        Gson gson = new Gson();
                        GuestTable guestTable = GuestTable.getFromObject(o);
                        if (guestTable.getOrders()!=null){
                            if (guestTable.getOrders().size() > 1){
                                // Show list of orders for selection.
                                DialogFragment listDialogFragment = ListDialogFragment.newInstance(Order.class.getSimpleName(), guestTable.getId()+"",
                                        new ListItemClickCallback() {
                                            @Override
                                            public void onItemClickListener(Object o, boolean isLong) {

                                            }
                                        });
                            }
                            else {
                                intent.putExtra(Constant.ACTIVE_TABLE_KEY, gson.toJson(guestTable));
                            }
                        }
                        startActivity(intent);
                    }
                }));

        binding.cardOrder.setOnClickListener(this);
        binding.cardMenu.setOnClickListener(this);
        binding.cardUser.setOnClickListener(this);
        binding.cardTable.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        Gson gson = new Gson();
        if (view == binding.cardMenu){
            Intent intent = new Intent(requireContext(), MenuItemActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, gson.toJson(appDataBase.businessDao().getAllBusinesses().get(0)));
            startActivity(intent);
        }
        else if (view == binding.cardTable){
            Intent intent = new Intent(requireContext(), TableActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, gson.toJson(appDataBase.businessDao().getAllBusinesses().get(0)));
            startActivity(intent);
        }
        else if (view == binding.cardUser){
            Intent intent = new Intent(requireContext(), UsersActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, gson.toJson(appDataBase.businessDao().getAllBusinesses().get(0)));
            startActivity(intent);
        }
        else if (view == binding.cardOrder){
            Intent intent = new Intent(requireContext(), OrderActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, gson.toJson(appDataBase.businessDao().getAllBusinesses().get(0)));
            startActivity(intent);
        }
    }
}
