package com.poupock.feussom.aladabusiness.ui.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.Table;
import com.android.print.sdk.bluetooth.BluetoothPort;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.iposprinter.iposprinterservice.IPosPrinterCallback;
import com.iposprinter.iposprinterservice.IPosPrinterService;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.bluetooth.BluetoothOperation;
import com.poupock.feussom.aladabusiness.callback.BLDeviceClickCallback;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.PrinterSelectedCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.OrderDetailFragmentBinding;
import com.poupock.feussom.aladabusiness.posq2.ThreadPoolManager;
import com.poupock.feussom.aladabusiness.posq2.Utils.ButtonDelayUtils;
import com.poupock.feussom.aladabusiness.posq2.Utils.HandlerUtils;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderItemAdapter;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.ShowMsg;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OrderDetailDialogFragment extends DialogFragment implements ReceiveListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = OrderDetailDialogFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Bitmap bitmap;
    private boolean boolean_save;
    private Printer epsonPrinter = null;
    private static final int DISCONNECT_INTERVAL = 500;

    private IPosPrinterService mIPosPrinterService;
    private HandlerUtils.MyHandler handler;
    private IPosPrinterCallback callback = null;

    /*定义打印机状态*/
    private final int PRINTER_NORMAL = 0;
    private final int PRINTER_PAPERLESS = 1;
    private final int PRINTER_THP_HIGH_TEMPERATURE = 2;
    private final int PRINTER_MOTOR_HIGH_TEMPERATURE = 3;
    private final int PRINTER_IS_BUSY = 4;
    private final int PRINTER_ERROR_UNKNOWN = 5;
    /*打印机当前状态*/
    private int printerStatus = 0;

    /*定义状态广播*/
    private final String  PRINTER_NORMAL_ACTION = "com.iposprinter.iposprinterservice.NORMAL_ACTION";
    private final String  PRINTER_PAPERLESS_ACTION = "com.iposprinter.iposprinterservice.PAPERLESS_ACTION";
    private final String  PRINTER_PAPEREXISTS_ACTION = "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION";
    private final String  PRINTER_THP_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION";
    private final String  PRINTER_THP_NORMALTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_NORMALTEMP_ACTION";
    private final String  PRINTER_MOTOR_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION";
    private final String  PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION";
    private final String  PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION = "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION";

    /*定义消息*/
    private final int MSG_TEST                               = 1;
    private final int MSG_IS_NORMAL                          = 2;
    private final int MSG_IS_BUSY                            = 3;
    private final int MSG_PAPER_LESS                         = 4;
    private final int MSG_PAPER_EXISTS                       = 5;
    private final int MSG_THP_HIGH_TEMP                      = 6;
    private final int MSG_THP_TEMP_NORMAL                    = 7;
    private final int MSG_MOTOR_HIGH_TEMP                    = 8;
    private final int MSG_MOTOR_HIGH_TEMP_INIT_PRINTER       = 9;
    private final int MSG_CURRENT_TASK_PRINT_COMPLETE     = 10;

    private static final int REQUEST_BL_PERMISSION_CODE = 0x50;

    /*循环打印类型*/
    private final int  MULTI_THREAD_LOOP_PRINT  = 1;
    private final int  INPUT_CONTENT_LOOP_PRINT = 2;
    private final int  DEMO_LOOP_PRINT          = 3;
    private final int  PRINT_DRIVER_ERROR_TEST  = 4;
    private final int  DEFAULT_LOOP_PRINT       = 0;

    //循环打印标志位
    private       int  loopPrintFlag            = DEFAULT_LOOP_PRINT;
    private       byte loopContent              = 0x00;
    private       int  printDriverTestCount     = 0;


    public static OrderDetailDialogFragment newInstance(String param1, String param2) {
        OrderDetailDialogFragment fragment = new OrderDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    OrderDetailFragmentBinding binding;
    OrderViewModel viewModel;

    private boolean isMobilePrinterConnected;

    private PrinterInstance mobilePrinter;

    private MyTask myTask;

    /**
     * wifi机器需要定时读取打印机数据, 如下代码
     * 当连接断开时, 读取数据 read() 会触发断开连接的消息
     *
     * USB 蓝牙 可忽略
     */
    private class MyTask extends java.util.TimerTask{
        @Override
        public void run() {
            if(isMobilePrinterConnected && mobilePrinter != null) {
                byte[] by = mobilePrinter.read();
                if (by != null) {
                    System.out.println(mobilePrinter.isConnected() + " read byte " + Arrays.toString(by));
                }
            }
        }
    }

    private class ExitTask extends java.util.TimerTask{
        @Override
        public void run() {
            Log.i("SDK TEST", "Exiting");
            try {
                Objects.requireNonNull(viewModel.getOrderMutableLiveData().getValue()).setStatus(Constant.STATUS_CLOSED);
                long timeValue = (new Date().getTime());
                viewModel.getOrderMutableLiveData().getValue().setUpdated_at(timeValue);
                for (int i=0; i<viewModel.getOrderMutableLiveData().getValue().getCourses().size(); i++){
                    viewModel.getOrderMutableLiveData().getValue().getCourses().get(i).setUpdated_at(timeValue);
                    for (int j=0; j<viewModel.getOrderMutableLiveData().getValue().getCourses().get(i).getItems().size(); j++){
                        viewModel.getOrderMutableLiveData().getValue().getCourses().get(i).getOrderItems().get(j).setUpdated_at(timeValue);
                        AppDataBase.getInstance(requireContext()).orderItemDao().update(viewModel.getOrderMutableLiveData().getValue().getCourses().get(i).getOrderItems().get(j));
                    }
                    AppDataBase.getInstance(requireContext()).courseDao().update(viewModel.getOrderMutableLiveData().getValue().getCourses().get(i));
                }
                AppDataBase.getInstance(requireContext()).orderDao().update(viewModel.getOrderMutableLiveData().getValue());
                OrderDetailDialogFragment.this.dismiss();
                if (mobilePrinter.isConnected())
                    mobilePrinter.closeConnection();
            }catch (IllegalStateException ex){
            }

        }
    }
    private BluetoothOperation bluetoothOperation;
    private Handler mobilePrinterHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isMobilePrinterConnected = true;
//                    mobilePrinter = bluetoothOperation.getPrinter();
                    if (mobilePrinter == null){
                        Log.i("SDK TEST", "Printer set back to null");
                    }else Log.i("SDK TEST", "Printer not set back to null");
                    java.util.Timer timer = new java.util.Timer();
                    myTask = new MyTask();
                    timer.schedule(myTask, 0, 2000);
                    Log.i("SDK TEST", "BT connected");
//                    Toast.makeText(requireContext(), R.string.yesconn, Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.FAILED:
                    if(myTask != null){
                        myTask.cancel();
                    }
                    isMobilePrinterConnected = false;
                    Log.i("SDK TEST", "BT failed");
//                    Toast.makeText(requireContext(), R.string.conn_failed, Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.CLOSED:
                    if(myTask != null){
                        myTask.cancel();
                    }
                    isMobilePrinterConnected = false;
                    Log.i("SDK TEST", "BT closed");
//                    Toast.makeText(requireContext(), R.string.conn_closed, Toast.LENGTH_SHORT).show();

                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isMobilePrinterConnected = false;
                    Log.i("SDK TEST", "BT no device");
//                    Toast.makeText(requireContext(), R.string.conn_no, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };

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
        binding = OrderDetailFragmentBinding.inflate(inflater, container, false);
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

    public static boolean runtimeBLConnectPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BL_PERMISSION_CODE);
                Log.i("SDK TEST","Requesting perm");
            }else {
                Log.i("SDK TEST","Perm not requested.");
            }

            return true;
        } else return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        Log.i(TAG,"The view created");

        runtimeBLConnectPermissions(requireActivity());

        binding.txtCode.setText(viewModel.getOrderMutableLiveData().getValue().getCode());
        binding.txtTable.setText(AppDataBase.getInstance(requireContext()).guestTableDao().getSpecificGuestTable(viewModel.getOrderMutableLiveData().getValue().getGuest_table_id()).getTitle());
        try {
            String d  = viewModel.getOrderMutableLiveData().getValue().getCreated_at().replaceAll("T", " ");
            binding.txtTime.setText(d.substring(0, d.indexOf(".")));
        }catch (StringIndexOutOfBoundsException e){
            binding.txtTime.setText(viewModel.getOrderMutableLiveData().getValue().getCreated_at().replaceAll("T", " "));
        }

        binding.txtTotal.setText(viewModel.getOrderMutableLiveData().getValue().getTotal()+" CFA");

        binding.listDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listDetails.setAdapter(new OrderItemAdapter(requireContext(),
                viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems(),
                new DialogCallback() {
                    @Override
                    public void onActionClicked(Object o, int action) {

                    }
                }));

        binding.btnAction.setImageResource(R.drawable.ic_baseline_print_24);

        initializeObject();
        connectService = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIPosPrinterService = IPosPrinterService.Stub.asInterface(service);
                binding.btnAction.setEnabled(true);
                Toast.makeText(requireContext(),"Service connected!!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                FirebaseCrashlytics.getInstance().recordException(new Exception("Service set to null"));
                Toast.makeText(requireContext(),"Service disconnected!!", Toast.LENGTH_LONG).show();
                mIPosPrinterService = null;
            }

            @Override
            public void onNullBinding(ComponentName name) {
                ServiceConnection.super.onNullBinding(name);
            }

            @Override
            public void onBindingDied(ComponentName name) {
                ServiceConnection.super.onBindingDied(name);
            }
        };
        printerInit();

        handler = new HandlerUtils.MyHandler(iHandlerIntent);

        callback = new IPosPrinterCallback.Stub() {

            @Override
            public void onRunResult(final boolean isSuccess) throws RemoteException {
                Log.i(TAG,"result:" + isSuccess + "\n");
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG,"result:" + value + "\n");
            }
        };

        //绑定服务
        Intent intent=new Intent();
        intent.setPackage("com.iposprinter.iposprinterservice");
        intent.setAction("com.iposprinter.iposprinterservice.IPosPrintService");
        requireActivity().startService(intent);

        requireActivity().bindService(intent, connectService, Context.BIND_AUTO_CREATE);

        //注册打印机状态接收器
        IntentFilter printerStatusFilter = new IntentFilter();
        printerStatusFilter.addAction(PRINTER_NORMAL_ACTION);
        printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION);
        printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION);
        printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_BUSY_ACTION);

        requireActivity().registerReceiver(IPosPrinterStatusListener,printerStatusFilter);
        if (ButtonDelayUtils.isFastDoubleClick())
        {
            return;
        }

        FirebaseCrashlytics.getInstance().recordException(new Exception("Actions initiated!"));

        binding.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String printOption = User.getPrinterOption(requireContext());
                if(printOption == null){
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    User.storePrinterOption(User.EPSON, requireContext());
                                    if (User.getPrinterModelTarget(requireContext()) == null){
                                        DialogFragment epsonDiscoverDialog = EpsonPrintersListDialogFragment.newInstance(1,
                                                new PrinterSelectedCallback() {
                                                    @Override
                                                    public void onItemClickListener(DeviceInfo o, boolean isLong) {
                                                        Log.i(TAG, "Printer selected!!");
                                                        User.storePrinterModelTarget(o.getTarget(), requireContext());
                                                    }
                                                });
                                        epsonDiscoverDialog.show(getParentFragmentManager(), EpsonPrintersListDialogFragment.class.getSimpleName());
                                    }
                                    else {
                                        if (!runPrintReceiptSequence()) {
                                            dismiss();
                                            updateButtonState(true);
                                        }else{
                                            Log.i(TAG, "Printing sequence FALSE");
                                        }
                                    }
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    User.storePrinterOption(User.I_POS_PRINTER, requireContext());
                                    if (getPrinterStatus() == PRINTER_NORMAL){
                                        printText(viewModel.getOrderMutableLiveData().getValue(),
                                                viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems());
                                    }
//                                    dialog.dismiss();
                                case DialogInterface.BUTTON_NEUTRAL:
                                    User.storePrinterOption(User.MOBILE_PRINTER, requireContext());
                                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        Log.i("SDK TEST", "Permission connect to be called");
//            return;
                                    }
                                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                                    List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
                                    for(BluetoothDevice bt : pairedDevices){
                                        devices.add(bt);
                                        Log.i("SDK TEST", "ADD : "+ bt.getAddress()+" the name is : "+bt.getName()+" the ... : ");
                                    }

                                    DialogFragment btDeviceDialogFragment = BTDeviceDialogFragment.newInstance(devices, "",
                                            new BLDeviceClickCallback() {
                                                @Override
                                                public void onItemClickListener(BluetoothDevice o, boolean isLong) {
                                                    BluetoothDevice bluetoothDevice = new Gson().fromJson(o.toString(), BluetoothDevice.class);
                                                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                                                    mobilePrinter = new BluetoothPort().btConnnect(requireContext(), bluetoothDevice.getAddress(), adapter, mobilePrinterHandler);
                                                    User.storeMobilePrinterAddress(bluetoothDevice.getAddress(), requireContext());
                                                    mobilePrinterPrint();

                                                }
                                            });
                                    btDeviceDialogFragment.show(getParentFragmentManager(), BTDeviceDialogFragment.class.getSimpleName());


                                    dialog.dismiss();
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage(getString(R.string.select_printer_option)).setPositiveButton(getString(R.string.epson_printer), dialogClickListener)
                            .setNeutralButton(getString(R.string.mobile_printer), dialogClickListener)
                            .setNegativeButton(getString(R.string.ipos_printer), dialogClickListener).show();
                }
                else {
                    if (printOption.equalsIgnoreCase(User.EPSON)){
                        if (User.getPrinterModelTarget(requireContext()) == null){
                            DialogFragment epsonDiscoverDialog = EpsonPrintersListDialogFragment.newInstance(1,
                                    new PrinterSelectedCallback() {
                                        @Override
                                        public void onItemClickListener(DeviceInfo o, boolean isLong) {
                                            Log.i(TAG, "Printer selected!!");
                                            User.storePrinterModelTarget(o.getTarget(), requireContext());
                                        }
                                    });
                            epsonDiscoverDialog.show(getParentFragmentManager(), EpsonPrintersListDialogFragment.class.getSimpleName());
                        }
                        else {
                            if (!runPrintReceiptSequence()) {
                                dismiss();
                                updateButtonState(true);
                            }else{
                                Log.i(TAG, "Printing sequence FALSE");
                            }
                        }
                    }
                    else if (printOption.equalsIgnoreCase(User.I_POS_PRINTER)){
                        if (getPrinterStatus() == PRINTER_NORMAL){
                            printText(viewModel.getOrderMutableLiveData().getValue(),
                                    viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems());
                        }
                    }
                    else if(printOption.equalsIgnoreCase(User.MOBILE_PRINTER)){
                        bluetoothOperation = new BluetoothOperation(requireContext(), mobilePrinterHandler);
//                        bluetoothOperation.btAutoConn(requireContext(),  mobilePrinterHandler);

                        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Log.i("SDK TEST", "Permission connect to be called");
                        }


                        if (User.getPrinterBtAddress(requireContext()) == null){
                            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                            List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
                            for(BluetoothDevice bt : pairedDevices){
                                devices.add(bt);
                                Log.i("SDK TEST", "ADD : "+ bt.getAddress()+" the name is : "+bt.getName()+" the ... : ");
                            }
                            DialogFragment btDeviceDialogFragment = BTDeviceDialogFragment.newInstance(devices, "",
                                    new BLDeviceClickCallback() {
                                        @Override
                                        public void onItemClickListener(BluetoothDevice device, boolean isLong) {
                                            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                                            mobilePrinter = new BluetoothPort().btConnnect(requireContext(), device.getAddress(), adapter, mobilePrinterHandler);
                                            mobilePrinterPrint();
                                        }
                                    });
                            btDeviceDialogFragment.show(getParentFragmentManager(), BTDeviceDialogFragment.class.getSimpleName());
                        }
                        else {
                            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                            if (mobilePrinter == null){
                                Log.i("SDK TEST", "Printer is null");
                                mobilePrinter = new BluetoothPort().btConnnect(requireContext(), User.getPrinterBtAddress(requireContext()), adapter, mobilePrinterHandler);

                            }else if (!mobilePrinter.isConnected()) {
                                mobilePrinter.openConnection();
                                Log.i("SDK TEST", "Printer not null");
                            }
                            mobilePrinterPrint();
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), R.string.print_option_not_valid, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /**
     * 打印机初始化
     */
    public void printerInit(){
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try{
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireContext(), "Printer init", Toast.LENGTH_LONG).show();
                        }
                    });
                    if (mIPosPrinterService != null){
                        mIPosPrinterService.printerInit(callback);
                    }
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void mobilePrinterPrint(){
        if(!isMobilePrinterConnected && mobilePrinter == null) {
            Log.i("SDK TEST", "Printer is null");
            return;
        }
        new Thread(){
            @Override
            public void run() {
               mobilePrinterPrint(mobilePrinter, viewModel.getOrderMutableLiveData().getValue(),
                       viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems());

            }
        }.start();
    }

    public void mobilePrinterPrint(PrinterInstance mPrinter, Order order, List<OrderItem> orderItems) {

        Log.i("SDK TEST", "Printing");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        Bitmap logoData = BitmapFactory.decodeFile(User.getPath(requireContext()), options);

        mPrinter.init();

        Business business = AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0);
        List<User> users = AppDataBase.getInstance(requireContext()).userDao().getAllUsers();
        String role = User.currentUser(requireContext()).getName();
        for(int i=0; i<users.size(); i++){
            if (users.get(i).getEmail().equals(User.currentUser(requireContext()).getEmail())){
                role =(users.get(i).getName());
                break;
            }
        }
        String finalRole = role;
        String date = "";
        try {
            date  = order.getCreated_at().replaceAll("T", " ");
            binding.txtTime.setText(date.substring(0, date.indexOf(".")));
        }catch (StringIndexOutOfBoundsException e){
            date = (order.getCreated_at().replaceAll("T", " "));
        }
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 1);
        if (logoData != null)
            mPrinter.printImage(logoData);
        mPrinter.setFont(0, 0, 1, 0);
        mPrinter.printText(business.getName()+"\n");
        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 1);
        mPrinter.printText(business.getPhone());
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText(business.getLocation()+"\n");
        mPrinter.printText(getString(R.string.cashier)+" : "+ finalRole +" \n");
        mPrinter.printText(getString(R.string.welcome)+" \n");
        mPrinter.printText("---------------------\n");
        mPrinter.setFont(0, 0, 0, 0);
        Table table = new Table(getString(R.string.item)+" : "+ getString(R.string.qty).replaceAll("é", "e")+" : "+getString(R.string.price), ":", new int[]{17,3,11});
        float total = 0;
        for (int i=0; i  < orderItems.size(); i++){
            total = (float) (total + (orderItems.get(i).getPrice() * orderItems.get(i).getQuantity()));
            table.addRow(AppDataBase.getInstance(requireContext()).menuItemDao().
                    getSpecificMenuItem(orderItems.get(i).getMenu_item_id()).getName()+" : "+
                    orderItems.get(i).getQuantity()+" : "+
                    (int) (orderItems.get(i).getQuantity() * orderItems.get(i).getPrice()));
        }
        table.addRow(getString(R.string.total)+": :"+(int)total+"XAF");
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 1);
        mPrinter.printText("---------------------\n");
        mPrinter.setFont(0, 0, 1, 0);
        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.printText("Code : "+ order.getCode()+"\n");
        mPrinter.printText( date+"\n");
        mPrinter.printText(getString(R.string.thank_u_for_visit)+"\n");
        mPrinter.printText("-----------------------\n\n\n");
        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, 0);
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new ExitTask(), 5000);

    }


    public int getPrinterStatus(){

        Log.i(TAG,"***** printerStatus"+printerStatus);
        try{
           printerStatus = mIPosPrinterService.getPrinterStatus();
        }catch (RemoteException e){
            e.printStackTrace();
        }
        Log.i(TAG,"#### printerStatus"+printerStatus);
        return  printerStatus;
    }

    public void loopPrint(int flag)
    {
        switch (flag)
        {
//            case MULTI_THREAD_LOOP_PRINT:
//                multiThreadLoopPrint();
//                break;
//            case DEMO_LOOP_PRINT:
//                demoLoopPrint();
//                break;
//            case INPUT_CONTENT_LOOP_PRINT:
//                bigDataPrintTest(127, loopContent);
//                break;
//            case PRINT_DRIVER_ERROR_TEST:
//                printDriverTest();
//                break;
            default:
                break;
        }
    }

    private HandlerUtils.IHandlerIntent iHandlerIntent = new HandlerUtils.IHandlerIntent()
    {
        @Override
        public void handlerIntent(Message msg)
        {
            if (isAdded()){
                switch (msg.what)
                {
                    case MSG_TEST:
                        break;
                    case MSG_IS_NORMAL:
                        if(getPrinterStatus() == PRINTER_NORMAL)
                        {
                            loopPrint(loopPrintFlag);
                        }
                        break;
                    case MSG_IS_BUSY:
                        Toast.makeText(requireContext(), R.string.printer_is_working, Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_PAPER_LESS:
                        loopPrintFlag = DEFAULT_LOOP_PRINT;
                        Toast.makeText(requireContext(), R.string.out_of_paper, Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_PAPER_EXISTS:
                        Toast.makeText(requireContext(), R.string.exists_paper, Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_THP_HIGH_TEMP:
                        Toast.makeText(requireContext(), R.string.printer_high_temp_alarm, Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_MOTOR_HIGH_TEMP:
                        loopPrintFlag = DEFAULT_LOOP_PRINT;
                        Toast.makeText(requireContext(), R.string.motor_high_temp_alarm, Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP_INIT_PRINTER, 180000);  //马达高温报警，等待3分钟后复位打印机
                        break;
                    case MSG_MOTOR_HIGH_TEMP_INIT_PRINTER:
                        printerInit();
                        break;
                    case MSG_CURRENT_TASK_PRINT_COMPLETE:
                        Toast.makeText(requireContext(), R.string.printer_current_task_print_complete, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

        }
    };

    public void printText(Order order, List<OrderItem> orderItems)
    {
        Business business = AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0);
        List<User> users = AppDataBase.getInstance(requireContext()).userDao().getAllUsers();
        String role = User.currentUser(requireContext()).getName();
        for(int i=0; i<users.size(); i++){
            if (users.get(i).getEmail().equals(User.currentUser(requireContext()).getEmail())){
//                binding.txtRole.setText();
                role =(users.get(i).getName());
                break;
            }
        }
        String finalRole = role;
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
//                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 3;
//                Bitmap logoData = BitmapFactory.decodeFile(User.getPath(requireContext()), options);

                try {
                    String date = "";
                    try {
                        date  = order.getCreated_at().replaceAll("T", " ");
                        binding.txtTime.setText(date.substring(0, date.indexOf(".")));
                    }catch (StringIndexOutOfBoundsException e){
                        date = (order.getCreated_at().replaceAll("T", " "));

                    }

//                    mIPosPrinterService.printBitmap(1, 12, logoData, callback);
                    mIPosPrinterService.printBlankLines(1, 16, callback);
                    mIPosPrinterService.printSpecifiedTypeText(business.getName().toUpperCase()+"  \n", "ST", 32, callback);
                    mIPosPrinterService.printBlankLines(1, 16, callback);
                    mIPosPrinterService.printSpecifiedTypeText(getString(R.string.phone)+" : "+business.getPhone()+"    \n", "ST", 24, callback);
                    mIPosPrinterService.printBlankLines(1, 8, callback);
                    mIPosPrinterService.printSpecifiedTypeText(getString(R.string.cashier)+" : "+ finalRole +" \n", "ST", 24, callback);
                    mIPosPrinterService.printBlankLines(1, 8, callback);
                    mIPosPrinterService.printSpecifiedTypeText(getString(R.string.order)+" : "+ order.getCode()+"\n", "ST", 24, callback);
                    mIPosPrinterService.printBlankLines(1, 8, callback);
                    mIPosPrinterService.printSpecifiedTypeText(getString(R.string.date)+" : "+ date+"\n", "ST", 24, callback);
                    mIPosPrinterService.printBlankLines(1, 8, callback);
                    mIPosPrinterService.printBlankLines(1, 8, callback);
                    mIPosPrinterService.printBlankLines(1, 16, callback);
                    mIPosPrinterService.printSpecifiedTypeText("********************************", "ST", 24, callback);
                    float total = 0;
                    StringBuilder textData = new StringBuilder();
                    for (int i = 0; i < orderItems.size(); i++) {
                        total = (float) (total + (orderItems.get(i).getPrice() * orderItems.get(i).getQuantity()));
                        textData.append(orderItems.get(i).getQuantity()).append(" ")
                                .append(AppDataBase.getInstance(requireContext()).menuItemDao().
                                        getSpecificMenuItem(orderItems.get(i).getMenu_item_id()).getName())
                                .append(" : ").append((int) (orderItems.get(i).getQuantity() * orderItems.get(i).getPrice()))
                                .append("\n");
                    }

                    mIPosPrinterService.printSpecifiedTypeText(textData.toString(), "ST", 24, callback);
                    mIPosPrinterService.printBlankLines(1, 16, callback);
                    mIPosPrinterService.PrintSpecFormatText(getString(R.string.total)+" : "+total+"F CFA \n", "ST", 32, 1, callback);
                    mIPosPrinterService.printSpecifiedTypeText("********************************", "ST", 24, callback);
                    mIPosPrinterService.setPrinterPrintAlignment(0,callback);
                    mIPosPrinterService.printBarCode(order.getCode(), 8, 2, 5, 0, callback);
                    mIPosPrinterService.printBlankLines(1, 16, callback);
                    mIPosPrinterService.printBlankLines(1, 16, callback);

                    mIPosPrinterService.PrintSpecFormatText("*"+getString(R.string.thank_u_for_visit)+"*\n"+getString(R.string.dont_hesitate_to_contact_us)+"", "ST", 32, 1,
                            callback);
//                    bitmapRecycle(logoData);
                    mIPosPrinterService.printerPerformPrint(160,  callback);



                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
        Objects.requireNonNull(order).setStatus(Constant.STATUS_CLOSED);
        long timeValue = (new Date().getTime());
        order.setUpdated_at(timeValue);
        for (int i=0; i<order.getCourses().size(); i++){
            order.getCourses().get(i).setUploaded_at(timeValue);
            order.getCourses().get(i).setUploaded_at(timeValue);
            for (int j=0; j<order.getCourses().get(i).getItems().size(); j++){
                order.getCourses().get(i).getOrderItems().get(j).setUpdated_at(timeValue);
                AppDataBase.getInstance(requireContext()).orderItemDao().update(order.getCourses().get(i).getOrderItems().get(j));
            }
            AppDataBase.getInstance(requireContext()).courseDao().update(order.getCourses().get(i));
        }
        AppDataBase.getInstance(requireContext()).orderDao().update(order);
//        requireActivity().onBackPressed();
        Toast.makeText(requireContext(), R.string.order_printed_success, Toast.LENGTH_LONG).show();

//        java.util.Timer timer = new java.util.Timer();
//        timer.schedule(new ExitTask(), 5000);
    }


    private BroadcastReceiver IPosPrinterStatusListener = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(action == null)
            {
                Log.d(TAG,"IPosPrinterStatusListener onReceive action = null");
                return;
            }
            Log.d(TAG,"IPosPrinterStatusListener action = "+action);
            if(action.equals(PRINTER_NORMAL_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_IS_NORMAL,0);
            }
            else if (action.equals(PRINTER_PAPERLESS_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_PAPER_LESS,0);
            }
            else if (action.equals(PRINTER_BUSY_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_IS_BUSY,0);
            }
            else if (action.equals(PRINTER_PAPEREXISTS_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_PAPER_EXISTS,0);
            }
            else if (action.equals(PRINTER_THP_HIGHTEMP_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_THP_HIGH_TEMP,0);
            }
            else if (action.equals(PRINTER_THP_NORMALTEMP_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_THP_TEMP_NORMAL,0);
            }
            else if (action.equals(PRINTER_MOTOR_HIGHTEMP_ACTION))  //此时当前任务会继续打印，完成当前任务后，请等待2分钟以上时间，继续下一个打印任务
            {
                handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP,0);
            }
            else if(action.equals(PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_CURRENT_TASK_PRINT_COMPLETE,0);
            }
            else
            {
                handler.sendEmptyMessageDelayed(MSG_TEST,0);
            }
        }
    };

    private ServiceConnection connectService = null;

    private boolean runPrintReceiptSequence() {

        if (!createReceiptData(viewModel.getOrderMutableLiveData().getValue(),
                viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems())) {
            return false;
        }

        if (!printData()) {
            return false;
        }

        return true;
    }

    private boolean createReceiptData(Order order, List<OrderItem> orderItems) {
        String method = "";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        Bitmap logoData = BitmapFactory.decodeFile(User.getPath(requireContext()), options);
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (epsonPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            epsonPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addImage";
            epsonPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);

            method = "addFeedLine";
            epsonPrinter.addFeedLine(1);
            Business business = AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0);
            List<User> users = AppDataBase.getInstance(requireContext()).userDao().getAllUsers();
            String role = User.currentUser(requireContext()).getName();
            for(int i=0; i<users.size(); i++){
                if (users.get(i).getEmail().equals(User.currentUser(requireContext()).getEmail())){
                    role = User.currentUser(requireContext()).getEmail()+" - "+
                            requireContext().getResources().getStringArray(R.array.role_array)[Integer.parseInt(users.get(i).getRole_id())];
                    break;
                }
            }
            textData.append("").append(business.getName().toUpperCase()).append(" ").append(business.getPhone()).append("\n");
            textData.append(role.toUpperCase()).append("\n");
            textData.append("\n");
            textData.append(order.getCreated_at()).append("\n");
            textData.append(order.getCode()).append("\n");
            textData.append("------------------------------\n");
            method = "addText";
            epsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            float total = 0;
            for (int i = 0; i < orderItems.size(); i++) {
                total = (float) (total + (orderItems.get(i).getPrice() * orderItems.get(i).getQuantity()));
                textData.append(orderItems.get(i).getQuantity()).append(" ")
                        .append(AppDataBase.getInstance(requireContext()).menuItemDao().
                            getSpecificMenuItem(orderItems.get(i).getMenu_item_id()).getName())
                        .append(" ")
                        .append((orderItems.get(i).getQuantity() * orderItems.get(i).getPrice()))
                        .append(getString(R.string.currency_cfa))
                        .append("\n");

            }

            textData.append("------------------------------\n");
            method = "addText";
            epsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addText";
            epsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextSize";
            epsonPrinter.addTextSize(2, 2);
            method = "addText";
            epsonPrinter.addText("TOTAL "+total+" "+ getString(R.string.currency_cfa)+"\n");
            method = "addTextSize";
            epsonPrinter.addTextSize(1, 1);
            method = "addFeedLine";
            epsonPrinter.addFeedLine(1);

            textData.append("------------------------------\n");
            method = "addText";
            epsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append(getString(R.string.thank_u_for_visit)).append("!\n");
            method = "addText";
            epsonPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            epsonPrinter.addFeedLine(2);

            method = "addBarcode";
            epsonPrinter.addBarcode(order.getId()+"",
                    Printer.BARCODE_CODE39,
                    Printer.HRI_BELOW,
                    Printer.FONT_A,
                    barcodeWidth,
                    barcodeHeight);

            method = "addCut";
            epsonPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            epsonPrinter.clearCommandBuffer();
            ShowMsg.showException(e, method, requireContext());
            return false;
        }

        textData = null;

        return true;
    }

    private boolean printData() {
        if (epsonPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            epsonPrinter.clearCommandBuffer();
            return false;
        }

        try {
            epsonPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            epsonPrinter.clearCommandBuffer();
            ShowMsg.showException(e, "sendData", requireContext());
            try {
                epsonPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        Order order = viewModel.getOrderMutableLiveData().getValue();
        Objects.requireNonNull(order).setStatus(Constant.STATUS_CLOSED);
        long timeValue = (new Date().getTime());
        order.setUpdated_at(timeValue);
        for (int i=0; i<order.getCourses().size(); i++){
            order.getCourses().get(i).setUploaded_at(timeValue);
            order.getCourses().get(i).setUploaded_at(timeValue);
            for (int j=0; j<order.getCourses().get(i).getItems().size(); j++){
                order.getCourses().get(i).getOrderItems().get(j).setUpdated_at(timeValue);
                AppDataBase.getInstance(requireContext()).orderItemDao().update(order.getCourses().get(i).getOrderItems().get(j));
            }
            AppDataBase.getInstance(requireContext()).courseDao().update(order.getCourses().get(i));
        }
        AppDataBase.getInstance(requireContext()).orderDao().update(order);
        Toast.makeText(requireContext(), R.string.order_printed_success, Toast.LENGTH_LONG).show();

        return true;
    }

    private boolean initializeObject() {
        try {

            epsonPrinter = new Printer(Printer.TM_M30,
                    Printer.MODEL_ANK,
                    requireContext());
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", requireContext());
            return false;
        }

        epsonPrinter.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (epsonPrinter == null) {
            return;
        }

        epsonPrinter.setReceiveEventListener(null);

        epsonPrinter = null;
    }

    private boolean connectPrinter() {
        if (epsonPrinter == null) {
            return false;
        }

        try {
            epsonPrinter.connect(User.getPrinterModelTarget(requireContext()), Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", requireContext());
            return false;
        }

        return true;
    }

    private void disconnectPrinter() {
        if (epsonPrinter == null) {
            return;
        }

        while (true) {
            try {
                epsonPrinter.disconnect();
                break;
            } catch (final Exception e) {
                if (e instanceof Epos2Exception) {
                    //Note: If epsonPrinter is processing such as printing and so on, the disconnect API returns ERR_PROCESSING.
                    if (((Epos2Exception) e).getErrorStatus() == Epos2Exception.ERR_PROCESSING) {
                        try {
                            Thread.sleep(DISCONNECT_INTERVAL);
                        } catch (Exception ex) {
                        }
                    }else{
                        requireActivity().runOnUiThread(new Runnable() {
                            public synchronized void run() {
                                ShowMsg.showException(e, "disconnect", requireContext());
                            }
                        });
                        break;
                    }
                }else{
                    requireActivity().runOnUiThread(new Runnable() {
                        public synchronized void run() {
                            ShowMsg.showException(e, "disconnect", requireContext());
                        }
                    });
                    break;
                }
            }
        }

        epsonPrinter.clearCommandBuffer();
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onPtrReceive(Printer epsonPrinter, final int code, final PrinterStatusInfo status, final String printJobId) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), requireContext());

                displayPrinterWarnings(status);

                updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    private void updateButtonState(boolean state) {
        binding.btnAction.setEnabled(state);
    }

    private void displayPrinterWarnings(PrinterStatusInfo status) {
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }

        if (!warningsMsg.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.printer_warning);
            builder.setMessage(warningsMsg)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    }).show();
        }
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }
        if (status.getRemovalWaiting() == Printer.REMOVAL_WAIT_PAPER) {
            msg += getString(R.string.handlingmsg_err_wait_removal);
        }
        if(status.getUnrecoverError() == Printer.HIGH_VOLTAGE_ERR ||
                status.getUnrecoverError() == Printer.LOW_VOLTAGE_ERR) {
            msg += getString(R.string.handlingmsg_err_voltage);
        }

        return msg;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        finalizeObject();
        requireActivity().unregisterReceiver(IPosPrinterStatusListener);
        requireActivity().unbindService(connectService);
        handler.removeCallbacksAndMessages(null);
    }

    public static void bitmapRecycle(Bitmap bitmap)
    {
        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
        }
    }
}
