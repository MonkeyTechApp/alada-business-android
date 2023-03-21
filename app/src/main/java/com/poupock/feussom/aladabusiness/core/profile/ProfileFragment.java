package com.poupock.feussom.aladabusiness.core.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.core.menu.MenuItemActivity;
import com.poupock.feussom.aladabusiness.core.table.TableActivity;
import com.poupock.feussom.aladabusiness.core.table.TableListFragment;
import com.poupock.feussom.aladabusiness.core.user.UsersActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentProfileBinding;
import com.poupock.feussom.aladabusiness.util.Constant;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private FragmentProfileBinding binding;
    private final Gson gson = new Gson();

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cardOrder.setOnClickListener(this);
        binding.cardMenu.setOnClickListener(this);
        binding.cardUser.setOnClickListener(this);
        binding.cardTable.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.cardMenu){
            Intent intent = new Intent(requireContext(), MenuItemActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY,
                    gson.toJson(AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0)));
            startActivity(intent);
        }
        else if (view == binding.cardTable){
            Intent intent = new Intent(requireContext(), TableActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY,
                    gson.toJson(AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0)));
            startActivity(intent);
        }
        else if (view == binding.cardUser){
            Intent intent = new Intent(requireContext(), UsersActivity.class);
            intent.putExtra(Constant.ACTIVE_BUSINESS_KEY,
                    gson.toJson(AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0)));
            startActivity(intent);
        }
        else if (view == binding.cardOrder){
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.action_ProfileFragment_to_OrderListFragment);
        }
    }
}
