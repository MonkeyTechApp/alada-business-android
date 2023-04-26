package com.poupock.feussom.aladabusiness.ui.fragment.orderdetail;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.core.profile.ProfileViewModel;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.OrderDetailFragmentBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderItemAdapter;
import com.poupock.feussom.aladabusiness.ui.dialog.ConfirmationCodeDialogFragment;
import com.poupock.feussom.aladabusiness.util.Course;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.util.HashMap;
import java.util.List;

public class OrderDetailFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private OrderDetailFragmentBinding binding;

    public static OrderDetailFragment newInstance() {
        return new OrderDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        profileViewModel =
            new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        binding = OrderDetailFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.txtCode.setText(profileViewModel.getOrderMutableLiveData().getValue().getCode());
        binding.txtTable.setText(AppDataBase.getInstance(requireContext()).guestTableDao().getSpecificGuestTable(profileViewModel.getOrderMutableLiveData().getValue().getGuest_table_id()).getTitle());
        binding.txtTime.setText(profileViewModel.getOrderMutableLiveData().getValue().getCreated_at());
        binding.txtTotal.setText(profileViewModel.getOrderMutableLiveData().getValue().getTotal()+" CFA");

        binding.listDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listDetails.setAdapter(new OrderItemAdapter(requireContext(),
                profileViewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems(),
                new DialogCallback() {
                    @Override
                    public void onActionClicked(Object o, int action) {

                    }
                }));

//        binding.btnAction.setText(R.string.delete);
        binding.btnAction.setBackgroundColor(getResources().getColor(R.color.flat_red_secondary));
        binding.btnAction.setImageResource(R.drawable.ic_baseline_delete_outline_24);

        binding.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = ConfirmationCodeDialogFragment.newInstance(1,
                        new DialogCallback() {
                            @Override
                            public void onActionClicked(Object o, int action) {
                                if (action == DialogCallback.DONE){
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            switch (i){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    delete(o.toString(), profileViewModel.getOrderMutableLiveData().getValue());
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    dialogInterface.dismiss();
                                                    break;
                                            }
                                        }
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setMessage(getString(R.string.confirm_order_deletion)).setPositiveButton(getString(R.string.yes),
                                                    dialogClickListener)
                                            .setNegativeButton(getString(R.string.no), dialogClickListener).show();
                                }
                            }
                        });
                dialogFragment.show(getParentFragmentManager(), ConfirmationCodeDialogFragment.class.getSimpleName());
            }
        });
    }

    void delete(String code, Order order){
        HashMap<String, String> map = new HashMap<>();
        map.put("order", order.getCode()+"");
        map.put("code", code+"");


        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage(getString(R.string.deleting_order_3_dots));
        progressDialog.setCancelable(false);

        Log.i(PostTask.class.getSimpleName(), "The params are : "+ map.toString());
        new PostTask(requireContext(), ServerUrl.ORDER_DEL, map,
                new VolleyRequestCallback() {
                    @Override
                    public void onStart() {
                        progressDialog.show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
                        if (datumResponse.success){
                            AppDataBase.getInstance(requireContext()).orderDao().delete(order);
                            deleteItems(order.getId());
                            requireActivity().onBackPressed();
                            Toast.makeText(requireContext(), R.string.order_deleted_successs, Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (datumResponse.data == null){
                                AppDataBase.getInstance(requireContext()).orderDao().delete(order);
                                deleteItems(order.getId());
                                requireActivity().onBackPressed();
                                Toast.makeText(requireContext(), R.string.order_deleted_successs, Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(requireContext(), R.string.order_not_deleted, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onJobFinished() {
                        progressDialog.dismiss();
                    }
                }).execute();
    }

    void deleteItems(int id){
        List<Course> courses = AppDataBase.getInstance(requireContext()).courseDao().getOrderCourses(id);
        for(int j=0; j<courses.size(); j++ ){
            AppDataBase.getInstance(requireContext()).courseDao().delete(courses.get(j));
            List<OrderItem> orderItems = AppDataBase.getInstance(requireContext()).orderItemDao().getAllCourseItems(courses.get(j).getId());
            for (int i=0; i<orderItems.size(); i++)
                AppDataBase.getInstance(requireContext()).orderItemDao().delete(orderItems.get(i));
        }

    }
}
