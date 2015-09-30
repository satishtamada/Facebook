package com.satish.facebook.app;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.satish.facebook.R;
import com.satish.facebook.activity.LoginActivity;
import com.satish.facebook.helper.ImageBitmapCache;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.helper.SessionManager;
import com.satish.facebook.utils.AccountUtil;
import com.satish.facebook.utils.ParseUtils;

/**
 * Created by satish on 6/8/15.
 */
public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();
    private static AppController appControllerInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static synchronized AppController getInstance() {
        return appControllerInstance;
    }

    public void onCreate() {
        super.onCreate();
        appControllerInstance = this;
        // register with parse
        ParseUtils.registerParse(this);
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

    public void verifySession() {
    // check login status
        boolean isLoggedIn = AccountUtil.hasAccount(getApplicationContext());

        if (!isLoggedIn) {

            clearUserData();

            Toast.makeText(getApplicationContext(), getString(R.string.logout_message), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(AppController.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void clearUserData() {
// cancel all volley requests
        getRequestQueue().cancelAll(getApplicationContext());

// Clearing all the shared preferences
// TODO - clear preferences
        SessionManager session = new SessionManager(getApplicationContext());
        session.resetPreferences();

// Clear all the database tables
// TODO - clear database
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        db.deleteUsers();
    }
}
