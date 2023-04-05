package com.poupock.feussom.aladabusiness.util;

public class PrinterDevice {
    String target;
    String usb;

    public PrinterDevice(String target, String usb) {
        this.target = target;
        this.usb = usb;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUsb() {
        return usb;
    }

    public void setUsb(String usb) {
        this.usb = usb;
    }
}
