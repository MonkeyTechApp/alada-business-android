package com.poupock.feussom.aladabusiness.core.menu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentCreateMenuCategoryBinding;
import com.poupock.feussom.aladabusiness.databinding.FragmentListCategoryMenuBinding;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.MenuItemCategory;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.PutTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.poupock.feussom.aladabusiness.util.Methods.createTextWatcher;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateMenuCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressLint("SimpleDateFormat")
public class CreateMenuCategoryFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = CreateMenuCategoryFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateMenuCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateMenuCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateMenuCategoryFragment newInstance(String param1, String param2) {
        CreateMenuCategoryFragment fragment = new CreateMenuCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.i(TAG,"Back pressed");
                NavHostFragment.findNavController(CreateMenuCategoryFragment.this)
                    .navigate(R.id.action_createCategoryMenuFragment_to_listMenuFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    FragmentCreateMenuCategoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateMenuCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    MenuCreationViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MenuCreationViewModel.class);
        Objects.requireNonNull(binding.nameTextField.getEditText()).addTextChangedListener(createTextWatcher(binding.nameTextField));
        if(viewModel.getSelectedCategoryLiveData().getValue() != null)
            binding.nameTextField.getEditText().setText(viewModel.getSelectedCategoryLiveData().getValue().getName());

        binding.btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(binding.btnSave == view){
            binding.btnSave.setEnabled(false);
            String name = Objects.requireNonNull(binding.nameTextField.getEditText()).getText().toString().trim();
            if(!name.isEmpty()){
                if(AppDataBase.getInstance(requireContext()).menuItemCategoryDao().getSpecificMenuItemCategory(name) == null){

                    MenuItemCategory menuItemCategory = new MenuItemCategory();
                    menuItemCategory.setName(binding.nameTextField.getEditText().getText().toString());
                    menuItemCategory.setCreated_at(Methods.getCurrentTimeStamp());

                    HashMap<String, String> params = new HashMap<>();
                    params.put("name", menuItemCategory.getName());
                    params.put("business_id", viewModel.getBusinessLiveData().getValue().getId()+"");
                    ProgressDialog dialog = new ProgressDialog(requireContext());
                    if(viewModel.getSelectedCategoryLiveData().getValue()!=null) {
                        new PutTask(requireContext(), ServerUrl.MENU_CATEGORY+"/"+viewModel.getSelectedCategoryLiveData().getValue().getId(), params,
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
                                            MenuItemCategory category = MenuItemCategory.getFromObject(datumResponse.data);
                                            menuItemCategory.setId(category.getId());
                                            menuItemCategory.setUser_id(User.currentUser(requireContext()).getId());
                                            if(AppDataBase.getInstance(requireContext()).menuItemCategoryDao().update(menuItemCategory)<=0) {
                                                Toast.makeText(requireContext(), getString(R.string.menu_category_not_updated),Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(requireContext(), getString(R.string.menu_category_updated),Toast.LENGTH_SHORT).show();
                                                NavHostFragment.findNavController(CreateMenuCategoryFragment.this)
                                                        .navigate(R.id.action_createCategoryMenuFragment_to_listMenuFragment);
                                            }
                                        }
                                        else Toast.makeText(requireContext(), getString(R.string.menu_category_not_updated),Toast.LENGTH_SHORT).show();
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
                                        dialog.dismiss();
                                        binding.btnSave.setEnabled(true);
                                    }
                                }).execute();
                    }
                    else {
                        new PostTask(requireContext(), ServerUrl.MENU_CATEGORY, params,
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
                                            MenuItemCategory category = MenuItemCategory.getFromObject(datumResponse.data);
                                            category.setUser_id(User.currentUser(requireContext()).getId());

                                            if(AppDataBase.getInstance(requireContext()).menuItemCategoryDao().insert(category)>0){
                                                Toast.makeText(requireContext(), getString(R.string.menu_category_inserted),Toast.LENGTH_SHORT).show();
                                                NavHostFragment.findNavController(CreateMenuCategoryFragment.this)
                                                        .navigate(R.id.action_createCategoryMenuFragment_to_listMenuFragment);
                                            }else{
                                                Toast.makeText(requireContext(), getString(R.string.menu_category_not_inserted),Toast.LENGTH_SHORT).show();
                                            }

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


                }else {
                    binding.nameTextField.setError(getString(R.string.category_name_already_exist));
                    binding.nameTextField.setErrorEnabled(true);
                    binding.btnSave.setEnabled(true);
                }
            }
            else {
                binding.nameTextField.setError(getString(R.string.category_name_input_error));
                binding.nameTextField.setErrorEnabled(true);
                binding.btnSave.setEnabled(true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
