package com.poupock.feussom.aladabusiness.callback;

import android.bluetooth.BluetoothDevice;

public interface BLDeviceClickCallback {
    void onItemClickListener(BluetoothDevice o , boolean isLong);
}
