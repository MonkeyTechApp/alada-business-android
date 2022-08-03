package com.poupock.feussom.aladabusiness.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.core.menu.ListFragment;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.DialogListBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.GuestTableAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderAdapter;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.Order;

public class ListDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = ListDialogFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static ListItemClickCallback mCallback;

    public static ListDialogFragment newInstance(String param1, String param2, ListItemClickCallback callback) {
        ListDialogFragment fragment = new ListDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        mCallback= callback;
        fragment.setArguments(args);
        return fragment;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    DialogListBinding binding;
    OrderViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        binding = DialogListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        Log.i(TAG,"The view created");

        if(mParam1.equalsIgnoreCase(GuestTable.class.getSimpleName()))
        {
            binding.list.setLayoutManager(new GridLayoutManager(requireContext(),2));
            binding.list.setAdapter(new GuestTableAdapter(
                requireContext(),
                AppDataBase.getInstance(requireContext()).guestTableDao().getAllGuestTables(),
                new ListItemClickCallback() {
                    @Override
                    public void onItemClickListener(Object o, boolean isLongClick) {
                        if(!isLongClick){
                            Gson gson = new Gson();
                            viewModel.setGuestTableMutableLiveData(gson.fromJson(gson.toJson(o),GuestTable.class));

                        }
                        dismiss();
                    }
                }
            ));
        }
        else if (mParam1.equalsIgnoreCase(Order.class.getSimpleName())){
            binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
            try {
                binding.list.setAdapter(new OrderAdapter(requireContext(),
                    AppDataBase.getInstance(requireContext()).orderDao().getTableOrders(
                        Integer.parseInt(mParam2), Constant.STATUS_OPEN)
                    , false, new ListItemClickCallback() {
                    @Override
                    public void onItemClickListener(Object o, boolean isLong) {
                        mCallback.onItemClickListener(o, isLong);
                        dismiss();
                    }
                }));
            }catch (NumberFormatException ex){
                Toast.makeText(requireContext(), R.string.error_retrieving_orders, Toast.LENGTH_LONG).show();
                dismiss();
            }

        }
    }
}
