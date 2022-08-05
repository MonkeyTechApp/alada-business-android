package com.poupock.feussom.aladabusiness.core.restaurant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.DashboardActivity;
import com.poupock.feussom.aladabusiness.HomeActivity;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentBusinessCreationDetailBinding;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.Connection;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

@SuppressLint({"SetTextI18n", "SimpleDateFormat"})
public class BusinessCreationDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = BusinessCreationDetailFragment.class.getSimpleName();
    private FragmentBusinessCreationDetailBinding binding;
    private BusinessCreationViewModel viewModel;
    AlertDialog dialog;
    ActivityResultLauncher<String[]> locationPermissionRequest;

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {

        binding = FragmentBusinessCreationDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(BusinessCreationViewModel.class);
        viewModel.getBusinessLiveData().observe(requireActivity(), item -> {
            Log.i(TAG, "The business variable has been updated!");
        });

        dialog = Methods.setProgressDialog(requireContext());

        locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                    .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                        dialog.show();
                        queryLocation(dialog, true);
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                        dialog.show();
                        queryLocation(dialog, false);
                    } else {
                        // No location access granted.
                        Log.i(TAG,"No location access was granted");
                    }
                }
            );
//        fusedLocationClient = LocationServices.

        binding.buttonFirst.setOnClickListener(this);
        binding.btnGPS.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.buttonFirst) {
            Business business = viewModel.getBusinessLiveData().getValue();
            assert business != null;
            if (Objects.requireNonNull(binding.phoneTextField.getEditText()).getText().toString().trim().length() >= 5) {
                business.setPhone(binding.phoneTextField.getEditText().getText().toString().trim());
                if (Integer.parseInt(Objects.requireNonNull(binding.floorTextField.getEditText()).getText().toString().trim()) > 0) {
                    business.setFloors(Integer.parseInt(binding.floorTextField.getEditText().getText().toString().trim()));
                    if (Objects.requireNonNull(binding.placeTextField.getEditText()).getText().toString().trim().length() > 3) {
                        business.setLocation(binding.placeTextField.getEditText().getText().toString().trim());
                        if (Integer.parseInt(Objects.requireNonNull
                            (binding.salePointTextField.getEditText()).getText().toString().trim()) > 0) {
                            business.setSalePointCount
                                (Integer.parseInt(binding.floorTextField.getEditText().getText().toString().trim()));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-DD hh:mm:ss");
                            business.setCreated_at(sdf.format(new Date()));
                            viewModel.setBusinessMutableLiveData(business);
                            List<String> phones = new ArrayList<>();
                            phones.add(binding.phoneTextField.getEditText().toString().trim());
                            phones.add(Objects.requireNonNull(binding.phone2TextField.getEditText()).toString().trim());
                            HashMap<String, String> params = new HashMap<>();
                            params.put("name", business.getName());
                            params.put("description", business.getDescription());
                            params.put("longitude", business.getLongitude()+"");
                            params.put("latitude", business.getLatitude()+"");
                            params.put("business_type_id", "1");
                            params.put("location", business.getLocation());
                            params.put("phone_numbers", new Gson().toJson(phones));
                            params.put("zone_id", "1");
                            ProgressDialog dialog = new ProgressDialog(requireContext());

                            new PostTask(requireContext(), ServerUrl.BUSINESS_URL, params,
                                new VolleyRequestCallback() {
                                    @Override
                                    public void onStart() {
                                        dialog.setMessage(getString(R.string.connecting_3_dots));
                                        dialog.setCancelable(true);
                                        dialog.show();
                                    }

                                    @Override
                                    public void onSuccess(String response) {
                                        Gson gson = new Gson();

                                        AppDataBase.getInstance(requireContext()).businessDao().insert(business);

                                        Toast.makeText(requireContext(), R.string.businss_created, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(requireContext(), DashboardActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        FirebaseCrashlytics.getInstance().recordException(
                                            new RuntimeException(new String(error.networkResponse.data, StandardCharsets.UTF_8)));
                                    }

                                    @Override
                                    public void onJobFinished() {
                                        dialog.dismiss();
                                    }
                                }).execute();
//                            startActivity(new Intent(requireContext(), HomeActivity.class));
                        } else {
                            Toast.makeText(requireContext(), R.string.business_sale_point_error_input, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), R.string.business_floor_error_input, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.business_sale_point_error_input, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), R.string.business_phone_error_input, Toast.LENGTH_SHORT).show();
            }

//            NavHostFragment.findNavController(BusinessCreationDetailFragment.this)
//                .navigate(R.id.action_Second2Fragment_to_FirstFragment);
        } else if (view == binding.btnGPS) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                locationPermissionRequest.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }else{
                queryLocation(dialog, true);
            }

        }
    }

    @SuppressLint("MissingPermission")
    void queryLocation(AlertDialog dialog, boolean isPrecise) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (isPrecise){
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    // Do something on location canceled request sent
                    return null;
                }

                @Override
                public boolean isCancellationRequested() {
                    // Returns true to make the app actually query the location
                    return false;
                }
            }).addOnSuccessListener(
                requireActivity(),
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Objects.requireNonNull(viewModel.getBusinessLiveData().getValue())
                                .setLongitude(location.getLongitude());
                            viewModel.getBusinessLiveData().getValue().setLatitude(location.getLatitude());
                            binding.btnGPS.setText(location.getLatitude()+ ", "+ location.getLongitude());
                            dialog.dismiss();
                        }
                    }
                }
            );
        }
        else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(
                requireActivity(),
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Objects.requireNonNull(viewModel.getBusinessLiveData().getValue())
                                .setLongitude(location.getLongitude());
                            viewModel.getBusinessLiveData().getValue().setLatitude(location.getLatitude());
                            dialog.dismiss();
                        }
                    }
                }
            );
        }

    }
}
