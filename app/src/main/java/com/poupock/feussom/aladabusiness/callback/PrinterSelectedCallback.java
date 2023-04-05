package com.poupock.feussom.aladabusiness.callback;

import com.epson.epos2.discovery.DeviceInfo;

public interface PrinterSelectedCallback {
    void onItemClickListener(DeviceInfo o , boolean isLong);
}
