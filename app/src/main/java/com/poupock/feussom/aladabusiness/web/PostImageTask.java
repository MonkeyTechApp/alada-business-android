package com.poupock.feussom.aladabusiness.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.poupock.feussom.aladabusiness.callback.VolleyRequestCallback;
import com.poupock.feussom.aladabusiness.net.SingleRequest;
import com.poupock.feussom.aladabusiness.net.VolleyFileMultipartRequest;
import com.poupock.feussom.aladabusiness.util.User;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class PostImageTask extends AsyncTask<Void, Void, Void> {

    String url;
    Bitmap  bitmap;
    Context context;
    HashMap<String, String > hashMap;
    private String tag = PostImageTask.class.getSimpleName();
    VolleyRequestCallback callback;

    public PostImageTask(Context context, String url, Bitmap bitmap, HashMap<String, String > params, boolean b, VolleyRequestCallback callback){
        this.context = context;
        this.url = url;
        this.hashMap = params;
        this.bitmap = bitmap;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        VolleyFileMultipartRequest multipartRequest = new VolleyFileMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.i(tag, new String(response.data));
                        callback.onSuccess(new String(response.data));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                        Log.e("GotError",""+error.toString());
                        try {
                            Log.i(tag, "The response error is "+
                                    new String(error.networkResponse.data, StandardCharsets.UTF_8));
                        }catch (NullPointerException ex){Log.i(tag,"The exception is : "+ex.toString());}
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept","application/json");
                headers.put("Content-Type","application/json");
                headers.put("Authorization","Bearer "+ User.getToken(context));
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return hashMap;
            }

            @Override
            protected Map<String, VolleyFileMultipartRequest.DataPart> getByteData() {
                Map<String, VolleyFileMultipartRequest.DataPart> params = new HashMap<>();
                long imageName = System.currentTimeMillis();
                params.put("image", new DataPart(imageName + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(5000,2,2));
        multipartRequest.setShouldCache(false);
        SingleRequest.getInstance(context).addToRequestQueue(multipartRequest);

        return null;
    }




}
