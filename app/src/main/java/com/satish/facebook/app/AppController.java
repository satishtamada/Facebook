package com.satish.facebook.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.satish.facebook.helper.ImageBitmapCache;

/**
 * Created by satish on 6/8/15.
 */
public class AppController extends Application {
    private static AppController appControllerInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public static final String TAG = AppController.class.getSimpleName();

    public void onCreate() {
        super.onCreate();
        appControllerInstance = this;
    }

    public static synchronized AppController getInstance() {
        return appControllerInstance;
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

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new ImageBitmapCache());
        }
        return this.mImageLoader;
    }
}
