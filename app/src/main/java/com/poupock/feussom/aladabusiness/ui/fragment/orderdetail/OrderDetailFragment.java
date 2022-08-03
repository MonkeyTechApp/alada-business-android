package com.poupock.feussom.aladabusiness.ui.fragment.orderdetail;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poupock.feussom.aladabusiness.databinding.OrderDetailFragmentBinding;

public class OrderDetailFragment extends Fragment {

    private OrderDetailViewModel orderDetailViewModel;
    private OrderDetailFragmentBinding binding;

    public static OrderDetailFragment newInstance() {
        return new OrderDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        orderDetailViewModel =
            new ViewModelProvider(this).get(OrderDetailViewModel.class);
        binding = OrderDetailFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
