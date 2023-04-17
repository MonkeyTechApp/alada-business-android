package com.poupock.feussom.aladabusiness.web;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.net.SingleRequest;
import com.poupock.feussom.aladabusiness.util.User;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;



public class Fetch extends AsyncTask<Void, Void, Void> {

    VolleyRequestCallback callback;
    Context context;
    String Url;
    JSONObject param;
    private final String tag = Fetch.class.getSimpleName();

    public Fetch(Context context, String Url, VolleyRequestCallback callback){
        this.callback = callback;
        this.context = context;
        this.Url = Url;
        param = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Log.i(tag, "The url is : "+Url);
        StringRequest request = new StringRequest(Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.i(tag,"The response is "+response.toString());
                        callback.onSuccess(response.toString());
                        callback.onJobFinished();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                callback.onError(error);
                callback.onJobFinished();
                Log.i(tag,"The volley error is "+error.toString());
                try {
                    Log.e(tag,new String(error.networkResponse.data, StandardCharsets.UTF_8));
                    FirebaseCrashlytics.getInstance().log(tag+ " - "+Url+" \n \n "+
                            new String(error.networkResponse.data, StandardCharsets.UTF_8));
                }catch (NullPointerException exception){
                    Log.i(tag,"The Null error is "+exception.toString());
                    FirebaseCrashlytics.getInstance().recordException(exception);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","Bearer "+ User.getToken(context));
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=" + getParamsEncoding();
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(2500 , 2 , 2));
        SingleRequest.getInstance(context).addToRequestQueue(request);
        return null;
    }
}
