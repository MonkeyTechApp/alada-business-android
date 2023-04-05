package com.poupock.feussom.aladabusiness.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.callback.PrinterSelectedCallback;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.DialogEpsonPrinterListBinding;
import com.poupock.feussom.aladabusiness.databinding.OrderItemUpdateQtyFragmentBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.PrinterRecycleAdapter;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.PrinterDevice;
import com.poupock.feussom.aladabusiness.util.ShowMsg;
import com.poupock.feussom.aladabusiness.web.PostTask;
import com.poupock.feussom.aladabusiness.web.ServerUrl;
import com.poupock.feussom.aladabusiness.web.response.DatumResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EpsonPrintersListDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = EpsonPrintersListDialogFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private int mParam1;
    static PrinterSelectedCallback mCallback;


    public static EpsonPrintersListDialogFragment newInstance(int param1, PrinterSelectedCallback callback) {
        EpsonPrintersListDialogFragment fragment = new EpsonPrintersListDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        mCallback = callback;
        fragment.setArguments(args);
        return fragment;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    DialogEpsonPrinterListBinding binding;
    OrderViewModel viewModel;
    private FilterOption mFilterOption = null;
//    private ArrayList<HashMap<String, String>> mPrinterList = null;
//    private SimpleAdapter mPrinterListAdapter = null;
    private List<DeviceInfo> deviceInfos = null;
    private PrinterRecycleAdapter printerRecycleAdapter = null;

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
        binding = DialogEpsonPrinterListBinding.inflate(inflater, container, false);
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

    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    Log.i(TAG, "Device found!");
                    binding.progressBar.setVisibility(View.GONE);
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("PrinterName", deviceInfo.getDeviceName());
                    item.put("Target", deviceInfo.getTarget());
                    deviceInfos.add(deviceInfo);
                    printerRecycleAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        OrderItem orderItem = AppDataBase.getInstance(requireContext()).orderItemDao().getSpecificOrderItem(mParam1);
        Log.i(TAG,"The view created");
        binding.progressBar.setVisibility(View.VISIBLE);

        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        mFilterOption.setUsbDeviceName(Discovery.TRUE);

//        mPrinterList = new ArrayList<HashMap<String, String>>();
//
//        mPrinterListAdapter = new SimpleAdapter(requireContext(), mPrinterList, R.layout.list_at,
//                new String[] { "PrinterName", "Target" },
//                new int[] { R.id.PrinterName, R.id.Target });

        deviceInfos = new ArrayList<>();
        printerRecycleAdapter = new PrinterRecycleAdapter(requireContext(), deviceInfos, mCallback);
        binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.list.setAdapter(printerRecycleAdapter);

        try {
            Discovery.start(requireContext(), mFilterOption, mDiscoveryListener);
            Log.i(TAG, "The discovery started");
        }
        catch (Exception e) {
            Log.i(TAG, "The ex is : "+e.toString());
            ShowMsg.showException(e, "start", requireContext());
        }

    }

    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    ShowMsg.showException(e, "stop", requireContext());
                    return;
                }
            }
        }

        deviceInfos.clear();
        printerRecycleAdapter.notifyDataSetChanged();

        try {
            Discovery.start(requireContext(), mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "stop", requireContext());
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }

        mFilterOption = null;
    }
}
