package com.poupock.feussom.aladabusiness.core.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.core.menu.MenuItemActivity;
import com.poupock.feussom.aladabusiness.core.table.TableActivity;
import com.poupock.feussom.aladabusiness.core.user.UsersActivity;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentProfileBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderAdapter;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.List;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private FragmentProfileBinding binding;
    private final Gson gson = new Gson();
    private String tag = ProfileFragment.class.getSimpleName();

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String role = "";
        int role_id = 0;
        AppDataBase dataBase = AppDataBase.getInstance(requireContext());
        List<User> users = dataBase.userDao().getAllUsers();
        for(int i=0; i<users.size(); i++){
            if (users.get(i).getEmail().equals(User.currentUser(requireContext()).getEmail())){
//                binding.txtRole.setText();
                role = User.currentUser(requireContext()).getEmail()+" - "+
                        requireContext().getResources().getStringArray(R.array.role_array)[Integer.parseInt(users.get(i).getRole_id()) -1];
                role_id = Integer.parseInt(users.get(i).getRole_id());
                break;
            }
        }
        Log.i(tag, "The role is : "+role_id);
        if (role_id > 2){
            binding.cardMenu.setVisibility(View.GONE);
            binding.cardUser.setVisibility(View.GONE);
            binding.cardTable.setVisibility(View.GONE);
        }
        binding.txtName.setText(User.currentUser(requireContext()).getName()+" "+role);
        Log.i(tag, "The profile : "+
                new Gson().toJson(User.currentUser(requireContext()).getBusinesses().get(0).getPivot()));

        binding.txtAmount.setText(
                Order.getCalculateFromList(AppDataBase.getInstance(requireContext()).orderDao().getTodayOrders(), requireContext())+" FCFA"
        );
        binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.list.setAdapter(new OrderAdapter(
                requireContext(), AppDataBase.getInstance(requireContext()).orderDao().getTodayOrders(),
                true, new ListItemClickCallback() {
            @Override
            public void onItemClickListener(Object o, boolean isLong) {

            }
        }
        ));

        binding.cardOrder.setOnClickListener(this);
        binding.btnAllOrders.setOnClickListener(this);
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
        else if (view == binding.cardOrder || view == binding.btnAllOrders){
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.action_ProfileFragment_to_OrderListFragment);
        }
    }
}
