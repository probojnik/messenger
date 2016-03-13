package tellit.com.tellit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;


import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.SHA1;

import tellit.com.tellit.account.TellitAccount;
import tellit.com.tellit.controller.operations.profile.SaveAva;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.custom_xmpp.messages.LikeNotifycation;
import tellit.com.tellit.model.custom_xmpp.messages.ReadedMessage;
import tellit.com.tellit.model.custom_xmpp.messages.ReviewMessage;
import tellit.com.tellit.model.custom_xmpp.messages.ReviewNotifycation;
import tellit.com.tellit.model.custom_xmpp.messages.VCardNotification;
import tellit.com.tellit.model.custom_xmpp.providers.FeedbackIQProvider;
import tellit.com.tellit.model.custom_xmpp.providers.FeedbackListIQProvider;
import tellit.com.tellit.model.custom_xmpp.providers.LikeNotifycationProvider;
import tellit.com.tellit.model.custom_xmpp.providers.RatingAllUsersProvider;
import tellit.com.tellit.model.custom_xmpp.providers.RatingUserByJidProvider;
import tellit.com.tellit.model.custom_xmpp.providers.ReadedMessageProvider;
import tellit.com.tellit.model.custom_xmpp.providers.RegisterFriendsProvider;
import tellit.com.tellit.model.custom_xmpp.providers.history.ChatIQProvider;
import tellit.com.tellit.model.custom_xmpp.providers.history.MessageIQProvider;
import tellit.com.tellit.model.custom_xmpp.providers.muc.MucHistoryProvider;
import tellit.com.tellit.model.custom_xmpp.providers.review.ReviewMessageProvider;
import tellit.com.tellit.model.custom_xmpp.providers.review.ReviewNotifycationProvider;
import tellit.com.tellit.model.custom_xmpp.providers.review.RewiewIQProvider;
import tellit.com.tellit.model.custom_xmpp.providers.review.RewiewIdAllFromIQProvider;
import tellit.com.tellit.model.custom_xmpp.providers.review.RewiewIdAllToIQProvider;
import tellit.com.tellit.model.custom_xmpp.providers.review.RewiewListIQProvider;
import tellit.com.tellit.model.custom_xmpp.providers.UnknownUserProvider;
import tellit.com.tellit.model.custom_xmpp.providers.VCardNotificationProvider;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackIQ;
import tellit.com.tellit.model.custom_xmpp.requests.history.ChatIQReq;
import tellit.com.tellit.model.custom_xmpp.requests.history.MessageIQReq;
import tellit.com.tellit.model.custom_xmpp.requests.muc.MucHistoryReq;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingAllUsersReq;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIQ;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllFromReq;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllToReq;
import tellit.com.tellit.model.custom_xmpp.requests.users.RegisterFriendsReq;
import tellit.com.tellit.model.custom_xmpp.requests.users.UnknownUser;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.AndroidModule;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.modules.XmppModule;
import tellit.com.tellit.service.ContactService;
import tellit.com.tellit.tools.IgnoreSSL;


/**
 * Created by probojnik on 17.05.15.
 */
public class MyApplication extends Application {
    public static Account sAccount;

    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
        Injector.init(new AndroidModule(this), new UserData(this), new XmppModule(), new SaveAva(), new VCardModule());

        HelperFactory.setHelper(getApplicationContext());
        IgnoreSSL.getInstance();

        ProviderManager.addExtensionProvider(ReviewMessage.ELEMENT, ReviewMessage.NAMESPACE, new ReviewMessageProvider());
        ProviderManager.addIQProvider(RewiewIQProvider.ELEMENT, ReviewIQ.NAMESPACE, new RewiewIQProvider());
        ProviderManager.addIQProvider(RatingAllUsersReq.ELEMENT, RatingAllUsersReq.NAMESPACE, new RatingAllUsersProvider());
        ProviderManager.addIQProvider("user", "custom:iq:rating", new RatingUserByJidProvider());
        ProviderManager.addIQProvider(FeedbackIQProvider.ELEMENT, FeedbackIQ.NAMESPACE, new FeedbackIQProvider());
        ProviderManager.addIQProvider(FeedbackListIQProvider.ELEMENT, FeedbackIQ.NAMESPACE, new FeedbackListIQProvider());
        ProviderManager.addIQProvider(ChatIQProvider.ELEMENT, ChatIQReq.NAMESPACE, new ChatIQProvider());
        ProviderManager.addIQProvider(MessageIQProvider.ELEMENT, MessageIQReq.NAMESPACE, new MessageIQProvider());
        ProviderManager.addIQProvider(RewiewListIQProvider.ELEMENT, ReviewIQ.NAMESPACE, new RewiewListIQProvider());
        ProviderManager.addIQProvider(ReviewIdAllFromReq.ELEMENT, ReviewIdAllFromReq.NAMESPACE, new RewiewIdAllFromIQProvider());
        ProviderManager.addIQProvider(ReviewIdAllToReq.ELEMENT, ReviewIdAllToReq.NAMESPACE, new RewiewIdAllToIQProvider());
        ProviderManager.addIQProvider(UnknownUser.ELEMENT, UnknownUser.NAMESPACE, new UnknownUserProvider());
        ProviderManager.addIQProvider(MucHistoryReq.ELEMENT, MucHistoryReq.NAMESPACE, new MucHistoryProvider());
        ProviderManager.addIQProvider("query", RegisterFriendsReq.NAMESPACE, new RegisterFriendsProvider());
        ProviderManager.addExtensionProvider(ReadedMessage.ELEMENT, ReadedMessage.NAMESPACE, new ReadedMessageProvider());
        ProviderManager.addExtensionProvider(ReviewNotifycation.ELEMENT, ReviewNotifycation.NAMESPACE, new ReviewNotifycationProvider());
        ProviderManager.addExtensionProvider(LikeNotifycation.ELEMENT, LikeNotifycation.NAMESPACE, new LikeNotifycationProvider());
        ProviderManager.addExtensionProvider(VCardNotification.ELEMENT, VCardNotification.NAMESPACE, new VCardNotificationProvider());

        final AccountManager am = AccountManager.get(this);
        if (sAccount == null) {
            sAccount = new Account("Tellit", TellitAccount.TYPE);
        }
        if(am.addAccountExplicitly(sAccount, null, null)) {
            ContentResolver.setIsSyncable(sAccount, TellitAccount.TYPE, 1);
            ContentResolver.setSyncAutomatically(sAccount, TellitAccount.TYPE, false);
        }



        startService(new Intent(this, ContactService.class));

    }


    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }

    public static void main(String ... arg){
        for(double d = 0.1; d != 1 ; d+=0.1){
            System.out.println(d);
        }
    }


}
