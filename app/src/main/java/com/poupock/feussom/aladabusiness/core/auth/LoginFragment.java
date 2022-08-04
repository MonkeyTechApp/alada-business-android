package com.poupock.feussom.aladabusiness.core.auth;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.menu.CreateMenuFragment;
import com.poupock.feussom.aladabusiness.databinding.FragmentLoginBinding;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.Connection;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = LoginFragment.class.getSimpleName();
    private MainViewModel mViewModel;
    private FragmentLoginBinding binding;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                Log.i(TAG,"Back pressed");
//                NavHostFragment.findNavController(LoginFragment.this)
//                    .navigate(R.id.actionLo);
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(this);

        binding.txtRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnLogin){
            if(Methods.validate(Objects.requireNonNull(binding.edtEmail.getEditText())
                .getText().toString().trim())){
                if(Objects.requireNonNull(binding.edtPassword.getEditText()).getText().toString().trim().length() > 0){
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", binding.edtEmail.getEditText().getText().toString().trim());
                    params.put("password", binding.edtPassword.getEditText().getText().toString().trim());
                    ProgressDialog dialog = new ProgressDialog(requireContext());

                    new PostTask(requireContext(), ServerUrl.CONNECTION_URL, params,
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
                                Connection connection = gson.fromJson(response, Connection.class);
                                Toast.makeText(requireContext(), R.string.connection_successful, Toast.LENGTH_SHORT).show();
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
                }
            }
            else {
                Toast.makeText(requireContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show();
            }
        }
        else if(view == binding.txtRegister) {
            NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_fragmentLogin_to_fragmentRegister);
        }
    }
}
