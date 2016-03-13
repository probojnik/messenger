package tellit.com.tellit.controller.operations;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.controller.SearchThreadController;
import tellit.com.tellit.controller.SingleThreadController;
import tellit.com.tellit.controller.operations.profile.SaveAva;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.users.RegisterFriendsReq;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;

/**
 * Created by probojnik on 22.07.15.
 */
public class SincOperation  {
    @Inject
    UserData userData;
    @Inject
    VCardModule vCardModule;


    public static void run() {
        Log.e("SincOperation", "SyncStart");
        new SincOperation();
    }

    public SincOperation() {
        Injector.inject(this);
        Log.d("SincOperation", "````StartSync");

            vCardModule.getContactByJid(userData.getMyJid(), new VCardModule.ContactDataCallback() {
                @Override
                public void result(ContactData contactData) {
                    userData.setUSER_FIRST_NAME(contactData.getFirsName());
                    userData.setUSER_LAST_NAME(contactData.getLastName());
                    userData.setAva(contactData.getPhoto_uri());
                    EventBus.getDefault().post(contactData);
                }
            });

//        SingleThreadController.getInstance().execute(new SyncContacts(new OperationCalback() {
//            @Override
//            public void onComplete() {
//                Log.d("SincOperation", "````ContactEnd");
//
//                SingleThreadController.getInstance().execute(new SyncReview(new OperationCalback() {
//                    @Override
//                    public void onComplete() {
//                        Log.d("SincOperation", "````ReviewEnd");
////                        SingleThreadController.getInstance().execute(new SearchContact());
//                        CustomStanzaController.getInstance().sendStanza(new RegisterFriendsReq(), new CustomStanzaCallback<RegisterFriendsReq>() {
//                            @Override
//                            public void resultOK(RegisterFriendsReq result) {
//                                for(String uuid : result.getUsers()){
//                                    try {
//                                        ContactData contactData = ContactData.getDao().queryBuilder().where().eq("uuid",uuid).queryForFirst();
//                                        if(contactData!= null){
//                                            contactData.setIsInstalls(true);
//                                            ContactData.getDao().update(contactData);
//                                            EventBus.getDefault().post(contactData);
//                                        }
//                                    } catch (SQLException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void error(Exception ex) {
//
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(Exception ex) {
//                        Log.d("SincOperation", "````ReviewEndError");
//                        EventBus.getDefault().post(new SyncReview.SincOperationResult(false));
//
//                    }
//                }));
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                EventBus.getDefault().post(new SyncContacts.SincOperationResult(false));
//                Log.d("SincOperation", "````ContactEndErr");
//
//            }
//        }));




    }


}