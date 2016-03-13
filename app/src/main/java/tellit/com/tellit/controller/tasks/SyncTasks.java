package tellit.com.tellit.controller.tasks;

import android.util.Log;

import com.stanfy.enroscar.goro.FutureObserver;
import com.stanfy.enroscar.goro.Goro;
import com.stanfy.enroscar.goro.ObservableFuture;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;

/**
 * Created by probojnik on 28.09.15.
 */
public class SyncTasks {
    final String syncAll = "syncAll";
    Goro goro;
    Date s_date,e_date;
    FutureObserver contactSyncComplete = new FutureObserver<List<ContactData>>() {
        @Override
        public void onSuccess(List<ContactData> value) {
            final  String syncRoster = "syncRoster";
            goro.schedule(syncAll, new ReviewSync(value));
            goro.schedule(syncAll, new LikesSync(value));
            goro.schedule(syncRoster, new AddToRosterSync(value));
            goro.schedule(syncRoster,new RegisteredFriendSync());
            goro.schedule(syncRoster, new RatingSinc());


            e_date = new Date();

            long diff = s_date.getTime() - e_date.getTime();

            long diffSeconds = diff / 1000 % 60;
            Log.d("~~~SyncTasks", "sec - " + diffSeconds);
        }

        @Override
        public void onError(Throwable error) {

        }
    };

    private static SyncTasks ourInstance = new SyncTasks();
    @Inject
    UserData userData;
    @Inject
    VCardModule vCardModule;

    public static SyncTasks getInstance() {
        return ourInstance;
    }

    private SyncTasks() {
       Injector.inject(this);
        goro = Goro.create();
    }

    public void syncAll(){
        s_date = new Date();
        goro.schedule(syncAll, new ContactSync()).subscribe(contactSyncComplete);

    }

    public void getMyVCard(){
        vCardModule.getContactByJid(userData.getMyJid(), new VCardModule.ContactDataCallback() {
            @Override
            public void result(ContactData contactData) {
                userData.setUSER_FIRST_NAME(contactData.getFirsName());
                userData.setUSER_LAST_NAME(contactData.getLastName());
                userData.setAva(contactData.getPhoto_uri());
                EventBus.getDefault().post(userData);
            }
        });
    }

    public void syncAllReview() {

        try {
            List<ContactData> contactDataList = HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("isValid",true).query();
            goro.schedule(syncAll, new ReviewSync(contactDataList));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
