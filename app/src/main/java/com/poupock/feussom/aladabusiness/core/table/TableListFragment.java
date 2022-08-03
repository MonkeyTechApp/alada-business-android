package com.poupock.feussom.aladabusiness.core.table;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.core.menu.MenuCreationViewModel;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentTableListBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.GuestTableAdapter;
import com.poupock.feussom.aladabusiness.ui.dialog.CustomDialogFragment;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.GuestTable;

public class TableListFragment extends Fragment {

    private FragmentTableListBinding binding;
    private final Gson gson = new Gson();
    private GuestTableViewModel viewModel;
    GuestTableAdapter guestTableAdapter = null;

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        binding = FragmentTableListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(GuestTableViewModel.class);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setIsEditMutableLiveData(false);
                viewModel.setGuestTableMutableLiveData(null);
                NavHostFragment.findNavController(TableListFragment.this)
                        .navigate(R.id.action_TableListFragment_to_TableCreationFragment);
            }
        });

        binding.list.setLayoutManager(new GridLayoutManager(requireContext(),2));
        guestTableAdapter = new GuestTableAdapter(requireContext(), AppDataBase.getInstance(requireContext()).guestTableDao().getAllGuestTables(),
            new DialogCallback() {
                @Override
                public void onActionClicked(Object o, int action) {
                    GuestTable guestTable = gson.fromJson(gson.toJson(o), GuestTable.class);
                    if(action == Constant.ACTION_EDIT){
                        viewModel.setIsEditMutableLiveData(true);
                        viewModel.setGuestTableMutableLiveData(guestTable);
                        NavHostFragment.findNavController(TableListFragment.this)
                            .navigate(R.id.action_TableListFragment_to_TableCreationFragment);
                    }
                    else if (action == Constant.ACTION_DELETE){
                        AppDataBase.getInstance(requireContext()).guestTableDao().delete(guestTable);
                        guestTableAdapter.setGuestTables(AppDataBase.getInstance(requireContext()).guestTableDao().getAllGuestTables());
                    }
                }
            });
        binding.list.setAdapter(guestTableAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    public void showDialog() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        CustomDialogFragment newFragment = new CustomDialogFragment();
//
//        if (isLargeLayout) {
//            // The device is using a large layout, so show the fragment as a dialog
//            newFragment.show(fragmentManager, "dialog");
//        } else {
//            // The device is smaller, so show the fragment fullscreen
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            // For a little polish, specify a transition animation
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//            // To make it fullscreen, use the 'content' root view as the container
//            // for the fragment, which is always the root view for the activity
//            transaction.add(android.R.id.content, newFragment)
//                .addToBackStack(null).commit();
//        }
//    }
}
