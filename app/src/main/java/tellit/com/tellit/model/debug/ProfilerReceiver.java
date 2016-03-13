package tellit.com.tellit.model.debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.sql.SQLException;

import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.tools.log.DumpHelper;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by Stas on 15.09.2015.
 cd C:\Users\Stas\AppData\Local\Android\sdk\platform-tools
 adb shell am broadcast -a com.intent.action.CLEAN_CONTACTS
 adb shell am broadcast -a com.intent.action.CLEAN_CHATS
 */
public class ProfilerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean result = false;
        try {
            result = exec(intent.getAction());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TraceHelper.print(result, intent);
    }

    private boolean exec(String action) throws SQLException {
        if (action.equals("com.intent.action.CLEAN_CONTACTS")) {
            return HelperFactory.getInstans().getDao(ContactData.class).deleteBuilder().delete() > 0;
        } else if (action.equals("com.intent.action.CLEAN_CHATS")) {
            boolean chat = HelperFactory.getInstans().getDao(ChatData.class).deleteBuilder().delete() > 0;
            boolean message = HelperFactory.getInstans().getDao(MessageData.class).deleteBuilder().delete() > 0;
            return chat || message;
        }
        return false;
    }
}