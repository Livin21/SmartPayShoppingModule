package com.smartpay.android.shopping;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SmartPay {

    private RequestQueue mRequestQueue;

    private Context context;

    private SmartPay(Context context){
        this.context = context;
        smartShopper = this;
        mRequestQueue = getRequestQueue();
    }


    @SuppressLint("StaticFieldLeak")
    private static SmartPay smartShopper;

    public static synchronized SmartPay getInstance(Context context){

        if (smartShopper == null)
            smartShopper = new SmartPay(context);

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

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
