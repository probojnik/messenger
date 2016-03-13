package tellit.com.tellit.ui.fragments.review;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tellit.com.tellit.R;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;

/**
 * Created by probojnik on 26.07.15.
 */
public class TellitTabsAboutUser extends BaseFragment implements ActionBar.TabListener {
    private static final String TAB_1_TAG = "tab_1";
    private static final String TAB_2_TAG = "tab_2";
    TellitToUser toUser;
    TellitFromUser fromUser;

    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    long[] from_ids, to_ids;
    String contactName, jid;
    @InjectView(R.id.tabHost)
    TabHost tabHost;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tellit_tellit_tabs, container, false);
        ButterKnife.inject(this, rootView);

        to_ids = getArguments().getLongArray("params_to");
        from_ids = getArguments().getLongArray("params_from");
        contactName = getArguments().getString("contact_name");
        jid = getArguments().getString("jid");


        TabHost.TabSpec tabSpec;
        tabHost.setup();
        tabSpec = tabHost.newTabSpec(TAB_1_TAG);
        View to_tab = getTextView("To");
        tabSpec.setIndicator(to_tab);
        tabSpec.setContent((TabHost.TabContentFactory) new TabContentFactory());
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_2_TAG);
        View from_tab = getTextView("From");
        tabSpec.setIndicator(from_tab);
        tabSpec.setContent((TabHost.TabContentFactory) new TabContentFactory());
        tabHost.addTab(tabSpec);



        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case TAB_1_TAG:
                        viewpager.setCurrentItem(0);
                        break;
                    case TAB_2_TAG:
                        viewpager.setCurrentItem(1);
                        break;

                }
            }
        });


        viewpager.setAdapter(new PageAdapter(getChildFragmentManager()));
        viewpager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        tabHost.setCurrentTab(position);
                    }
                });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(contactName);

        setHasOptionsMenu(true);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.tellit_about_user, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getContainer().clearStack();

                break;

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        ((Tellit) getActivity()).hideProgress();
        setHasOptionsMenu(false);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    public class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    toUser = new TellitToUser();
                    toUser.setReviewId(to_ids);
                    toUser.setJID(jid);
                    return toUser;

                case 1:
                    fromUser = new TellitFromUser();
                    fromUser.setReviewId(from_ids);
                    fromUser.setJID(jid);
                    return fromUser;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
