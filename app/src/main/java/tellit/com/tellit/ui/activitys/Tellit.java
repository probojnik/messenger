package tellit.com.tellit.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.sasl.SASLErrorException;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.SearchThreadController;
import tellit.com.tellit.controller.SingleThreadController;
import tellit.com.tellit.controller.operations.SincOperation;
//import tellit.com.tellit.controller.operations.SyncContacts;
import tellit.com.tellit.controller.operations.SyncReview;
import tellit.com.tellit.controller.tasks.SyncTasks;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.service.XmppService;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.autorithation.Login;
import tellit.com.tellit.ui.activitys.autorithation.Profile;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.chat.NavigationDrawerFragment;
import tellit.com.tellit.ui.fragments.containers.ChatsContainer;
import tellit.com.tellit.ui.fragments.containers.ContactsContainer;
import tellit.com.tellit.ui.fragments.containers.FavoritesContainer;
import tellit.com.tellit.ui.fragments.containers.SettingsContainer;
import tellit.com.tellit.ui.fragments.containers.TellitssContainer;

/**
 * Created by probojnik on 22.07.15.
 */
public class Tellit extends BaseActivity {
    @Inject MyApplication myApplication;
    @InjectView(android.R.id.tabs) TabWidget tabs;
    @InjectView(android.R.id.tabcontent) FrameLayout tabcontent;
    @InjectView(android.R.id.tabhost) FragmentTabHost tabhost;
    @Inject AbstractXMPPConnection connection;
    @Inject UserData userData;
    private NavigationDrawerFragment navigationDrawer;
    SyncReview syncReview;

    private static final String TAB_1_TAG = "tab_1";
    private static final String TAB_2_TAG = "tab_2";
    private static final String TAB_3_TAG = "tab_3";
    private static final String TAB_4_TAG = "tab_4";
    private static final String TAB_5_TAG = "tab_5";
    private TextView conversation_counter_txt;
    private TextView tellit_counter_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        getSupportActionBar().setDisplayShowCustomEnabled(true);

