package tellit.com.tellit.modules;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.controller.ChatController;
import tellit.com.tellit.controller.MultiChatController;
import tellit.com.tellit.controller.ReviewController;
import tellit.com.tellit.controller.operations.SearchContact;
import tellit.com.tellit.controller.operations.SyncReview;
import tellit.com.tellit.controller.operations.profile.SaveAva;
import tellit.com.tellit.controller.operations.profile.SendVCardOperation;
import tellit.com.tellit.controller.operations.SincOperation;
import tellit.com.tellit.controller.tasks.ContactSync;
import tellit.com.tellit.controller.tasks.LikesSync;
import tellit.com.tellit.controller.tasks.RatingSinc;
import tellit.com.tellit.controller.tasks.ReviewSync;
import tellit.com.tellit.controller.tasks.SyncTasks;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.service.ContactService;
import tellit.com.tellit.service.XmppService;
import tellit.com.tellit.service.gcm.RegistrationIntentService;
import tellit.com.tellit.sync.SyncAdapter;
import tellit.com.tellit.ui.activitys.CropImageActivity;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.activitys.autorithation.Login;
import tellit.com.tellit.ui.activitys.autorithation.Otp;
import tellit.com.tellit.ui.activitys.autorithation.OtpSucc;
import tellit.com.tellit.ui.activitys.autorithation.Profile;
import tellit.com.tellit.ui.fragments.Settings;
import tellit.com.tellit.ui.fragments.autorithation.ProfileFr;
import tellit.com.tellit.ui.fragments.chat.ChatList;
import tellit.com.tellit.ui.fragments.chat.ChatListMain;
import tellit.com.tellit.ui.fragments.chat.ChatListMenu;
import tellit.com.tellit.ui.fragments.chat.NavigationDrawerFragment;
import tellit.com.tellit.ui.fragments.chat.ReviewAdapter;
import tellit.com.tellit.ui.fragments.chat.ReviewChat;
import tellit.com.tellit.ui.fragments.chat.SimpleChat;
import tellit.com.tellit.ui.fragments.contacts.ContactDetail;
import tellit.com.tellit.ui.fragments.review.ContactsReviewList;
import tellit.com.tellit.ui.fragments.review.LikeReview;
import tellit.com.tellit.ui.fragments.review.TellitAboutMe;
import tellit.com.tellit.ui.fragments.review.TellitAdapter;
import tellit.com.tellit.ui.fragments.review.TellitAll;
import tellit.com.tellit.ui.fragments.review.TellitFromUser;
import tellit.com.tellit.ui.fragments.review.TellitMe;
import tellit.com.tellit.ui.fragments.review.TellitToUser;


@Module(library = true,complete = false,
        includes = {UserData.class,XmppModule.class,SaveAva.class,VCardModule.class},
//        staticInjections = {ContactData.class},
        injects = {
                UserData.class,
                XmppModule.class,
                XmppService.class,
                VCardModule.class,
                ContactService.class
                , Login.class
                , Otp.class
                , OtpSucc.class
                , Profile.class
                , ProfileFr.class
                , SendVCardOperation.class
                , Tellit.class
                , ReviewAdapter.class
                , ContactsReviewList.class
                , TellitAll.class
                , Settings.class
                , SincOperation.class
                , SaveAva.class
                , CustomStanzaController.class
                , ContactDetail.class
                , LikeReview.class
                , TellitAll.class
                , TellitMe.class
                , TellitAboutMe.class
                , MultiChatController.class
                , SimpleChat.class
                , ReviewController.class
                , ReviewData.class
                , ChatController.class
                , MultiChatController.class
                , RegistrationIntentService.class
                , TellitFromUser.class
                , TellitToUser.class
                , SyncAdapter.class
                , SyncReview.class
                , ContactSync.class
                , VCardModule.class
                , ReviewChat.class
                , TellitAdapter.class
                ,SearchContact.class
                ,ChatList.class
                ,NavigationDrawerFragment.class
                ,ChatListMenu.class
                ,ChatListMain.class
                ,CropImageActivity.class
                ,LikesSync.class
                ,ReviewSync.class
                ,SyncTasks.class
                ,RatingSinc.class

}
        )
public class AndroidModule {
    private final MyApplication application;


    public AndroidModule(MyApplication application) {
        this.application = application;
    }



    @Provides @Singleton  MyApplication provideApplicationContext() {
        return application;
    }
    @Provides @Singleton  SharedPreferences provideSharedPreference(){
        return application.getSharedPreferences("imclient", Context.MODE_PRIVATE);
    }
    @Provides
    ContentResolver provideContentResolver(){
        return application.getBaseContext().getContentResolver();
    }
}

