package tellit.com.tellit.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by probojnik on 01.08.15.
 */
public class TellitAuthenticatorService extends Service {

    private TellitAuthenticator mAuthenticator;


    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new TellitAuthenticator(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
