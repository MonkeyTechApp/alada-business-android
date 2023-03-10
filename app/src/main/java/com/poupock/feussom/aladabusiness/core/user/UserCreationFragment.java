package com.poupock.feussom.aladabusiness.core.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.menu.ListMenuFragment;
import com.poupock.feussom.aladabusiness.core.menu.MenuCreationViewModel;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentUserCreationBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.UserAdapter;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.Role;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.PostImageTask;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.util.HashMap;

public class UserCreationFragment extends Fragment implements View.OnClickListener {

    private FragmentUserCreationBinding binding;
    private Bitmap bitmap = null;
    UserActivityViewModel viewModel;
    private String tag = UserCreationFragment.class.getSimpleName();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentUserCreationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(UserActivityViewModel.class);

        cleanInputField(binding.emailTextField);
        cleanInputField(binding.nameTextField);
        cleanInputField(binding.phoneTextField);

        binding.cardImage.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.btnRole.setOnClickListener(this);

        if(viewModel.getIsEditLiveData().getValue()){
            User user = viewModel.getUserLiveData().getValue();
            if (user != null) {
                binding.nameTextField.getEditText().setText(user.getName());
                binding.phoneTextField.getEditText().setText(user.getPhone()+"");
                binding.emailTextField.getEditText().setText(user.getEmail()+"");
                String[] arr = requireContext().getResources().getStringArray(R.array.role_array);
                binding.btnRole.setText(arr[Integer.parseInt(user.getRole_id())-1]);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (binding.btnSave == view){
            if (!binding.nameTextField.getEditText().getText().toString().isEmpty()){
                if (!binding.emailTextField.getEditText().getText().toString().isEmpty()){
                    if (!binding.phoneTextField.getEditText().getText().toString().isEmpty()){
                        if (viewModel.getRoleLiveData().getValue() != null){

                            ProgressDialog dialog = new ProgressDialog(requireContext());
                            dialog.setMessage(getString(R.string.create_a_personnel)+" "+getString(R.string.processing_3_dots));
                            dialog.setCancelable(false);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", binding.nameTextField.getEditText().getText().toString().trim());
                            hashMap.put("email", binding.emailTextField.getEditText().getText().toString().trim());
                            hashMap.put("phone", binding.phoneTextField.getEditText().getText().toString().trim());
                            hashMap.put("business_role_id", viewModel.getRoleLiveData().getValue().getId()+"");
                            hashMap.put("business_id", AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getId()+"");
                            if (Boolean.TRUE.equals(viewModel.getIsEditLiveData().getValue()))
                                hashMap.put("_method", "PUT");
                            Log.i(tag, "The map is : "+hashMap.toString());
                            if (bitmap != null){
                                postWithImage(dialog, hashMap);
                            }else {
                                postData(dialog, hashMap);
                            }
                        }else {
                            Toast.makeText(requireContext(),R.string.select_role, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        binding.phoneTextField.setError(getString(R.string.provide_info));
                    }
                }else {
                    binding.emailTextField.setError(getString(R.string.provide_info));
                }
            }else {
                binding.nameTextField.setError(getString(R.string.provide_info));
            }
        }
        else if (binding.cardImage == view){

        }else if (binding.btnRole == view){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.select_role)
                    .setCancelable(true)
                    .setItems(R.array.role_array, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            Role role = new Role();
                            role.setId(which+1);
                            viewModel.setRoleMutableLiveData(role);
                            binding.btnRole.setText(requireContext().getResources().getStringArray(R.array.role_array)[which]);
                        }
                    }).create().show();
        }
    }

    public void postWithImage(ProgressDialog dialog, HashMap<String ,String> hashMap){
        new PostImageTask(requireContext(), ServerUrl.CREATE_USER, bitmap, hashMap, true,
            new VolleyRequestCallback() {
                @Override
                public void onStart() {
                    dialog.show();
                }

                @Override
                public void onSuccess(String response) {
                    treatResponse(response);
                    dialog.dismiss();
                }

                @Override
                public void onError(VolleyError error) {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onJobFinished() {

                }
            }
        ).execute();
    }

    public void postData(ProgressDialog dialog, HashMap<String, String> hashMap){
        new PostTask(requireContext(), ServerUrl.CREATE_USER, hashMap, new VolleyRequestCallback() {
            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onSuccess(String response) {
                treatResponse(response);
                dialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onJobFinished() {
            }
        }).execute();
    }

    void treatResponse(String response){
        DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
        if (datumResponse.success){
            User user = User.getObjectFromObject(datumResponse.data);
            user.setRole_id(viewModel.getRoleLiveData().getValue().getId()+"");

            long id = AppDataBase.getInstance(requireContext()).userDao().insert(user);
            if (id > 0){
                Toast.makeText(requireContext(), R.string.user_inserted, Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(requireContext(), R.string.user_not_inserted, Toast.LENGTH_LONG).show();
            }
            requireActivity().onBackPressed();
        }
    }

    void cleanInputField(TextInputLayout edt){
        edt.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                edt.setErrorEnabled(false);
            }
        });
    }
}
