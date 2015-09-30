package com.satish.facebook.helper;

import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.satish.facebook.R;
import com.satish.facebook.app.AppController;
import com.satish.facebook.utils.AccountUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by satish on 28/9/15.
 */
public class  VolleyNetwork {

    private static final String TAG = VolleyNetwork.class.getSimpleName();

    public static Map<String, String> getHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getAuthToken());

        return headers;
    }


    private static String getAuthToken() {
        Log.e(TAG, "getAuthToken()");
        Context mContext = AppController.getInstance().getApplicationContext();
        AccountManager accountManager = AccountManager.get(mContext);

        AccountManagerFuture<Bundle> future = accountManager
                .getAuthToken(AccountUtil.getAccount(mContext), mContext.getString(R.string.auth_token_type), null, false, null, null);
        String token = "";
        Bundle tokenBundle = null;

        try {
            tokenBundle = future.getResult();
        } catch (AuthenticatorException | IOException | OperationCanceledException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        Log.e(TAG, "getAuthToken() tokenBundle: " + tokenBundle);

        if (tokenBundle == null) {
            return null;
        }
        token = tokenBundle.getString(AccountManager.KEY_AUTHTOKEN);

// the format of api key should be ApiKey <Username>:<ApiToken>
        Log.e(TAG, "Authorization token=" + token);

        return token;
    }
}
