package tellit.com.tellit.modules;

import android.os.Handler;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingUserByJidReq;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingUserByJidResp;
import tellit.com.tellit.model.custom_xmpp.requests.users.UnknownUser;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by probojnik on 14.08.15.
 */
@Module(library = true)
public class VCardModule {
    @Inject
    UserData userData;
    @Inject
    AbstractXMPPConnection connection;
    @Inject
    MyApplication myApplication;

    @Provides
    @Singleton
    VCardModule providerVCardModule() {
        Injector.inject(this);
        return this;
    }

    public void sendMyVCard(VCard vCard, final SaveResult saveResult){
        vCard.setTo(null);
        vCard.setType(IQ.Type.set);
        CustomStanzaController.getInstance().sendStanza(vCard, new CustomStanzaCallback() {
            @Override
            public void resultOK(Object result) {
                saveResult.success();
            }

            @Override
            public void error(Exception ex) {
                saveResult.error(ex);
            }
        });

    }

    public void getContactByJid(final String jid, final ContactDataCallback contactDataCallback){
        if(jid == null){
            contactDataCallback.result(new ContactData().setName("Unknown"));
            return;
        }
        List<ContactData> contactDatas = null;
        try {
            contactDatas = ContactData.getDao().queryForEq("jid", jid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        TraceHelper.print(jid, contactDatas);
        boolean isValidContactData = contactDatas!=null && contactDatas.size()>0 && contactDatas.get(0).getName()!=null;
        if(isValidContactData){
            final List<ContactData> finalContactDatas = contactDatas;
            new Handler(myApplication.getApplicationContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    contactDataCallback.result(finalContactDatas.get(0));
                }
            });
        }else {
            getVcard(jid,contactDataCallback);
        }


    }

    public void getVcard(final String jid, final ContactDataCallback contactDataCallback) {
        final VCard vcardRequest = new VCard();
        vcardRequest.setTo(jid);
        CustomStanzaController.getInstance().sendStanza(vcardRequest, new CustomStanzaCallback<VCard>() {
            @Override
            public void resultOK(VCard vCard) {
                try {
                    ContactData contactData = (ContactData) HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("jid", jid).queryForFirst();

                    if (contactData != null) {
                        if (contactData.getPhoto_uri() != null && contactData.getPhoto_uri().length() > 0 && !contactData.getPhoto_uri().startsWith("file")) {
                            final ContactData finalContactData1 = contactData;
                            new Handler(myApplication.getApplicationContext().getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    contactDataCallback.result(finalContactData1);

                                }
                            });
                            return;
                        }
                    } else {
                        try {
                            contactData = new ContactData();
                            contactData.setJid(jid);
                            ContactData.getDao().create(contactData);
                            updateRatingForContact(contactData);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }

                    final ContactData finalContactData = contactData;


                    if (vCard.getAvatar() != null) {
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(new File(myApplication.getApplicationContext().getFilesDir() + File.separator + contactData.get_id() + ".jpg"));
                            fos.write(vCard.getAvatar());
                            fos.close();
                            contactData.setPhoto_uri("file://" + myApplication.getApplicationContext().getFilesDir() + File.separator + contactData.get_id() + ".jpg");
                            Picasso.with(myApplication).invalidate(contactData.getPhoto_uri());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        contactData.setPhoto_uri("");
                    }
                    ContactData.getDao().update(contactData);


                 if(!contactData.isValid()){

                        if (vCard.getFirstName() != null && vCard.getLastName() != null) {
                            contactData.setFirsName(vCard.getFirstName());
                            contactData.setLastName(vCard.getLastName());
                            if (vCard.getNickName() != null && vCard.getNickName().length() > 0)
                                contactData.setName(vCard.getNickName());
                            else
                                contactData.setName(vCard.getFirstName() + " " + vCard.getLastName());

                            ContactData.getDao().update(contactData);
                            new Handler(myApplication.getApplicationContext().getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    contactDataCallback.result(finalContactData);

                                }
                            });

                        } else {
                            requestUnknownUser(jid, 1, contactDataCallback);
                        }
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void error(Exception ex) {
                requestUnknownUser(jid, 1, contactDataCallback);
            }
        });
    }

    public void getNameByJid(final String jid, final NameCallback nameCallback){
        getContactByJid(jid, new ContactDataCallback() {
            @Override
            public void result(final ContactData contactData) {
                new Handler(myApplication.getApplicationContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        nameCallback.result(contactData.getName());
                    }
                });
            }
        });

    }
    /**
     * @param jid
     * @param mode 0 - phone, 1 - mask
     * @param contactDataCallback
     */
    public void requestUnknownUser(String jid, final int mode,final ContactDataCallback contactDataCallback){
        try {
            ContactData contactData = (ContactData) HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("jid", jid).queryForFirst();
            if (contactData == null) {
                contactData = new ContactData();
                contactData.setJid(jid);
                ContactData.getDao().create(contactData);
                updateRatingForContact(contactData);
            }
            final ContactData finalContactData = contactData;

            int index = jid.indexOf("@");
            if (index > 0) {
                String userName = jid.substring(0, index);
                UnknownUser unknownUser = new UnknownUser();
                unknownUser.setUsername(userName);
                unknownUser.setMode(mode);
                CustomStanzaController.getInstance().sendStanza(unknownUser, new CustomStanzaCallback<UnknownUser>() {
                    @Override
                    public void resultOK(UnknownUser result) {
                        if(mode == 1)
                            finalContactData.setName(result.getPhoneMask());
                        else if(mode == 0)
                            finalContactData.setNumber(result.getPhoneMask());
                        try {
                            ContactData.getDao().update(finalContactData);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        new Handler(myApplication.getApplicationContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                contactDataCallback.result(finalContactData);

                            }
                        });
                    }

                    @Override
                    public void error(Exception ex) {
                        finalContactData.setName("Unknown");
                        new Handler(myApplication.getApplicationContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                contactDataCallback.result(finalContactData);

                            }
                        });
                    }
                });
            }
        }catch (SQLException ex){ex.printStackTrace();}
    }


    private void updateRatingForContact(final ContactData new_contact){
        RatingUserByJidReq ratingUserByJidReq = new RatingUserByJidReq();
        ratingUserByJidReq.setJid(new_contact.getJid());
        CustomStanzaController.getInstance().sendStanza(ratingUserByJidReq, new CustomStanzaCallback<RatingUserByJidResp>() {
            @Override
            public void resultOK(RatingUserByJidResp result) {
                new_contact.setRate(result.getRating());
                new_contact.setReviewNumber(result.getReviewNumber());

                try {
                    ContactData.getDao().update(new_contact);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(new_contact);
            }

            @Override
            public void error(Exception ex) {

            }
        });
    }



    public boolean isMyJid(String jid){
        String myJid = userData.getMyJid();
        if(myJid.equals(jid)) return true;
        return false;
    }

    public static abstract class ContactDataCallback{
        public abstract void result(ContactData contactData);

    }
    public static abstract class NameCallback{
        public abstract void result(String name);
    }
    public static abstract class SaveResult {
        public abstract void success();
        public abstract void error(Exception ex);
    }
}
