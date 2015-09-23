package com.satish.facebook.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.satish.facebook.app.AppConfig;

import org.apache.http.util.TextUtils;

/**
 * Created by satish on 23/9/15.
 */
public class ParseUtils {

    private static String TAG = ParseUtils.class.getSimpleName();

    public static void verifyParseConfiguration(Context context) {
        if (TextUtils.isEmpty(AppConfig.PARSE_APPLICATION_ID) || TextUtils.isEmpty(AppConfig.PARSE_CLIENT_KEY)) {
            Toast.makeText(context, "Please configure your Parse Application ID and Client Key in AppConfig.java", Toast.LENGTH_LONG).show();
            ((Activity) context).finish();
        }
    }

    public static void registerParse(Context context) {
        // initializing parse library
        Parse.initialize(context, AppConfig.PARSE_APPLICATION_ID, AppConfig.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                String deviceToken = (String) ParseInstallation.getCurrentInstallation().get("deviceToken");
                Log.d(TAG, "device token is:" + deviceToken);
            }
        });
    }

    public static void subscribeWithEmail(String email) {

        String channel = email.replace("@", "at").replace(".", "dot");

        ParsePush.subscribeInBackground(channel, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null && e.getMessage() != null)
                    Log.e(TAG, "Subscribe in background: " + e.getMessage());
                else
                    Log.e(TAG, "Successfully subscribed to channel!");
            }
        });

        /*

        final ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("channels", email);

        Log.e(TAG, "Email: " + email);

        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Subscribed with channel: error: " + e.getMessage());
            }
        });
        */
    }
}