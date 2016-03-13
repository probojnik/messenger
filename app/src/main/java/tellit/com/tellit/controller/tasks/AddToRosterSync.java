package tellit.com.tellit.controller.tasks;

import android.util.Log;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;
import java.util.concurrent.Callable;

import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.users.AddContactsToRosterReq;

/**
 * Created by probojnik on 29.09.15.
 */
public class AddToRosterSync implements Callable {
    private List<ContactData> normalisedContacts;

    public AddToRosterSync(List<ContactData> normalisedContacts) {
        this.normalisedContacts = normalisedContacts;
    }

    @Override
    public Object call() throws Exception {
        Log.d("~~~~AddToRosterSync", "start");

        if(normalisedContacts!= null && normalisedContacts.size() > 0) {
            AddContactsToRosterReq addContactsToRosterReq = new AddContactsToRosterReq(normalisedContacts, AddContactsToRosterReq.ADD);
            addContactsToRosterReq.setType(IQ.Type.set);

            CustomStanzaController.getInstance().sendStanzaSerial(addContactsToRosterReq);
        }
        Log.d("~~~~AddToRosterSync", "end");

        return null;
    }
}
