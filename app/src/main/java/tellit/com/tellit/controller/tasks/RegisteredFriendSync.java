package tellit.com.tellit.controller.tasks;

import android.util.Log;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.users.RegisterFriendsReq;

/**
 * Created by probojnik on 28.09.15.
 */
public class RegisteredFriendSync implements Callable {
    @Override
    public Object call() throws Exception {
        Log.d("~~~~RegisteredFriendSync", "start");

        CustomStanzaController.getInstance().sendStanza(new RegisterFriendsReq(), new CustomStanzaCallback<RegisterFriendsReq>() {
            @Override
            public void resultOK(RegisterFriendsReq result) {
                for (String uuid : result.getUsers()) {
                    try {
                        ContactData contactData = ContactData.getDao().queryBuilder().where().eq("uuid", uuid).queryForFirst();
                        if (contactData != null) {
                            contactData.setIsInstalls(true);
                            ContactData.getDao().update(contactData);
                            EventBus.getDefault().post(contactData);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void error(Exception ex) {

            }
        });
        Log.d("~~~~RegisteredFriendSync", "finish");

        return null;
    }
}
