package com.poupock.feussom.aladabusiness.net;

import android.content.Context;

import com.android.volley.Request;

import java.util.ArrayList;

/**
 * Created by Silver Ronald Jr on 12/9/2016.
 */
public class MyCommand<T> {
    Context _context;
    ArrayList<Request<T>> requests = new ArrayList<>();
    public MyCommand (Context context){
        _context = context;
    }

    public void addRequest(Request<T> request){
        requests.add(request);
    }

    public void removeRequest(Request<T> request){
        requests.remove(request);
    }

    public void execute(){
        for(Request<T> request : requests){
            SingleRequest.getInstance(_context).addToRequestQueue(request);
        }
    }
}
