package com.poupock.feussom.aladabusiness.core.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.dashboard.DashboardActivity;
import com.poupock.feussom.aladabusiness.core.menu.CreateMenuFragment;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentLoginBinding;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.Connection;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
                    if (Methods.verifyPassword(binding.edtPassword.getEditText().getText().toString().trim())){
                        HashMap<String, String> params = new HashMap<>();
                        params.put("identifier", binding.edtEmail.getEditText().getText().toString().trim());
                        params.put("password", binding.edtPassword.getEditText().getText().toString().trim());
                        ProgressDialog dialog = new ProgressDialog(requireContext());

                        new PostTask(requireContext(), ServerUrl.CONNECTION_URL, params,
                                new VolleyRequestCallback() {
                                    @Override
                                    public void onStart() {
                                        dialog.setMessage(getString(R.string.connecting_3_dots));
                                        dialog.setCancelable(false);
                                        dialog.show();
                                    }

                                    @Override
                                    public void onSuccess(String response) {
                                        Gson gson = new Gson();
                                        Connection connection = gson.fromJson(response, Connection.class);
                                        if (connection.success){
                                            User.storeConnectedUser(connection.data, requireContext());
                                            User.storeToken(connection.access_token, requireContext());
                                            if (connection.data.getBusinesses() != null){
                                                AppDataBase dataBase = AppDataBase.getInstance(requireContext());
                                                for (int i = 0 ; i < connection.data.getBusinesses().size(); i++){
                                                    Business business = connection.data.getBusinesses().get(i);
                                                    List<String> strings = gson.fromJson(business.getPhone_numbers(),
                                                            new TypeToken<List<String>>(){}.getType());
                                                    if (strings != null){
                                                        if (!strings.isEmpty()){
                                                            business.setPhone(strings.get(0));
                                                            Log.i(TAG, "The phone is : "+business.getPhone());
                                                        }
                                                    }
                                                    dataBase.businessDao().insert(connection.data.getBusinesses().get(i));
                                                    if (connection.data.getBusinesses().get(i).getTables().size()>0){
                                                        for(int j=0; j<business.getUsers().size(); j++){
                                                            User user = business.getUsers().get(j);
                                                            user.setRole_id(business.getUsers().get(j).getPivot().getBusiness_role_id()+"");
                                                            dataBase.userDao().insert(user);
                                                        }for(int j=0; j<business.getTables().size(); j++){
                                                            dataBase.guestTableDao().insert(business.getTables().get(j));
                                                        }
                                                        for(int j=0; j<business.getMenuItemCategories().size(); j++){
                                                            dataBase.menuItemCategoryDao().insert(business.getMenuItemCategories().get(j));
                                                            for(int k=0;k<business.getMenuItemCategories().get(j).getMenus().size(); k++){
                                                                dataBase.menuItemDao().insert(business.getMenu_categories().get(j).getMenus().get(k));
                                                            }
                                                        }

                                                        Log.i(TAG, "The business order is : "+business.getOrders().size());
                                                        for(int j=0;j<business.getOrders().size(); j++){
                                                            dataBase.orderDao().insert(business.getOrders().get(j));
                                                            for (int k=0; k<business.getOrders().get(j).getCourseList().size(); k++){
                                                                dataBase.courseDao().insert(business.getOrders().get(j).getCourseList().get(k));
                                                                for (int l=0; l<business.getOrders().get(j).getCourseList().get(k).getOrderItems().size(); l++){
                                                                    dataBase.orderItemDao().insert(business.getOrders().get(j).getCourseList().get(k).getOrderItems().get(l));
                                                                }
                                                            }
                                                        }

                                                        Log.i(TAG, "The business order is : "+dataBase.orderDao().getAllOrders().size());
                                                        Log.i(TAG, gson.toJson(dataBase.orderDao().getAllOrders()));
                                                        Log.i(TAG, gson.toJson(dataBase.guestTableDao().getAllGuestTables()));
                                                    }
                                                }
                                            }

                                            Toast.makeText(requireContext(), R.string.connection_successful, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(requireContext(), DashboardActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            requireActivity().finish();
                                        }
                                        else{
                                            Toast.makeText(requireContext(),R.string.credentials_do_not_match_records, Toast.LENGTH_LONG).show();
                                        }
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
                    }else {
                        Toast.makeText(requireContext(),R.string.password_must_digit_4_char, Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(requireContext(),R.string.password_must_digit_4_char, Toast.LENGTH_LONG).show();
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
