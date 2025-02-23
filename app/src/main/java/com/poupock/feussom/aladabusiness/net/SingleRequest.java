package com.poupock.feussom.aladabusiness.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Silver Ronald Jr on 6/5/2016.
 */

public class SingleRequest {
  private static SingleRequest instance;
  private RequestQueue requestQueue;
  private ImageLoader imageLoader;
  private static Context context;

  private SingleRequest(Context context) {
    this.context = context;
    requestQueue = getRequestQueue();

    imageLoader = new ImageLoader(requestQueue,
            new ImageLoader.ImageCache() {
              private final LruCache<String, Bitmap>
                      cache = new LruCache<String, Bitmap>(20);

              @Override
              public Bitmap getBitmap(String url) {
                return cache.get(url);
              }

              @Override
              public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
              }
            });
  }

  public static synchronized SingleRequest getInstance(Context context) {
    if (instance == null) {
      instance = new SingleRequest(context);
    }
    return instance;
  }

  public RequestQueue getRequestQueue() {
    if (requestQueue == null) {
      // getApplicationContext() is key, it keeps you from leaking the
      // Activity or BroadcastReceiver if someone passes one in.
      requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
    return requestQueue;
  }

  public <T> void addToRequestQueue(Request<T> req) {
    getRequestQueue().add(req);
  }

  public ImageLoader getImageLoader() {
    return imageLoader;
  }
}


