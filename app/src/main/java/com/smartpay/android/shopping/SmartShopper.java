package com.smartpay.android.shopping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class SmartShopper{

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private Context context;

    private SmartShopper(Context context){
        this.context = context;
        smartShopper = this;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

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


    @SuppressLint("StaticFieldLeak")
    private static SmartShopper smartShopper;

    public static synchronized SmartShopper getInstance(Context context){

        if (smartShopper == null)
            smartShopper = new SmartShopper(context);

        return smartShopper;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    @SuppressWarnings("unused")
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    @SuppressWarnings("unused")
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
