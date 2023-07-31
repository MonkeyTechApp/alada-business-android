package com.poupock.feussom.aladabusiness.ui.dialog;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.BLDeviceClickCallback;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.databinding.DialogListBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.BluetoothDeviceAdapter;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;

import java.util.List;

public class BTDeviceDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = BTDeviceDialogFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private List<BluetoothDevice> mParam1;
    private String mParam2;
    private static BLDeviceClickCallback mCallback;

    public static BTDeviceDialogFragment newInstance(List<BluetoothDevice> param1, String param2, BLDeviceClickCallback callback) {
        BTDeviceDialogFragment fragment = new BTDeviceDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, new Gson().toJson(param1));
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
            mParam1 = new Gson().fromJson(getArguments().getString(ARG_PARAM1), new TypeToken<List<BluetoothDevice>>(){}.getType());
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

        binding.text.setText(R.string.device_list);
        binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.list.setAdapter(new BluetoothDeviceAdapter(requireContext(), mParam1, mCallback, new DialogCallback() {
            @Override
            public void onActionClicked(Object o, int action) {
                dismiss();
            }
        }));
    }
}
