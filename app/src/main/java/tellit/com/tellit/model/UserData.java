package tellit.com.tellit.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.modules.AndroidModule;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.modules.XmppModule;
import tellit.com.tellit.service.XmppService;



/**
 * Created by root on 05.06.15.
 */
@Module(library = true,  includes = {XmppModule.class,VCardModule.class})
public class UserData {
    private final String USER_LOGIN  = "user_login";
    private final String USER_PASSWORD  = "user_password";
    private final String USER_FIRST_NAME  = "user_first_name";
    private final String USER_LAST_NAME  = "user_last_name";
    private final String USER_JID  = "user_jid";
    private final String LOGIN_CODE = "login_code";
    private final String LOGIN_PHONE = "login_phone";
    private final String IS_AUTORIZ = "is_autoriz";
    private final String IS_PROFILE = "is_profile";
    private final String AVA = "ava";
    private final String TELLIT_INK = "tellit_ink";

    MyApplication myApplication;

    SharedPreferences sharedPreferences;

    @Provides @Singleton
    UserData provideUserData(){
        return this;
    }

    public UserData(MyApplication myApplication) {

        this.myApplication = myApplication;
        sharedPreferences = myApplication.getSharedPreferences("imclient", Context.MODE_PRIVATE);
    }

    public String getMyJid(){
       return sharedPreferences.getString(USER_JID,"");
    }
    public void setMyJid(String jid){
        sharedPreferences.edit().putString(USER_JID,jid).commit();
    }
    public String getMyLogin(){
        return sharedPreferences.getString(USER_LOGIN,"");
    }
    public void setMyLogin(String myLogin){
        sharedPreferences.edit().putString(USER_LOGIN,myLogin).commit();

    }

    public String getUserPassword() {
        return sharedPreferences.getString(USER_PASSWORD,"");
    }
    public void setUserPassword(String password) {
         sharedPreferences.edit().putString(USER_PASSWORD,password).commit();
    }

    public String getUserFirstName() {
        return sharedPreferences.getString(USER_FIRST_NAME,"");
    }
    public void setUSER_FIRST_NAME(String first_name){
        sharedPreferences.edit().putString(USER_FIRST_NAME,first_name).commit();
    }
    public void setUSER_LAST_NAME(String last_name){
        sharedPreferences.edit().putString(USER_LAST_NAME, last_name).commit();
    }

    public  String getUserLastName() {
        return sharedPreferences.getString(USER_LAST_NAME, "");
    }

    public  int getUserJid() {
        return sharedPreferences.getInt(USER_JID, 0);
    }
    public void setLoginCode(int code){
        sharedPreferences.edit().putInt(LOGIN_CODE,code).commit();
    }

    public int getLOGIN_CODE() {
        return sharedPreferences.getInt(LOGIN_CODE, 0);
    }

    public String getLOGIN_PHONE() {
        return sharedPreferences.getString(LOGIN_PHONE, "");
    }
    public void setLOGIN_PHONE(String phone) {
         sharedPreferences.edit().putString(LOGIN_PHONE,phone).commit();
    }

    public  boolean isAutoriz() {
        return sharedPreferences.getBoolean(IS_AUTORIZ,false);
    }
    public  void setAutoriz(boolean autoriz) {
         sharedPreferences.edit().putBoolean(IS_AUTORIZ,autoriz).commit();
    }

    public boolean getIS_PROFILE() {
        return sharedPreferences.getBoolean(IS_PROFILE,false);
    }
    public void setIS_PROFILE(boolean profile) {
         sharedPreferences.edit().putBoolean(IS_PROFILE,profile).commit();
    }

    public MyApplication getContext(){
        return myApplication;
    }




    public void setAva(String ava) {
        sharedPreferences.edit().putString(AVA,ava).commit();
    }
    public String getAva(){
        return sharedPreferences.getString(AVA,"");
    }

    public void incTellitCounter(){
        int counter = sharedPreferences.getInt(TELLIT_INK,0);
        sharedPreferences.edit().putInt(TELLIT_INK,++counter).commit();
    }
    public int getIncTellitCounter(){
        return sharedPreferences.getInt(TELLIT_INK,0);
    }
    public void resetTellitCounter(){
        sharedPreferences.edit().putInt(TELLIT_INK,0).commit();
    }

    public void playNotificationSound(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(myApplication.getBaseContext(), notification);
        r.play();
    }
}
