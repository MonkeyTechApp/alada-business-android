package com.poupock.feussom.aladabusiness.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.util.User;
import com.poupock.feussom.aladabusiness.web.Fetch;
import com.poupock.feussom.aladabusiness.web.ServerUrl;

import java.util.List;

public class OrderSyncService extends JobService {

    public static final int JOB_ID = 0x001;
    public static final String TRANSACTION_KEY = "transaction";
    private static final String TAG= OrderSyncService.class.getSimpleName();
    private boolean jobCancelled = false;
    private String tag = OrderSyncService.class.getSimpleName();

    public OrderSyncService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "Job started");
        Gson gson = new Gson();
//        transactions = gson.fromJson(jobParameters.getExtras().getString(TRANSACTION_KEY),
//                new TypeToken<List<Transaction>>(){}.getType());
//        Log.i(TAG, "The transaction : "+jobParameters.getExtras().getString(TRANSACTION_KEY));
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(JobParameters jobParameters) {
        Log.i(TAG, "Doing the background job");

        new Fetch(getApplicationContext(), ServerUrl.ORDER_SYNC + "?time="+ User.getLastSyncTime(getApplicationContext()),
                new VolleyRequestCallback() {
                    @Override
                    public void onStart() {
                        Log.i(tag, "Fetch started");
                    }

                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onError(VolleyError error) {

                    }

                    @Override
                    public void onJobFinished() {

                    }
                }).execute();
        jobFinished(jobParameters, false);

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Job stopped before completion");
        jobCancelled = true;
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i(TAG, "Rebind");
    }
}
