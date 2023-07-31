package com.poupock.feussom.aladabusiness.ui.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.callback.BLDeviceClickCallback;
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.List;

@SuppressLint("SetTextI18n")
public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BluetoothDeviceViewHolder> {

    private List<BluetoothDevice> devices;
    private Context context;
    BLDeviceClickCallback callback;
    DialogCallback dialogCallback;

    public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> devices, BLDeviceClickCallback callback, DialogCallback dialogCallback) {
        this.context = context;
        this.devices = devices;
        this.callback = callback;
        this.dialogCallback = dialogCallback;
    }

    public void setBluetoothDevices(List<BluetoothDevice> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public BluetoothDeviceAdapter.BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_bluetooth_dev, parent, false);
        return new BluetoothDeviceAdapter.BluetoothDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceViewHolder holder, int position) {

        BluetoothDevice device = this.devices.get(position);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        try{
            holder.txtName.setText(device.getName());
            holder.txtLetter.setText(device.getName().charAt(0)+"");
            holder.txtAddress.setText(device.getAddress());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onItemClickListener(device, false);
                    User.storeMobilePrinterAddress(device.getAddress(), context);
                    dialogCallback.onActionClicked(device, 1);
                }
            });
        }catch (NullPointerException ex){

        }catch (IllegalArgumentException ex){

        }

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtAddress;
        TextView txtLetter;

        public BluetoothDeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtLetter = itemView.findViewById(R.id.txtLetter);
            txtAddress = itemView.findViewById(R.id.txtAddress);
        }
    }
}
