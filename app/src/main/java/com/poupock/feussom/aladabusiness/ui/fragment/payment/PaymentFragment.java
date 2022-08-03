package com.poupock.feussom.aladabusiness.ui.fragment.payment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poupock.feussom.aladabusiness.databinding.PaymentFragmentBinding;

import com.poupock.feussom.aladabusiness.R;

public class PaymentFragment extends Fragment {

    private PaymentViewModel paymentViewModel;
    private PaymentFragmentBinding binding;

    public static PaymentFragment newInstance() {
        return new PaymentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        paymentViewModel =
            new ViewModelProvider(this).get(PaymentViewModel.class);
        binding = PaymentFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
