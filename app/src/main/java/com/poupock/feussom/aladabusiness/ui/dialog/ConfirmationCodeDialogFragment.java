package com.poupock.feussom.aladabusiness.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.DialogConfirmationCodeBinding;
import com.poupock.feussom.aladabusiness.databinding.OrderItemUpdateQtyFragmentBinding;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.util.HashMap;
import java.util.Objects;

public class ConfirmationCodeDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = ConfirmationCodeDialogFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private int mParam1;
    static DialogCallback mCallback;

    public static ConfirmationCodeDialogFragment newInstance(int param1, DialogCallback callback) {
        ConfirmationCodeDialogFragment fragment = new ConfirmationCodeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        mCallback = callback;
        fragment.setArguments(args);
        return fragment;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    DialogConfirmationCodeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        binding = DialogConfirmationCodeBinding.inflate(inflater, container, false);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.codeTextField.getEditText().getText().toString().trim().isEmpty()){
                    dismiss();
                    mCallback.onActionClicked(binding.codeTextField.getEditText().getText().toString().trim(), DialogCallback.DONE);
                }else{
                    binding.codeTextField.setError(getString(R.string.provide_info));
                    binding.codeTextField.setErrorEnabled(true);
                }
            }
        });

        binding.codeTextField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.codeTextField.setErrorEnabled(false);
            }
        });
    }

}
