package com.poupock.feussom.aladabusiness.callback;

import com.android.volley.VolleyError;

public interface VolleyRequestCallback {

    void onStart();

    void onSuccess(String response);

    void onError(VolleyError error);

    void onJobFinished();

}
