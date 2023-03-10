package com.poupock.feussom.aladabusiness.core.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.core.table.TableListFragment;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.FragmentUserListBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.UserAdapter;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.User;

public class UserListFragment extends Fragment {

    private FragmentUserListBinding binding;
    UserActivityViewModel viewModel;
    UserAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(UserActivityViewModel.class);

        adapter = new UserAdapter(requireContext(), AppDataBase.getInstance(requireContext()).userDao().getAllUsers(), true,
                new DialogCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onActionClicked(Object o, int action) {
                        User datum = User.getObjectFromObject(o);
                        if(action == Constant.ACTION_EDIT){
                            viewModel.setIsEditMutableLiveData(true);
                            viewModel.setUserMutableLiveData(datum);
                            NavHostFragment.findNavController(UserListFragment.this)
                                    .navigate(R.id.action_user_list_fragment_to_user_create_fragment);
                        }
                        else if (action == Constant.ACTION_DELETE){
                            AppDataBase.getInstance(requireContext()).userDao().delete(datum);
                            adapter.setUsers(AppDataBase.getInstance(requireContext()).userDao().getAllUsers());
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        binding.list.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.list.setAdapter(adapter);
        binding.btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserListFragment.this)
                        .navigate(R.id.action_user_list_fragment_to_user_create_fragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
