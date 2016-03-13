package tellit.com.tellit.modules;

import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;


import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.service.XmppService;
import tellit.com.tellit.tools.SerialUtils;


/**
 * Created by probojnik on 20.07.15.
 */
@Module(library = true)
public class XmppModule {
    String login, password;
    @Inject
    MyApplication context;

    @Inject
    UserData userData;

    AbstractXMPPConnection connection;



    @Provides @Singleton
    AbstractXMPPConnection providerXmppConection() {
        Injector.inject(this);
//        context = userData.getContext();
        Log.d("XmppModule", "new Connection");
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setServiceName("tellit"); // "ip-172-31-7-92"
//        configBuilder.setHost("52.11.160.63"); // 52.11.20.55
        configBuilder.setHost("52.11.20.55"); //
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setCompressionEnabled(true);
        configBuilder.setDebuggerEnabled(true); // debug
        configBuilder.setConnectTimeout(30000);

        try {
            TLSUtils.acceptAllCertificates(configBuilder);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        TLSUtils.disableHostnameVerificationForTlsCertificicates(configBuilder);
        ConnectionConfiguration configuration = configBuilder.build();
        ReconnectionManager.setEnabledPerDefault(true);
        ReconnectionManager.setDefaultFixedDelay(5);


        connection = new XMPPTCPConnection(configBuilder.build());
        SASLAuthentication.registerSASLMechanism(new SASLPlainMechanism());
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
        SASLAuthentication.blacklistSASLMechanism("CRAM-MD5");
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        connection.setPacketReplyTimeout(30000);
        connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.e("!!!!!!", "connected");

                //createAccount(login);
//                sharedPreferences = UserData.getInstance().getSharedPreferences();
                login = userData.getMyLogin();//sharedPreferences.getString(UserData.USER_LOGIN, "");
                password =userData.getUserPassword();// sharedPreferences.getString(UserData.USER_PASSWORD, "");
                try {
                    if (!XmppModule.this.connection.isAuthenticated() && XmppModule.this.connection.getConnectionCounter() == 0)
                        XmppModule.this.connection.login(login, password, SerialUtils.getPhoneSerial(context));
                }catch (SASLErrorException saslErrorException){
                    if(saslErrorException.getMessage()!=null && saslErrorException.getMessage().contains("not-authorized")){
                        userData.setAutoriz(false);
                        try {
                            HelperFactory.getInstans().getDao(ContactData.class).deleteBuilder().delete();
                            HelperFactory.getInstans().getDao(MessageData.class).deleteBuilder().delete();
                            HelperFactory.getInstans().getDao(ChatData.class).deleteBuilder().delete();
                            HelperFactory.getInstans().getDao(ReviewData.class).deleteBuilder().delete();
                            HelperFactory.getInstans().getDao(LikeData.class).deleteBuilder().delete();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(saslErrorException);
                    }
                } catch (XMPPException e) {
                    e.printStackTrace();

                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                Log.e("!!!!!!", "authenticated");

                if(!userData.isAutoriz()) {
                    userData.setAutoriz(true);
                    AccountManager.get(context).setUserData(MyApplication.sAccount, "autoriz", "autoriz");
                    String myJid = userData.getMyJid();
                    if (myJid.length() == 0) {
                        String jid = connection.getUser();
                        int index = jid.indexOf("/");
                        if (index > 0) {
                            jid = jid.substring(0, index);
                        }
                        userData.setMyJid(jid);

                    }
                }
            }

            @Override
            public void connectionClosed() {
                Log.e("!!!!!!", "connectionClosed");
                EventBus.getDefault().post(connection);

            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.e("!!!!!!", "connectionClosedOnError");

            }

            @Override
            public void reconnectionSuccessful() {
                Log.e("!!!!!!", "reconnectionSuccessful");

            }

            @Override
            public void reconnectingIn(int seconds) {
                Log.e("!!!!!!", "reconnectingIn");

            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.e("!!!!!!", "reconnectionFailed");

            }
        });
        return connection;
    }
}
