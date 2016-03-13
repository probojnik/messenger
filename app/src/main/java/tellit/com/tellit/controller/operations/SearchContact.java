package tellit.com.tellit.controller.operations;

import android.os.AsyncTask;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.service.XmppService;
import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by probojnik on 11.08.15.
 */
public class SearchContact extends AsyncTask {
    @Inject
    AbstractXMPPConnection connection;


    public SearchContact() {
        Injector.inject(this);
    }

    @Override
    protected Object doInBackground(Object[] params) {
       contactsIterate();

        return null;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Injector.inject(this);
    }



    private void contactsIterate() {
        boolean confirm = connection != null && connection.isConnected();
        if(confirm) {
            try {
                List<ContactData> query = HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("isValid", true).query();

                for (ContactData contactData : query) {
                    String jid = contactData.getJid();

                    boolean search = XmppService.searchContact(connection, TextUtil.substring(jid, "@"));
                    if(search) {
                        boolean installed = XmppService.setIsInstalled(jid);
                        if(installed){
                            EventBus.getDefault().post(contactData);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
//            EventBus.getDefault().post(new SyncContacts.SincOperationResult(true));

        }
    }
}
