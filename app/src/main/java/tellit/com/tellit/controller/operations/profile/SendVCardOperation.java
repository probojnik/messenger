package tellit.com.tellit.controller.operations.profile;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.controller.MultiThreadController;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;

/**
 * Created by probojnik on 22.07.15.
 *
 * <message to="+380635716703@ip-172-31-7-92" from="ip-172-31-7-92">
 *     <subject>vcard update</subject><vcard xmlns="custom:iq::notification"><user>+380637699857</user></vcard>
 *     <delay xmlns="urn:xmpp:delay" from="ip-172-31-7-92" stamp="2015-08-12T13:50:55.258Z"/>
 * <x xmlns="jabber:x:delay" from="ip-172-31-7-92" stamp="20150812T13:50:55"/></message>
 *
 */
public class SendVCardOperation {
    @Inject
    UserData userData;
    @Inject
    AbstractXMPPConnection connection;
    @Inject
    MyApplication myApplication;
    @Inject
    VCardModule vCardModule;

    public static void run(){
         new SendVCardOperation();
    }

    public SendVCardOperation() {
        Injector.inject(this);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    ContactData myContact = (ContactData) HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("jid", userData.getMyJid()).queryForFirst();
                    if (myContact != null) {
                        myContact.setName(userData.getUserFirstName() + " " + userData.getUserLastName());
                        HelperFactory.getInstans().getDao(ContactData.class).update(myContact);

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                VCard vCard = new VCard();
                vCard.setFirstName(userData.getUserFirstName());
                vCard.setLastName(userData.getUserLastName());
                vCard.setNickName(userData.getUserFirstName() +" "+userData.getUserLastName());

                try {
                    ContactData my_contactData = ContactData.getDao().queryBuilder().where().eq("jid",userData.getMyJid()).queryForFirst();
                    if(my_contactData!=null){
                        my_contactData.setName(userData.getUserFirstName()+" "+userData.getUserLastName());
                        ContactData.getDao().update(my_contactData);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(userData.getAva() != null && userData.getAva().length()>0) {
                    InputStream inputStream = null;
                    try {


//                        if (userData.getAva().startsWith("content://com.google.android.apps.photos.content")) {
                            inputStream = myApplication.getContentResolver().openInputStream(Uri.parse(userData.getAva()));
//                        } else {
//                            File f = new File(Uri.parse(userData.getAva()).getPath());
//                            inputStream = new FileInputStream(f);
//                        }
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        inputStream.close();
                        vCard.setAvatar(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }




                    vCardModule.sendMyVCard(vCard, new VCardModule.SaveResult() {
                        @Override
                        public void success() {
                            EventBus.getDefault().post(new SendVCardOperationResult(true));
                        }

                        @Override
                        public void error(Exception ex) {
                            ex.printStackTrace();
                            EventBus.getDefault().post(new SendVCardOperationResult(false));
                        }
                    });


                return new Object();
            }

//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                if (o!=null)
//                    EventBus.getDefault().post(new SendVCardOperationResult(true));
//                else
//                    EventBus.getDefault().post(new SendVCardOperationResult(false));
//            }
        };

        MultiThreadController.getInstance().execute(asyncTask);
    }


    public static final class SendVCardOperationResult {
        boolean result;
        public SendVCardOperationResult(boolean r) {
            this.result = r;
        }

        public boolean isResult() {
            return result;
        }
    }
}
