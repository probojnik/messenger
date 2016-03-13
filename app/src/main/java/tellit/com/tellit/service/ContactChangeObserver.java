package tellit.com.tellit.service;

import android.content.ContentResolver;
import android.content.SyncInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import tellit.com.tellit.MyApplication;
import tellit.com.tellit.account.TellitAccount;
import tellit.com.tellit.account.TellitAuthenticator;

/**
 * Created by probojnik on 01.08.15.
 */
class ContactChangeObserver extends ContentObserver {
    public ContactChangeObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {

        Log.e("change   ", "contact!!!!!!!!!");
        Bundle settingsBundle = new Bundle();

        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(MyApplication.sAccount, TellitAccount.TYPE, settingsBundle );

    }
}
