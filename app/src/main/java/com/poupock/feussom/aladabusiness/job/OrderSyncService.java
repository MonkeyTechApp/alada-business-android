package com.poupock.feussom.aladabusiness.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.util.Methods;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.Fetch;
import com.poupock.feussom.aladabusiness.web.ServerUrl;

import java.lang.reflect.Method;

public class OrderSyncService extends JobService {

    public static final int JOB_ID = 0x001;
    private boolean jobCancelled = false;
    private String tag = OrderSyncService.class.getSimpleName();

    public OrderSyncService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(tag, "Job started");
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(JobParameters jobParameters) {
        Log.i(tag, "Doing the background job");

        new Fetch(getApplicationContext(), ServerUrl.ORDER + "?time="+ User.getLastSyncTime(getApplicationContext()),
                new VolleyRequestCallback() {
                    @Override
                    public void onStart() {
                        Log.i(tag, "Fetch started");
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.i(tag, "RESPONSE : "+response);
                        User.storeLastSync(Methods.getCurrentTimeStamp(), getApplicationContext());
                    }

                    @Override
                    public void onError(VolleyError error) {}

                    @Override
                    public void onJobFinished() {}
                }).execute();
        jobFinished(jobParameters, true);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(tag, "Job stopped before completion");
        jobCancelled = true;
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i(tag, "Rebind");
    }
}
