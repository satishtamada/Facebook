package com.satish.facebook.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.satish.facebook.activity.FriendsHandlerActivity;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.helper.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Ravi on 01/06/15.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    private final String TAG = CustomPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    private Intent parseIntent;

    public CustomPushReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push received: " + json);

            parseIntent = intent;

            parsePushJson(context, json);

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    /**
     * Parses the push notification json
     *
     * @param context
     * @param json
     */
    private void parsePushJson(Context context, JSONObject json) {
        Log.e(TAG, "parsePushJson: " + json);
        try {
            boolean isBackground = json.getBoolean("is_background");
            int flag = json.getInt("flag");

            switch (flag){
                case AppConfig.FLAG_NEW_FRIEND_REQUEST:
                    processFriendRequest(context, json, isBackground);
                    break;
                default:
                    break;
            }



        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    private void processFriendRequest(Context context, JSONObject json, boolean isBackground) {

        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");

            if (!isBackground) {
                Intent resultIntent = new Intent(context, FriendsHandlerActivity.class);
                resultIntent.putExtra("tab_name", 3);
                showNotificationMessage(context, title, message, resultIntent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Shows the notification message in the notification bar
     * If the app is in background, launches the app
     *
     * @param context
     * @param title
     * @param message
     * @param intent
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        notificationUtils = new NotificationUtils(context);

        intent.putExtras(parseIntent.getExtras());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent);
    }
}