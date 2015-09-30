package com.satish.facebook.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Lincoln on 20/09/15.
 */
public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new AccountAuthenticator(this).getIBinder();
    }
}