        if (userData.isAutoriz()) {
            if (!userData.getIS_PROFILE()) {

                Intent intent = new Intent(this, Profile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return;
            }
        } else {
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        if (!connection.isConnected()) {
          // showProgress();
            startService(new Intent(this, XmppService.class).putExtra(XmppService.COMMAND, XmppService.CONNECT));
        } else {
            hideProgress();
        }
        if(SingleThreadController.getInstance().isRun() || SearchThreadController.getInstance().isRun()){
            showProgress();
        }else{
            hideProgress();
        }

        setContentView(R.layout.tellit_main);
        ButterKnife.inject(this);
        Injector.inject(this);
        EventBus.getDefault().register(this);
        getSupportActionBar().setTitle("Tellit");

        navigationDrawer = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        tabhost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        TabHost.TabSpec tabSpec;
        tabSpec = tabhost.newTabSpec(TAB_1_TAG);
        tabSpec.setIndicator(getImageView(R.drawable.tab_srar));
        tabhost.addTab(tabSpec, FavoritesContainer.class, null);


        tabSpec = tabhost.newTabSpec(TAB_2_TAG);
        tabSpec.setIndicator(getImageView(R.drawable.tab_contact));
        tabhost.addTab(tabSpec, ContactsContainer.class, null);

        tabSpec = tabhost.newTabSpec(TAB_3_TAG);
//        tabSpec.setIndicator("Tellit");

        View tellit_tab = getTextView("Tellit");
        tellit_counter_txt = (TextView) tellit_tab.findViewById(R.id.counter);
        tabSpec.setIndicator(tellit_tab);
        
        tabhost.addTab(tabSpec, TellitssContainer.class, null);

        tabSpec = tabhost.newTabSpec(TAB_4_TAG);
        View chat_tab = getImageView(R.drawable.tab_chat);
        conversation_counter_txt = (TextView) chat_tab.findViewById(R.id.counter);

        tabSpec.setIndicator(chat_tab);
        tabhost.addTab(tabSpec, ChatsContainer.class, null);

        tabSpec = tabhost.newTabSpec(TAB_5_TAG);
        tabSpec.setIndicator(getImageView(R.drawable.tab_settings));
        tabhost.addTab(tabSpec, SettingsContainer.class, null);

        tabhost.setCurrentTabByTag(TAB_2_TAG);

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                if(TAB_3_TAG.equals(tabId)){
                    userData.resetTellitCounter();
                    updateTellitCounter();
                }
                U.hideKeyboard(Tellit.this);
            }
        });

        setTabWidgetListener(tabhost, new Runnable() {
            @Override
            public void run() {
                clearCurrentStack();
            }
        });
    }



    private void setTabWidgetListener(TabHost tabHost, final Runnable run) {
        int numberOfTabs = tabHost.getTabWidget().getChildCount();
        for (int t = 0; t < numberOfTabs; t++) {
            tabHost.getTabWidget().getChildAt(t).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        run.run();
                    }
                    return false;
                }
            });
        }
    }

    private View getImageView(int res){
        View view = getLayoutInflater().inflate(R.layout.tab_img, null);
        ImageView img = (ImageView) view.findViewById(R.id.image);
        img.setImageResource(res);
        TextView counter = (TextView) view.findViewById(R.id.counter);
        counter.setVisibility(View.GONE);
        return view;
    }

    private View getTextView(String text){
        View view = getLayoutInflater().inflate(R.layout.tab_text, null);
        TextView tellit_txt = (TextView) view.findViewById(R.id.text);
        tellit_txt.setText(text);
        TextView counter = (TextView) view.findViewById(R.id.counter);
        counter.setVisibility(View.GONE);
        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(navigationDrawer != null){
            navigationDrawer.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(navigationDrawer != null && !navigationDrawer.isDrawerOpen()) {
        // TODO navigation drawer logic
            return true;
        } else return super.onCreateOptionsMenu(menu);
    }

    public void setGotoFragment(BaseContainerFragment gotoFragment){
        if(navigationDrawer != null){
            navigationDrawer.setGotoFragment(gotoFragment); // (BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_4_TAG)
        }
    }

    public void lockDrawer(boolean lock) {
        if(navigationDrawer != null) {
            navigationDrawer.lockDrawer(lock);
        }
    }

    public void onEventMainThread(List<ChatData> chatDataList) {
        int couter =0;
        for (ChatData chatData : chatDataList){
            if(chatData.getCount()>0){
                couter++;
            }
        }
        if(couter>0){
            conversation_counter_txt.setVisibility(View.VISIBLE);
            conversation_counter_txt.setText("" + couter);
        }else{
            conversation_counter_txt.setVisibility(View.GONE);
        }
    }

    public void onEventMainThread(SyncReview.SincOperationResult result) {
        if(SingleThreadController.getInstance().isRun() || SearchThreadController.getInstance().isRun()){
            showProgress();
        }else
            hideProgress();

    }
    public void onEventMainThread(SASLErrorException result) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    public void onEvent(AbstractXMPPConnection connection) {
        if(connection != null && !connection.isConnected()){
            startService(new Intent(this, XmppService.class).putExtra(XmppService.COMMAND, XmppService.CONNECT));
        }

    }


    public void onEventMainThread(ReviewData event) {
        if(!TAB_3_TAG.equals(tabhost.getCurrentTabTag())) {
            updateTellitCounter();
        }
    }
//    public void onEventMainThread(SyncContacts.SincOperationResult result) {
//        if(SingleThreadController.getInstance().isRun() || SearchThreadController.getInstance().isRun()){
//            showProgress();
//        } else {
//            hideProgress();
//        }
//    }

    private void updateTellitCounter(){
        int counter = userData.getIncTellitCounter();
        if (counter > 0) {
            tellit_counter_txt.setVisibility(View.VISIBLE);
            tellit_counter_txt.setText("" + counter);
            userData.playNotificationSound();
        } else {
            tellit_counter_txt.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (userData.getIS_PROFILE()) {
            startService(new Intent(this, XmppService.class).putExtra(XmppService.COMMAND, XmppService.STOP));
        }
    }

    public void hideTabs() {
        tabs.setVisibility(View.GONE);
    }

    public void showTabs() {
        tabs.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        boolean isPopFragment = false;
        BaseContainerFragment current = getCurrentFragment();
        if(current != null){
            isPopFragment = current.popFragment(true);
        }
        if (!isPopFragment) {
            finish();
        }
    }

    private boolean clearCurrentStack() {
        BaseContainerFragment current = getCurrentFragment();
        int loops = 0;
        if(current != null){
            loops = current.clearStackAsync();
        }
//        TraceHelper.print(loops, current);
        return loops > 0;
    }

    @Nullable
    private BaseContainerFragment getCurrentFragment(){
        String currentTabTag = tabhost.getCurrentTabTag();
        if (currentTabTag.equals(TAB_1_TAG)) {
            return  (BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_1_TAG);
        } else if (currentTabTag.equals(TAB_2_TAG)) {
            return (BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_2_TAG);
        } else if (currentTabTag.equals(TAB_3_TAG)) {
            return (BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_3_TAG);
        } else if (currentTabTag.equals(TAB_4_TAG)) {
            return (BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_4_TAG);
        } else if (currentTabTag.equals(TAB_5_TAG)) {
            return (BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_5_TAG);
        } else {
            return null;
        }
    }

    public void syncReviews(){
        if(syncReview != null && AsyncTask.Status.FINISHED != syncReview.getStatus()) return;
        showProgress();
//        syncReview = new SyncReview();
//        syncReview.execute();
//        SingleThreadController.getInstance().execute(new SyncReview());
        SyncTasks.getInstance().syncAllReview();



    }
}