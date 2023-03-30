package com.poupock.feussom.aladabusiness.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.OrderItemUpdateQtyFragmentBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderItemAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.PrintAdapter;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderItemUpdateQuantityDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = OrderItemUpdateQuantityDialogFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private int mParam1;
    private Bitmap bitmap;
    private boolean boolean_save;
    private PdfPCell cell;
    private Image imgReportLogo;
    static DialogCallback mCallback;

    BaseColor tableHeadColor = WebColors.getRGBColor("#000000");

    public static OrderItemUpdateQuantityDialogFragment newInstance(int param1,DialogCallback callback) {
        OrderItemUpdateQuantityDialogFragment fragment = new OrderItemUpdateQuantityDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        mCallback = callback;
        fragment.setArguments(args);
        return fragment;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    OrderItemUpdateQtyFragmentBinding binding;
    OrderViewModel viewModel;

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
        binding = OrderItemUpdateQtyFragmentBinding.inflate(inflater, container, false);
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

        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        OrderItem orderItem = AppDataBase.getInstance(requireContext()).orderItemDao().getSpecificOrderItem(mParam1);
        Log.i(TAG,"The view created");
        binding.textView1.setText(getString(R.string.order_item)+" : "+
                        AppDataBase.getInstance(requireContext()).menuItemDao().getSpecificMenuItem(orderItem.getMenu_item_id()).getName());

        Objects.requireNonNull(binding.qtyTextField.getEditText()).setText(orderItem.getQuantity()+"");

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int qty = Integer.parseInt(binding.qtyTextField.getEditText().getText().toString().trim());
                    if (qty > 0){
                        if (qty != orderItem.getQuantity()){

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:

                                            HashMap<String, String> map = new HashMap<>();
                                            map.put("course_id", orderItem.getCourse_id()+"");
                                            map.put("menu_item_id", orderItem.getMenu_item_id()+"");
                                            map.put("quantity", qty+"");

                                            Log.i(TAG, "The map is "+map.toString());
                                            ProgressDialog progressDialog = new ProgressDialog(requireContext());
                                            progressDialog.setMessage(getString(R.string.updating_order_item_3_dot));
                                            progressDialog.setCancelable(false);
                                            new PostTask(requireContext(), ServerUrl.ORDER_ITEM_UPD, map,
                                                    new VolleyRequestCallback() {
                                                        @Override
                                                        public void onStart() {
                                                            progressDialog.show();
                                                        }

                                                        @Override
                                                        public void onSuccess(String response) {
                                                            DatumResponse datumResponse = new Gson().fromJson(response, DatumResponse.class);
                                                            orderItem.setQuantity(qty);
                                                            if (datumResponse.success){
                                                                AppDataBase.getInstance(requireContext()).orderItemDao().update(orderItem);
                                                                mCallback.onActionClicked(orderItem, DialogCallback.DONE);
                                                                Toast.makeText(requireContext(), R.string.order_item_updated_successs, Toast.LENGTH_LONG).show();
                                                            }
                                                            else {
                                                                if (datumResponse.data == null){
                                                                    AppDataBase.getInstance(requireContext()).orderItemDao().update(orderItem);
                                                                    mCallback.onActionClicked(orderItem, DialogCallback.DONE);
                                                                    Toast.makeText(requireContext(), R.string.order_item_updated_successs, Toast.LENGTH_LONG).show();
                                                                }
                                                                Toast.makeText(requireContext(), R.string.order_item_not_updated, Toast.LENGTH_LONG).show();
                                                            }
                                                            OrderItemUpdateQuantityDialogFragment.this.dismiss();
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

                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setMessage(getString(R.string.confirm_order_item_update)+" : "
                                            + AppDataBase.getInstance(requireContext()).menuItemDao().getSpecificMenuItem(orderItem.getMenu_item_id()).getName()
                                            +" "+getString(R.string.with_quantity)+" "+ qty)
                                    .setPositiveButton(getString(R.string.yes), dialogClickListener)
                                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();
                        }else {
                            Toast.makeText(requireContext(), R.string.warning_same_quantity, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(requireContext(), R.string.quantity_must_be_number_n_greater_than_0, Toast.LENGTH_LONG).show();
                    }
                }catch (NumberFormatException ex){
                    Toast.makeText(requireContext(), R.string.quantity_must_be_number_n_greater_than_0, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
