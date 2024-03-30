package com.poupock.feussom.aladabusiness.core.menu;

import android.app.ProgressDialog;
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

import com.android.volley.VolleyError;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentCreateMenuBinding;
import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.PutTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

public class CreateMenuFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CreateMenuFragment.class.getSimpleName();
    private FragmentCreateMenuBinding binding;
    private MenuCreationViewModel viewModel;
    private String tag = CreateMenuFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.i(TAG,"Back pressed");
                if(viewModel.getSelectedMenuItemLiveData().getValue() != null) {
                   viewModel.setSelectedMenuItemMutableLiveData(null);
//            viewModel.setMenuItemCategoryMutableLiveData(viewModel.getSelectedMenuItemLiveData().getValue().getMenuItemCategory());
                }
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
            if (isResumed()){
                binding.btnCategory.setText(Objects.requireNonNull(viewModel.getMenuItemCategoryLiveData().getValue()).getName());
            }

        });

        if(viewModel.getSelectedMenuItemLiveData().getValue() != null) {
            binding.nameTextField.getEditText().setText(viewModel.getSelectedMenuItemLiveData().getValue().getName());
            binding.priceTextField.getEditText().setText(viewModel.getSelectedMenuItemLiveData().getValue().getPrice()+"");
//            viewModel.setMenuItemCategoryMutableLiveData(viewModel.getSelectedMenuItemLiveData().getValue().getMenuItemCategory());
        }


        binding.btnCategory.setOnClickListener(this);
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnSave.setEnabled(false);
                String name = Objects.requireNonNull(binding.nameTextField.getEditText()).getText().toString().trim();
                String price = Objects.requireNonNull(binding.priceTextField.getEditText()).getText().toString().trim();
                if(!name.isEmpty()){
                    if(!price.isEmpty()){

                        HashMap<String, String> params = new HashMap<>();
                        params.put("name", name);
                        params.put("menu_category_id", viewModel.getMenuItemCategoryLiveData().getValue().getId()+"");
                        params.put("price", price);

                        ProgressDialog dialog = new ProgressDialog(requireContext());

                        if (viewModel.getSelectedMenuItemLiveData().getValue() == null){
                            new PostTask(requireContext(), ServerUrl.MENU_ITEM, params,
                                    new VolleyRequestCallback() {
                                        @Override
                                        public void onStart() {
                                            dialog.setMessage(getString(R.string.processing_3_dots));
                                            dialog.setCancelable(false);
                                            dialog.show();
                                        }

                                        @Override
                                        public void onSuccess(String response) {
                                            dialog.dismiss();
                                            DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
                                            if (datumResponse.success){
                                                MenuItem serverMenuItem = MenuItem.getFromObject(datumResponse.data);
                                                MenuItem menuItem = new MenuItem(serverMenuItem.getId(), name,price, "", "",
                                                        viewModel.getMenuItemCategoryLiveData().getValue().getId(), User.currentUser(requireContext()).getId(),
                                                        serverMenuItem.getPrice(),serverMenuItem.getCreated_at(),"");

                                                Objects.requireNonNull(viewModel.getMenuItemLiveData().getValue()).setName(name);
                                                viewModel.getMenuItemLiveData().getValue().setPrice(Double.parseDouble(price));
                                                viewModel.getMenuItemLiveData().getValue().setCreated_at(Methods.getCurrentTimeStamp());

                                                AppDataBase.getInstance(requireContext()).menuItemDao().insert(
                                                        menuItem
                                                );

                                                requireActivity().onBackPressed();
//                                                NavHostFragment.findNavController(CreateMenuFragment.this)
//                                                        .navigate(R.id.action_createMenuFragment_to_listMenuFragment);
                                            }
                                            else {
                                                Toast.makeText(requireContext(), R.string.menu_not_created, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(VolleyError error) {
                                            dialog.dismiss();
                                            FirebaseCrashlytics.getInstance().recordException(
                                                    new RuntimeException(new String(error.networkResponse.data, StandardCharsets.UTF_8)));
                                            Toast.makeText(requireContext(), getString(R.string.server_error),Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onJobFinished() {
                                            binding.btnSave.setEnabled(true);
                                            dialog.dismiss();
                                        }
                                    }).execute();
                        }
                        else {
                            Log.i(tag, "The URL : "+ServerUrl.MENU_ITEM+"/"+viewModel.getSelectedMenuItemLiveData().getValue().getId());
                            new PutTask(requireContext(), ServerUrl.MENU_ITEM+"/"+viewModel.getSelectedMenuItemLiveData().getValue().getId(),
                                    params,
                                    new VolleyRequestCallback() {
                                        @Override
                                        public void onStart() {
                                            dialog.setMessage(getString(R.string.processing_3_dots));
                                            dialog.setCancelable(false);
                                            dialog.show();
                                        }

                                        @Override
                                        public void onSuccess(String response) {
                                            dialog.dismiss();
                                            DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
                                            if (datumResponse.success){
                                                MenuItem serverMenuItem = MenuItem.getFromObject(datumResponse.data);
                                                MenuItem menuItem = new MenuItem(serverMenuItem.getId(), name,price, "", "",
                                                        viewModel.getMenuItemCategoryLiveData().getValue().getId(), User.currentUser(requireContext()).getId(),
                                                        serverMenuItem.getPrice(),serverMenuItem.getCreated_at(),"");

                                                Objects.requireNonNull(viewModel.getMenuItemLiveData().getValue()).setName(name);
                                                viewModel.getMenuItemLiveData().getValue().setPrice(Double.parseDouble(price));
                                                viewModel.getMenuItemLiveData().getValue().setCreated_at(Methods.getCurrentTimeStamp());

                                                AppDataBase.getInstance(requireContext()).menuItemDao().update(
                                                        menuItem
                                                );

                                                requireActivity().onBackPressed();
//                                                NavHostFragment.findNavController(CreateMenuFragment.this)
//                                                        .navigate(R.id.action_createMenuFragment_to_listMenuFragment);
                                            }
                                            else {
                                                Toast.makeText(requireContext(), R.string.menu_not_created, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(VolleyError error) {
                                            dialog.dismiss();
                                            Toast.makeText(requireContext(), getString(R.string.server_error),Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onJobFinished() {
                                            binding.btnSave.setEnabled(true);
                                            dialog.dismiss();
                                        }
                                    }).execute();
                        }
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
