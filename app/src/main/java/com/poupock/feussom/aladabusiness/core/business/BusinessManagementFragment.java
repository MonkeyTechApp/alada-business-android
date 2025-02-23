package com.poupock.feussom.aladabusiness.core.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.core.menu.MenuItemActivity;
import com.poupock.feussom.aladabusiness.core.table.TableActivity;
import com.poupock.feussom.aladabusiness.core.user.UsersActivity;
import com.poupock.feussom.aladabusiness.databinding.FragmentBusinessManagementBinding;
import com.poupock.feussom.aladabusiness.util.Constant;

public class BusinessManagementFragment extends Fragment implements View.OnClickListener {

private FragmentBusinessManagementBinding binding;
    private BusinessViewModel businessViewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

      binding = FragmentBusinessManagementBinding.inflate(inflater, container, false);
      return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(BusinessManagementFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

        binding.txtMenu.setOnClickListener(this);
        binding.txtOrder.setOnClickListener(this);
        binding.txtTable.setOnClickListener(this);
        binding.txtUser.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view == binding.txtMenu){
            Intent intent = new Intent(requireContext(), MenuItemActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, new Gson().toJson(businessViewModel.getBusinessLiveData().getValue()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(view == binding.txtUser){
            Intent intent = new Intent(requireContext(), UsersActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, new Gson().toJson(businessViewModel.getBusinessLiveData().getValue()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(view == binding.txtTable){
            Intent intent = new Intent(requireContext(), TableActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY, new Gson().toJson(businessViewModel.getBusinessLiveData().getValue()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
