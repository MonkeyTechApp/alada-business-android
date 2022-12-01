package com.poupock.feussom.aladabusiness.core.table;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.menu.CreateMenuFragment;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentCreateTableBinding;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.MenuItem;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

import static com.poupock.feussom.aladabusiness.util.Methods.createTextWatcher;

public class TableCreationFragment extends Fragment {

    private FragmentCreateTableBinding binding;
    private GuestTableViewModel viewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCreateTableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(binding.capacityTextField.getEditText()).addTextChangedListener(createTextWatcher(binding.capacityTextField));
        Objects.requireNonNull(binding.nameTextField.getEditText()).addTextChangedListener(createTextWatcher(binding.nameTextField));

        viewModel = new ViewModelProvider(requireActivity()).get(GuestTableViewModel.class);

        if(viewModel.getIsEditLiveData().getValue()){
            GuestTable guestTable = viewModel.getGuestTableLiveData().getValue();
            if (guestTable != null) {
                binding.nameTextField.getEditText().setText(guestTable.getTitle());
                binding.capacityTextField.getEditText().setText(guestTable.getCapacity()+"");
            }
        }

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnCreate.setEnabled(false);
                String name = Objects.requireNonNull(binding.nameTextField.getEditText()).getText().toString().trim();
                try {
                    int capacity = Integer.parseInt(Objects.requireNonNull(binding.capacityTextField.getEditText()).getText().toString().trim());
                    if(capacity > 0){
                        if(name.length() > 0){
                            if(AppDataBase.getInstance(requireContext()).guestTableDao().getSpecificGuestTable(name) == null){
                                HashMap<String, String> params = new HashMap<>();
                                params.put("name", name);
                                params.put("capacity", capacity+"");
                                params.put("business_id", viewModel.getBusinessLiveData().getValue().getId()+"");

                                ProgressDialog dialog = new ProgressDialog(requireContext());

                                new PostTask(requireContext(), ServerUrl.GUEST_TABLE, params,
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
                                                    GuestTable guestTable = GuestTable.getFromObject(datumResponse.data);
                                                    if(viewModel.getIsEditLiveData().getValue()){

//                                                        GuestTable guestTable = viewModel.getGuestTableLiveData().getValue();
                                                        if (guestTable != null) {
                                                            guestTable.setTitle(name);
                                                            guestTable.setCapacity(capacity);

                                                            AppDataBase.getInstance(requireContext()).guestTableDao().update(guestTable);
                                                            Toast.makeText(requireContext(), R.string.table_updated, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else {
//                                                        GuestTable guestTable = new GuestTable();
                                                        guestTable.setTitle(name);
                                                        guestTable.setCapacity(capacity);
                                                        guestTable.setCreated_at(Methods.getCurrentTimeStamp());

                                                        AppDataBase.getInstance(requireContext()).guestTableDao().insert(guestTable);
                                                        Toast.makeText(requireContext(), R.string.table_created, Toast.LENGTH_SHORT).show();
                                                    }


                                                    NavHostFragment.findNavController(TableCreationFragment.this)
                                                            .navigate(R.id.action_TableCreationFragment_to_TableListFragment);
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
                                                binding.btnCreate.setEnabled(true);
                                                dialog.dismiss();
                                            }
                                        }).execute();

                            }
                            else {
                                binding.nameTextField.setErrorEnabled(true);
                                binding.nameTextField.setError(getString(R.string.table_name_already_exist));
                            }
                        }
                    }
                    else {
                        binding.capacityTextField.setErrorEnabled(true);
                        binding.capacityTextField.setError(getString(R.string.table_capacity_input_error));

                    }
                }catch (NumberFormatException ex){
                    Toast.makeText(requireContext(),R.string.table_capacity_input_error, Toast.LENGTH_SHORT).show();
                    binding.capacityTextField.setErrorEnabled(true);
                    binding.capacityTextField.setError(getString(R.string.table_capacity_input_error));

                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
