package com.poupock.feussom.aladabusiness.core.restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.databinding.FragmentBusinessBasicBinding;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Methods;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

public class BusinessBasicFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = BusinessBasicFragment.class.getSimpleName();

    BusinessCreationViewModel viewModel;
    private FragmentBusinessBasicBinding binding;
    private final Business business = new Business();
    ActivityResultLauncher<String> resultLauncher;
    MaterialAlertDialogBuilder dialogBuilderAddress = null;
    boolean isFormOk = true;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentBusinessBasicBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] categories = getResources().getStringArray(R.array.business_categories);
        dialogBuilderAddress = new MaterialAlertDialogBuilder(view.getContext());
        dialogBuilderAddress.setTitle(R.string.business_category).setCancelable(true);
        dialogBuilderAddress.setItems(categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                business.setCategory_id(i);
                business.setCategory_name(categories[i]);

                binding.btnCategory.setText(categories[i]);

            }
        });

        resultLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {

                    Log.i(TAG,"Fragment returned");
                    Log.i(TAG, "The image path is : "+ result.getPath());
                    try {
                        Log.i(TAG, "Reading the stream with encoded path "+ result.getEncodedPath());
                        InputStream imageStream = requireActivity().getContentResolver().openInputStream(result);
                        Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);

                        // Store image in another folder and query the internal path.

                        String relPath = Methods.saveImage(selectedImageBitmap, requireContext());

                        business.setImageLocalUrl(relPath);
                        binding.imgProduct.setImageBitmap(selectedImageBitmap);
                        binding.txtImageAdd.setVisibility(View.GONE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG, "The error is "+ e.toString());
                        Toast.makeText(requireContext(), R.string.image_internal_loading_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );

        viewModel = new ViewModelProvider(requireActivity()).get(BusinessCreationViewModel.class);

        binding.buttonFirst.setOnClickListener(this);
        binding.btnCategory.setOnClickListener(this);
        binding.cardImage.setOnClickListener(this);
        binding.txtImageAdd.setOnClickListener(this);
        binding.imgProduct.setOnClickListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view == binding.buttonFirst){
            if (Objects.requireNonNull(binding.nameTextField.getEditText()).getText().toString().trim().length() > 3){
                if(business.getCategory_id() >= 0 && business.getCategory_name() != null){
                    business.setName(binding.nameTextField.getEditText().getText().toString().trim());
                    if(Objects.requireNonNull(binding.detailTextField.getEditText()).getText().toString().trim().length() < 25 &&
                        binding.detailTextField.getEditText().getText().toString().trim().length() > 0){
                        isFormOk = false;
                        Toast.makeText(requireContext(), R.string.business_description_input_error, Toast.LENGTH_SHORT).show();
                    }else {
                        isFormOk = true;
                        business.setDescription(binding.detailTextField.getEditText().getText().toString().trim());
                    }

                    if(isFormOk){
                        business.setName(binding.nameTextField.getEditText().getText().toString().toLowerCase().trim());
                        viewModel.setBusinessMutableLiveData(business);
                        NavHostFragment.findNavController(BusinessBasicFragment.this)
                            .navigate(R.id.action_FirstFragment_to_Second2Fragment);
                    }
                }else {
                    Toast.makeText(requireContext(), R.string.business_category_input_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), R.string.business_name_input_error, Toast.LENGTH_SHORT).show();
            }
        }
        else if(view == binding.btnCategory){
            dialogBuilderAddress.show();
        }
        else if(view == binding.imgProduct || view == binding.txtImageAdd || view == binding.cardImage){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            if(!Methods.runtimeWritePermissions(requireActivity()))
                resultLauncher.launch("image/*");
            else{
                Toast.makeText(requireContext(),R.string.grant_permission_and_restart, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
