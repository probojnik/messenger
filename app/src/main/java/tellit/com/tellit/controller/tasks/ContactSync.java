package tellit.com.tellit.controller.tasks;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.stanfy.enroscar.goro.Goro;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.SHA1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactComparable;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.users.AddContactsToRosterReq;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.user_creation.LoginPasswAPIFacade;
import tellit.com.tellit.model.user_creation.PhoneNormaliseResp;
import tellit.com.tellit.model.user_creation.PhonesNormaliseReq;
import tellit.com.tellit.tools.IOUtil;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by probojnik on 25.09.15.
 */
public class ContactSync implements Callable<List<ContactData>> {

    @Inject
    UserData userData;
    @Inject
    AbstractXMPPConnection connection;

    public ContactSync() {
        Injector.inject(this);
    }

    @Override
    public List<ContactData> call() throws Exception {
        Log.d("~~~~ContactSync", "start");

        Cursor contactsContract = userData.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI}, null, null, ContactsContract.CommonDataKinds.Phone.NUMBER + " ASC");

        final List<ContactData> addContactsList = new ArrayList();
        final List<ContactData> delContactsList = ContactData.getDao().queryForAll();


        if(contactsContract.moveToFirst()) {
            while (!contactsContract.isAfterLast()){
                int id = contactsContract.getInt(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                String name = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String photo = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                ContactData find_contact = ContactData.getDao().queryBuilder().where().eq("id",id).queryForFirst();
                if(find_contact == null){
                    ContactData add_Contact = new ContactData();
                    add_Contact.setId(id);
                    add_Contact.setName(name);
                    add_Contact.setNumber(number);
                    add_Contact.setPhoto_uri(photo);
                    addContactsList.add(add_Contact);

                }else{
                    if(number!=null && !number.equals(find_contact.getNumber())){
                        ContactData add_Contact = new ContactData();
                        add_Contact.setId(id);
                        add_Contact.setName(name);
                        add_Contact.setNumber(number);
                        add_Contact.setPhoto_uri(photo);
                        addContactsList.add(add_Contact);
                        delContactsList.add(find_contact);
                    }else {
                        delContactsList.remove(find_contact);
                        if ((name != null && (!name.equals(find_contact.getName()))) || (photo != null && !photo.equals(find_contact.getPhoto_uri()))) {
                            find_contact.setName(name);
                            find_contact.setPhoto_uri(photo);
                            ContactData.getDao().update(find_contact);
                        }
                    }
                }
                contactsContract.moveToNext();
            }
        }
        if(delContactsList.size() >0) {
            Log.d("~~~~ContactSync", "del - "+delContactsList.size());
            AddContactsToRosterReq removeContactsToRosterReq = new AddContactsToRosterReq(delContactsList, AddContactsToRosterReq.REMOVE);
            removeContactsToRosterReq.setType(IQ.Type.set);
            CustomStanzaController.getInstance().sendStanza(removeContactsToRosterReq, new CustomStanzaCallback() {
                @Override
                public void resultOK(Object result) {
                    try {
                        ContactData.getDao().delete(delContactsList);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(Exception ex) {
                    ex.printStackTrace();

                }
            });
        }
        if(addContactsList.size() > 0) {
            Log.d("~~~~ContactSync", "add - "+addContactsList.size());
            PhonesNormaliseReq req = new PhonesNormaliseReq(userData.getLOGIN_CODE() == 0 ? "us" : "ua");
            req.setPhones(addContactsList);
            PhoneNormaliseResp phoneNormaliseResp = LoginPasswAPIFacade.getInstance().getService().getNormalizPhones(req);

            final List<ContactData> normalisedContacts = new ArrayList<>();
            for (PhoneNormaliseResp.Phone phone : phoneNormaliseResp.getPhones()) {
                ContactData  contactData = new ContactData();
                contactData.setId(phone.getId());
                contactData.setUuid(phone.getUuid());
                contactData.setJid(phone.getUuid()+"@"+connection.getServiceName());
                int ind = addContactsList.indexOf(contactData);
                if(ind>=0){
                    addContactsList.get(ind).setUuid(contactData.getUuid());
                    addContactsList.get(ind).setJid(contactData.getJid());
                }
                normalisedContacts.add(contactData);
            }
            Log.d("~~~~ContactSync", "norm - "+normalisedContacts.size());
            for (ContactData contactData : addContactsList) {

                if (normalisedContacts.contains(contactData) && !contactData.getJid().equals(userData.getMyJid())) {
                    contactData.setIsValid(true);
                } else {
                    contactData.setIsValid(false);
                }
                try {
                    ContactData.getDao().create(contactData);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return normalisedContacts;

        }

        Log.d("~~~~ContactSync", "start");

        return null;
    }


}
