package com.poupock.feussom.aladabusiness.core.dashboard.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.core.business.BusinessActivity;
import com.poupock.feussom.aladabusiness.core.restaurant.BusinessCreationActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentHomeBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.BusinessAdapter;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.txtLegend;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        AppDataBase appDataBase = AppDataBase.getInstance(requireContext());
        List<Business> businesses = appDataBase.businessDao().getAllBusinesses();

        if(businesses != null){
            binding.txtLegend.setVisibility(View.GONE);
            binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.list.setAdapter(new BusinessAdapter(requireContext(), businesses));
        }
        else {

        }
        binding.btnCreate.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnCreate){
            Intent intent = new Intent(requireContext(), BusinessCreationActivity.class);
            startActivity(intent);
        }
    }
}
