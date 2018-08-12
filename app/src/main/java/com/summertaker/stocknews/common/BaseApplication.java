package com.summertaker.stocknews.common;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.summertaker.stocknews.data.Item;

import java.util.ArrayList;

public class BaseApplication extends Application {

    public static final String TAG = BaseApplication.class.getSimpleName();

    private static BaseApplication mInstance;

    private RequestQueue mRequestQueue;

    private ArrayList<Item> mItemList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ArrayList<Item> getItemList() {
        return mItemList;
    }
}
