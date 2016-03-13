package tellit.com.tellit.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;


import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.controller.ChatController;
import tellit.com.tellit.controller.operations.SincOperation;
import tellit.com.tellit.controller.operations.SyncReview;
import tellit.com.tellit.controller.tasks.SyncTasks;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.chat.ChatMessage;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.service.gcm.QuickstartPreferences;
import tellit.com.tellit.service.gcm.RegistrationIntentService;
import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.log.TraceHelper;

//import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;

/**
 * Created by root on 08.06.15.
 */
public class XmppService extends Service {
    public static final String COMMAND = "command";
    public static final String ALWAYS_CONNECT = "always_connect";
    //    @Inject
//    Provider<AbstractXMPPConnection> connectionProvider;
    ChatManager chatmanager;
    @Inject
    AbstractXMPPConnection connection;
    Roster roster;
    public static final String CONNECT = "connect";
    public static final String STOP = "stop";
    public static final String SYNC = "sync";
    public static final String DELETE_ALL = "delete_all";
    public static final String APP_ACTIVE = "app_active";
    public static final String APP_INACTIVE = "app_inactive";
    public static boolean is_connect = false;
    public static boolean is_sync = false;
    //    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    UserData userData;
    //    @Inject
//    Application myApplication;
    String login, password;
    boolean isActiveApp = false;
    Thread xmpp_thread;
    @Inject
    VCardModule vCardModule;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public XmppService() {
        Injector.inject(this);
        EventBus.getDefault().register(this);
//        myApplication =  getBaseContext();
        sharedPreferences = userData.getContext().getSharedPreferences("imclient", Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        login = userData.getMyLogin();
        password = userData.getUserPassword();
        if (intent != null) {
            String command = intent.getStringExtra(COMMAND);
            if (STOP.equals(command)) {
                try {
                    is_connect = false;
                    if (connection != null && isConnected()) {
                        connection.disconnect();
                    }
                    Log.e("!!!stop", "!!!!!!!!!!!!!!!!!!!!`!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopSelf();
                return START_NOT_STICKY;
            }
            if (CONNECT.equals(command)) {
                is_connect = true;
                connect();
                Log.e("!!!connect", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            if (SYNC.equals(command)) {
                is_sync = true;

                connect();
                Log.e("!!!sync", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            if (DELETE_ALL.equals(command)) {
                Log.e("!!!delete all", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                for (RosterEntry entry : roster.getEntries()) {
                    try {
                        roster.removeEntry(entry);
                    } catch (SmackException.NotLoggedInException e) {
                        e.printStackTrace();
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    try {
                        List<ContactData> list = HelperFactory.getInstans().getDao(ContactData.class).queryForEq("jid", entry.getUser());
                        HelperFactory.getInstans().getDao(ContactData.class).delete(list.get(0));
                    } catch (Exception ex) {
                    }
                }
            }
            if (APP_ACTIVE.equals(command)) {
                isActiveApp = true;
            }
            if (APP_INACTIVE.equals(command)) {
                isActiveApp = false;
            }
        }


        return Service.START_STICKY;
    }

    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }
    
    private void connect() {

        if (xmpp_thread != null ) {
            if (is_sync && connection.isAuthenticated()) {
//                SincOperation.run();
//                SyncTasks.getInstance().getMyVCard();
                SyncTasks.getInstance().syncAll();

            }
            return;
        }

        xmpp_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = connection.connect();

                } catch (Exception e) {
                    //e.printStackTrace();
                    return;
                }
                boolean send_to_serv = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (!send_to_serv) {
                    if (checkPlayServices()) {

                        Intent intent = new Intent(getBaseContext(), RegistrationIntentService.class);
                        startService(intent);
                    }
                }

                roster = Roster.getInstanceFor(connection);

                chatmanager = ChatManager.getInstanceFor(connection);
                listeners();
            }
        }, "XMPPPThread");
        xmpp_thread.start();
    }

    public static boolean searchContact(final XMPPConnection connection, String userName){
        boolean result = false;
        try{
            UserSearchManager search = new UserSearchManager(connection);
            Form searchForm = search.getSearchForm("search." + connection.getServiceName());

            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);

            ReportedData data = search.getSearchResults(answerForm, "search." + connection.getServiceName());
//            TraceHelper.print(userName, data.getRows());
            result = !data.getRows().isEmpty();
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Получает джиды от калбэка - changeState
     * Если джид есть в таблице контактов и isInstalls равна ложь,
     * тогда обновляем поле isInstalls в данном контакте
     */
    public static boolean setIsInstalled(String jid) {
        boolean result = false;
        try {
            List<ContactData> contacts = HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().limit((long) 1).where().eq("jid", jid).and().eq("isInstalls", false).query();
            boolean checked = contacts.size() > 0;

            if (checked) {
                UpdateBuilder updateBuilder = HelperFactory.getInstans().getDao(ContactData.class).updateBuilder();
                updateBuilder.updateColumnValue("isInstalls", true).where().isNotNull("name").and().eq("jid", jid);
                result = updateBuilder.update() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * Отвечает за одобрение (с типом станзы - subscribed) всех входящих запросов (с помощью типа станзы - subscribe)
     */
    private static void subscribedAllListener(final XMPPConnection connection) {
        ((XMPPConnection) connection).addSyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                String address = TextUtil.substring(packet.getFrom(), "@");
                if (!TextUtil.isEmpty(address)) {
                    Presence subscribed = new Presence(Presence.Type.subscribed);
                    subscribed.setTo(address);
                    connection.sendStanza(subscribed);
                }
            }
        }, new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                if (stanza instanceof Presence &&
                        ((Presence) stanza).getType().equals(Presence.Type.subscribe)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Дополнительный слушатель к {@link RosterListener#presenceChanged}
     */
    private void presenceChangedStanzaListener() {
        ((XMPPConnection) connection).addSyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                boolean update = false;
                String jid = TextUtil.substring(packet.getFrom(), "/");
                if (!TextUtil.isEmpty(jid)) {
                    update = setIsInstalled(jid);
                }
                TraceHelper.print(update ? "IsInstalls" : "Skip", jid, packet);
            }
        }, new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                if (stanza instanceof Presence) {
                    return true;
                }
                return false;
            }
        });
    }

    private void listeners() {
//        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
//        subscribedAllListener(connection);
//        presenceChangedStanzaListener();
//        roster.addRosterLoadedListener(new RosterLoadedListener() {
//            /**
//             * Called when the Roster was loaded successfully.
//             */
//            @Override
//            public void onRosterLoaded(final Roster roster) {
//                TraceHelper.print(roster);
//                if (is_sync)
//                    SincOperation.run();
//            }
//        });

        roster.addRosterLoadedListener(new RosterLoadedListener() {
            /**
             * Called when the Roster was loaded successfully.
             */
            @Override
            public void onRosterLoaded(final Roster roster) {
                if (is_sync) {
//                    SyncTasks.getInstance().getMyVCard();
                    SyncTasks.getInstance().syncAll();
                }
            }
        });

//        roster.addRosterListener(new RosterListener() {
//            /**
//             * Called when roster entries are added.
//             */
//            @Override
//            public void entriesAdded(final Collection<String> addresses) {
////                TraceHelper.print(addresses);
////                entriesAddUpd(addresses);
//
////                for (String address: addresses){
////                    /**
////                     * если запись из моей телефонной книги, тогда подтверждаем(принимаем) подписку
////                     */
////                    try {
////                        long count = HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().limit((long) 1).where().eq("jid", address).countOf();
////                        TraceHelper.print(count, address, TextUtil.substring(address,"@"));
////                        if(count > 0){
////                            Presence subscribed = new Presence(Presence.Type.subscribed);
////                            subscribed.setTo(address);
////                            connection.sendPacket(subscribed);
////                            upLastActivity(connection, address);
////                        }
////                    } catch (SmackException.NotConnectedException e) {
////                        e.printStackTrace();
////                    }catch (SQLException e) {
////                        e.printStackTrace();
////                    }
////                }
//            }
//
//            /**
//             * Called when a roster entries are updated.
//             */
//            @Override
//            public void entriesUpdated(Collection<String> addresses) {
////                TraceHelper.print(addresses);
////                entriesAddUpd(addresses);
//
////                for (String adr : addresses) {
////                    RosterEntry rosterEntry = roster.getEntry(adr);
////                    ContactData contact = new ContactData();
////                    contact.setName(rosterEntry.getName());
////                    contact.setJid(adr);
////                    contact.setIsValid(true);
////                    ContactData.updateContact(contact);
////                }
//            }
//
//            @Override
//            public void entriesDeleted(Collection<String> addresses) {
////                TraceHelper.print(addresses);
//            }
//
//            /**
//             * Пользователь на которого я подписан поменял состояние
//             */
//            @Override
//            public void presenceChanged(Presence presence) {
////                boolean update = false;
////                String jid = TextUtil.substring(presence.getFrom(), "/");
////                if (!TextUtil.isEmpty(jid)) {
////                    update = setIsInstalled(jid);
////                }
////                TraceHelper.print(update ? "IsInstalls" : "Skip", jid, presence);
//            }
//        });

        for (ChatManagerListener chatMessageListener : chatmanager.getChatListeners()) {
            chatmanager.removeChatListener(chatMessageListener);
        }

        chatmanager.addChatListener(
            new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean createdLocally) {
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, Message message) {
                            ChatController.getInstance().reciveMessage(new ChatMessage(chat, message));
                        }
                    });
                }
            });
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode,UserData.getInstance().getContext(),
//                        900).show();
            } else {
                Log.i("WARNING!!!!", "This device is not supported.");
//                finish();
            }
            return false;
        }
        return true;
    }


    public void onEvent(SyncReview.SincOperationResult result) {
        is_sync = false;
        if (!is_connect)
            stopSelf();
        Log.e("SyncFinish", "result.isResult():" + result.isResult());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connection != null)
            connection.disconnect();
        EventBus.getDefault().unregister(this);


    }
}