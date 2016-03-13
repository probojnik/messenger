package tellit.com.tellit.ui.fragments.chat;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import tellit.com.tellit.R;
import tellit.com.tellit.tools.PreferenceMediator;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;

/**
 * Created by Stas on 10.08.2015.
 */
public class NavigationDrawerFragment extends ChatListMenu {
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_DRAWER = "navigation_drawer_learned";
    private DrawerLayout drawerLayout;
    private View fragmentContainerView;
    private ActionBarDrawerToggle drawerToggle;
    private RunnableDrawer runnableDrawer;
    private int currentSelectedPosition = 0;
    private boolean fromSavedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            this.currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            this.fromSavedInstanceState = true;
        }
        lockDrawer(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(drawerLayout != null){
            drawerLayout.setDrawerListener(null);
            drawerLayout.removeCallbacks(runnableDrawer);
            drawerLayout = null;
        }
        fragmentContainerView = null;
        drawerToggle = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void setUp(int fragmentID, DrawerLayout drawerLayout) {
//        TraceHelper.print(fragmentID, drawerLayout);
        this.fragmentContainerView = getActivity().findViewById(fragmentID);
        this.drawerLayout = drawerLayout;

//        this.drawerLayout.setDrawerShadow();
        this.drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, true, R.drawable.ic_menu_home, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if(getActivity() != null){
                    if(newState != 0 && !isDrawerOpen() && !isVisible()){
                        actionBarDrawed(true);
                    } else if(newState == 0 && !isDrawerOpen() && !isVisible()) {
                        actionBarDrawed(false);
                    }
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (isAdded()){
                    getActivity().invalidateOptionsMenu();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(isAdded()){
                    getActivity().invalidateOptionsMenu();
                }
            }
        };

        if (!fromSavedInstanceState && !PreferenceMediator.checkSwitchPref(getActivity(), PREF_DRAWER, false, true)){
            drawerLayout.openDrawer(fragmentContainerView);
        }

        drawerLayout.post(runnableDrawer);
        drawerLayout.setDrawerListener(drawerToggle);
        lockDrawer(true);
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && fragmentContainerView != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    public void lockDrawer(boolean lock) {
//        TraceHelper.print(lock);
        if (drawerLayout != null) {
            if (lock) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
    }

    private void actionBarDrawed(boolean open){
//        ((BaseActivity)getActivity()).getSupportActionBar().setTitle(open?"Open":"Close");
    }

    private static class RunnableDrawer implements Runnable {
        private NavigationDrawerFragment parent;

        @Override
        public void run() {
            if (parent != null) {
                parent.drawerToggle.syncState();
            }
        }

        public void unbind() {
            this.parent = null;
        }
    }
}