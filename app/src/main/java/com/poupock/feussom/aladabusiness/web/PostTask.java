package com.poupock.feussom.aladabusiness.web;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.net.SingleRequest;
import com.poupock.feussom.aladabusiness.util.User;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;



public class PostTask extends AsyncTask<Void, Void, Void> {

    String url;
    HashMap<String, String> params;
    JSONObject paramJsonObject;
    Context context;
    private String tag = PostTask.class.getSimpleName();
    VolleyRequestCallback callback;

    public PostTask(Context context, String url, HashMap<String, String> params, VolleyRequestCallback callback){
        this.context = context;
        this.url = url;
        this.params = params;
        paramJsonObject = new JSONObject(params);
        this.callback = callback;
    }

    public PostTask(Context context, String url,JSONObject params, VolleyRequestCallback callback){
        this.context = context;
        this.url = url;
        paramJsonObject = params;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        JsonObjectRequest connectObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                paramJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                Log.i(tag,"The response is "+response.toString());
                callback.onSuccess(response.toString());
                callback.onJobFinished();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(tag,"The volley error is "+error.toString());
                try {
                    Log.e(tag,new String(error.networkResponse.data, StandardCharsets.UTF_8));
                    FirebaseCrashlytics.getInstance().log(tag+ " - "+url+" \n "+
                            params.toString()+" \n "+
                            new String(error.networkResponse.data, StandardCharsets.UTF_8));
                }catch (NullPointerException exception){
                    Log.i(tag,"The Null error is "+exception.toString());
                    FirebaseCrashlytics.getInstance().recordException(exception);
                }
                callback.onError(error);
                callback.onJobFinished();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Accept-Type","application/json");
                headers.put("Authorization","Bearer "+ User.getToken(context));
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=" + getParamsEncoding();
            }
        };

        connectObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SingleRequest.getInstance(context).addToRequestQueue(connectObjectRequest);

        return null;
    }


}
