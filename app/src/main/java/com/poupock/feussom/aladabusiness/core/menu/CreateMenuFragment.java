package com.poupock.feussom.aladabusiness.core.menu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.core.restaurant.BusinessCreationViewModel;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentCreateMenuBinding;
import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.Objects;

public class CreateMenuFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CreateMenuFragment.class.getSimpleName();
    private FragmentCreateMenuBinding binding;
    private MenuCreationViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.i(TAG,"Back pressed");
                NavHostFragment.findNavController(CreateMenuFragment.this)
                    .navigate(R.id.action_createMenuFragment_to_listMenuFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCreateMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MenuCreationViewModel.class);

        viewModel.getMenuItemCategoryLiveData().observe(requireActivity(), item -> {
            Log.i(TAG, "The menu item category variable has been updated!");
            binding.btnCategory.setText(Objects.requireNonNull(viewModel.getMenuItemCategoryLiveData().getValue()).getName());
        });

        binding.btnCategory.setOnClickListener(this);
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = Objects.requireNonNull(binding.nameTextField.getEditText()).getText().toString().trim();
                String price = Objects.requireNonNull(binding.priceTextField.getEditText()).getText().toString().trim();
                if(!name.isEmpty()){
                    if(!price.isEmpty()){

                        Objects.requireNonNull(viewModel.getMenuItemLiveData().getValue()).setTitle(name);
                        viewModel.getMenuItemLiveData().getValue().setPrice(Double.parseDouble(price));
                        viewModel.getMenuItemLiveData().getValue().setCreated_at(Methods.getCurrentTimeStamp());

                        AppDataBase.getInstance(requireContext()).menuItemDao().insert(
                            viewModel.getMenuItemLiveData().getValue()
                        );

                        NavHostFragment.findNavController(CreateMenuFragment.this)
                            .navigate(R.id.action_createMenuFragment_to_listMenuFragment);
                    }
                    else {
                        Toast.makeText(requireContext(),R.string.menu_item_price_input_error, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(requireContext(),R.string.menu_item_name_input_error, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnCategory){
            DialogFragment fragment = ListFragment.newInstance("","");
            fragment.show(getParentFragmentManager(),ListFragment.class.getSimpleName());
        }
    }
}
