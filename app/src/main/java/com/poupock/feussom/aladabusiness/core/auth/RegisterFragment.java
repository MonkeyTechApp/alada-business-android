package com.poupock.feussom.aladabusiness.core.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.dashboard.DashboardActivity;
import com.poupock.feussom.aladabusiness.databinding.FragmentRegisterBinding;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.Connection;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = LoginFragment.class.getSimpleName();
    private MainViewModel mViewModel;
    private FragmentRegisterBinding binding;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegister.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == binding.btnRegister){
            if(Objects.requireNonNull(binding.edtName.getEditText()).getText().toString().trim().length()>3){
                if(Methods.validate(Objects.requireNonNull(binding.edtEmail.getEditText())
                    .getText().toString().trim())){
                    if (Methods.verifyPhone(binding.edtPhone.getEditText().getText().toString().trim())){
                        if(Objects.requireNonNull(binding.edtPassword.getEditText()).getText().toString().trim().length() > 0){
                            if(binding.edtPassword.getEditText().getText().toString().trim().equals(
                                    Objects.requireNonNull(binding.edtConfirmPassword.getEditText()).getText().toString().trim()
                            )){
                                if (Methods.verifyPassword(binding.edtPassword.getEditText().toString().trim())){
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("email", binding.edtEmail.getEditText().getText().toString().trim());
                                    params.put("name", binding.edtName.getEditText().getText().toString().trim());
                                    params.put("phone", binding.edtPhone.getEditText().getText().toString().trim());
                                    params.put("password", binding.edtPassword.getEditText().getText().toString().trim());
                                    params.put("confirm-password", binding.edtConfirmPassword.getEditText().getText().toString().trim());
                                    ProgressDialog dialog = new ProgressDialog(requireContext());

                                    new PostTask(requireContext(), ServerUrl.REGISTRATION_URL, params,
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

                                                    User.storeConnectedUser(connection.data, requireContext());
                                                    User.storeToken(connection.access_token, requireContext());

                                                    Toast.makeText(requireContext(), R.string.connection_successful, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(requireContext(), DashboardActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    requireActivity().finish();
                                                }

                                                @Override
                                                public void onError(VolleyError error) {

                                                }

                                                @Override
                                                public void onJobFinished() {
                                                    dialog.dismiss();
                                                }
                                            }).execute();
                                }
                                else {
                                    Toast.makeText(requireContext(),R.string.password_must_digit_4_char, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(requireContext(),R.string.confirm_pass_not_match, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(requireContext(),R.string.password_must_digit_4_char, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), R.string.invalid_phone, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(requireContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(requireContext(), R.string.invalide_name, Toast.LENGTH_SHORT).show();
            }

        }
        else if (view == binding.btnLogin){
            NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_fragmentRegister_to_fragmentLogin);
        }
    }
}
