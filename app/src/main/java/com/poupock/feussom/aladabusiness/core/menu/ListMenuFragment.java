package com.poupock.feussom.aladabusiness.core.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentListMenuBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.MenuItemAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.MenuItemCategoryAdapter;
import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;
import com.poupock.feussom.aladabusiness.util.Methods;

import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class ListMenuFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ListMenuFragment.class.getSimpleName();
    private FragmentListMenuBinding binding;
    private Gson gson = new Gson();
    MenuItemAdapter adapter;
    MenuCreationViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    @NonNull
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {

        binding = FragmentListMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MenuCreationViewModel.class);

        binding.list.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.listCategory.setLayoutManager(new LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,false));

        adapter = new MenuItemAdapter(requireContext(),
            AppDataBase.getInstance(requireContext()).menuItemDao().getAllMenuItems(), true,
            new ListItemClickCallback() {
                @Override
                public void onItemClickListener(Object o, boolean isLong) {

                    if(!isLong){
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle(R.string.menu_actions)
                            .setCancelable(true)
                            .setItems(R.array.action_array, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                }
                            }).create().show();
                    }
                }
            });

        List<MenuItemCategory> menuItemCategories = AppDataBase.getInstance(requireContext()).
            menuItemCategoryDao().getAllMenuItemCategories();

        if(menuItemCategories!=null)
            menuItemCategories.add(0, new MenuItemCategory(0,getString(R.string.all),0, Methods.getCurrentTimeStamp()));

        if (menuItemCategories != null) {
            binding.listCategory.setAdapter(new MenuItemCategoryAdapter(requireContext(),
                menuItemCategories,
                new ListItemClickCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onItemClickListener(Object o, boolean isLong) {
                        MenuItemCategory category =
                            gson.fromJson(gson.toJson(o),MenuItemCategory.class);
                        if(!isLong){
                            if(category.getId() == 0){
                                adapter.setMenuItems(AppDataBase.getInstance(requireContext()).menuItemDao().getAllMenuItems());
                                adapter.notifyDataSetChanged();
                            }else{
                                adapter.setMenuItems(AppDataBase.getInstance(requireContext()).menuItemDao().getSpecificCategoryItems(category.getId()));
                                adapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle(R.string.category_action)
                                .setCancelable(true)
                                .setItems(R.array.action_array, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        if(which == 1){
                                            viewModel.setSelectedCategoryMutableLiveData(category);
                                            NavHostFragment.findNavController(ListMenuFragment.this)
                                                .navigate(R.id.action_listMenuFragment_to_CreateCategoryMenuFragment);
                                        }
                                        else if(which == 2){

                                        }

                                    }
                                }).create().show();
                        }
                    }
                }));
        }

        binding.edtSearch.setOnEditorActionListener(
            new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionType, KeyEvent keyEvent) {
                    if(actionType == EditorInfo.IME_ACTION_SEARCH){
                        Log.i(TAG,"The search action called");
                        if(adapter != null){
                            adapter.setMenuItems(Methods.filterMenuItems(adapter.getMenuItems(),
                                binding.edtSearch.getText().toString().trim()));
                            adapter.notifyDataSetChanged();
                        }
                        Methods.hideKeyBoard(requireActivity());
                        return true;
                    }
                    return false;
                }
            }
        );

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 0)
                    if(adapter!=null) {
                        adapter.setMenuItems(AppDataBase.getInstance(requireContext()).menuItemDao().getAllMenuItems());
                        adapter.notifyDataSetChanged();
                    }
            }
        });

        binding.linFocus.requestFocus();

        binding.list.setAdapter(adapter);
        binding.txtCategory.setOnClickListener(this);
        binding.buttonFirst.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view == binding.txtCategory){
            NavHostFragment.findNavController(ListMenuFragment.this)
                .navigate(R.id.action_listMenuFragment_to_CreateCategoryMenuFragment);
        }
        else if(view == binding.buttonFirst) {
            NavHostFragment.findNavController(ListMenuFragment.this)
                .navigate(R.id.action_listMenuFragment_to_CreateMenuFragment);
        }
    }
}
