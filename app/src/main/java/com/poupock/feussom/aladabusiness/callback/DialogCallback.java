package com.poupock.feussom.aladabusiness.callback;

public interface DialogCallback {
    int LONG_CLICK = 2;
    int DONE = 3;

    void onActionClicked(Object o, int action);
}
