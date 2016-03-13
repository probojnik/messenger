package tellit.com.tellit.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;

import com.j256.ormlite.stmt.Where;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by probojnik on 01.08.15.
 */
public class ContactService extends Service {

    ContactChangeObserver observer;

    public ContactService() {
        super();
        Injector.inject(this);
    }

    @Override
    public void onCreate() {
        System.out.println("service created");
        observer=new ContactChangeObserver(new Handler());
        // TODO Auto-generated method stub
//        getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true, observer);
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, false, observer);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        getContentResolver().unregisterContentObserver(observer);
        super.onDestroy();
    }


}