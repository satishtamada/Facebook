package com.satish.facebook.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.satish.facebook.R;

import org.json.JSONArray;

/**
 * Created by satish on 28/9/15.
 */
public class AccountUtil {
    private static String TAG = AccountUtil.class.getSimpleName();

    public static boolean hasAccount(Context context) {
        if (getAccount(context) == null) {
            return false;
        }
        return true;
    }

    public static Account getAccount(Context context) {
        Account retAccount[] = getSpecificAccounts(context, context.getString(R.string.account_type));
        if (retAccount != null) {
            return retAccount[0];
        }
        return null;
    }

    public static Account[] getGoogleAccounts(Context context) {
        return getSpecificAccounts(context, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
    }

    public static JSONArray getGoogleAccountsJsonArray(Context context){
        Account[] accounts = getGoogleAccounts(context);
        JSONArray accountsArray = new JSONArray();
        if(accounts != null){
            for(Account account: accounts){
                accountsArray.put(account.name);
            }
        }
        return accountsArray;
    }

    public static String[] getGoogleEmails(Context context) {
        Account[] accounts = getGoogleAccounts(context);
        if (accounts != null && accounts.length > 0) {
            String[] emails = new String[accounts.length];
            for (int i = 0; i < accounts.length; i++) {
                emails[i] = accounts[i].name;
            }
            return emails;
        }
        return null;
    }

    public static Account[] getSpecificAccounts(Context context, String accountType) {
        Account accounts[] = AccountManager.get(context)
                .getAccountsByType(accountType);
        if (accounts.length != 0) {
            return accounts;
        } else {
            return null;
        }
    }

    public static void createAccount(Context context, String token) {
        Log.e(TAG, "createAccount: " + token);
        Account account = new Account(context.getString(R.string.app_name), context.getString(R.string.account_type));

        AccountManager accountManager = AccountManager.get(context);
        accountManager.addAccountExplicitly(account, "", null);
        accountManager.setAuthToken(account, context.getString(R.string.auth_token_type), token);

        ContentResolver.setSyncAutomatically(account, context.getString(R.string.authority), true);

        new FetchAllInstalledAppTask(context).execute();

    }

    static class FetchAllInstalledAppTask extends AsyncTask<Void, Void, Void> {
        Context context;

        public FetchAllInstalledAppTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Creating app packages job
            // FIXME
            //
            //SyncUtils syncUtils = new SyncUtils(context);
            //syncUtils.createAppPackagesJob();
            return null;
        }
    }

    public static void deleteAccount(Context context) {
        Account account = getAccount(context);
        if(account != null){
            AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            accountManager.removeAccount(account, null, null);
        }
    }

    public static class NoGoogleAccountException extends Exception {
        public NoGoogleAccountException(String message) {
            super(message);
        }
    }

    public static boolean verifyGoogleAccounts(Context context) {
        Account[] accounts = getGoogleAccounts(context);
        String[] emails = getGoogleEmails(context);

        return accounts != null && accounts.length > 0 && emails != null && emails.length > 0 ? true : false;
    }
}
