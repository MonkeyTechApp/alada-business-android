package com.poupock.feussom.aladabusiness.ui.fragment.orderdetail;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.core.profile.ProfileViewModel;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.OrderDetailFragmentBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderItemAdapter;

public class OrderDetailFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private OrderDetailFragmentBinding binding;

    public static OrderDetailFragment newInstance() {
        return new OrderDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        profileViewModel =
            new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        binding = OrderDetailFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.txtCode.setText(profileViewModel.getOrderMutableLiveData().getValue().getCode());
        binding.txtTable.setText(AppDataBase.getInstance(requireContext()).guestTableDao().getSpecificGuestTable(profileViewModel.getOrderMutableLiveData().getValue().getGuest_table_id()).getTitle());
        binding.txtTime.setText(profileViewModel.getOrderMutableLiveData().getValue().getCreated_at());
        binding.txtTotal.setText(profileViewModel.getOrderMutableLiveData().getValue().getTotal()+" CFA");

        binding.listDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listDetails.setAdapter(new OrderItemAdapter(requireContext(),
                profileViewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems(),
                new DialogCallback() {
                    @Override
                    public void onActionClicked(Object o, int action) {

                    }
                }));
    }
}
