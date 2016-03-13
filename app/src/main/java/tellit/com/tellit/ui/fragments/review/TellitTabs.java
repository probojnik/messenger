package tellit.com.tellit.ui.fragments.review;

import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tellit.com.tellit.R;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.fragments.BaseFragment;

/**
 * Created by probojnik on 26.07.15.
 */
public class TellitTabs extends BaseFragment implements ActionBar.TabListener {
    private static final String TAB_1_TAG = "tab_1";
    private static final String TAB_2_TAG = "tab_2";
    private static final String TAB_3_TAG = "tab_3";

    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(android.R.id.tabs)
    TabWidget tabs;
    @InjectView(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @InjectView(R.id.tabHost)
    TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tellit_tellit_tabs, container, false);
        ButterKnife.inject(this, rootView);


        TabHost.TabSpec tabSpec;
        tabHost.setup();
        tabSpec = tabHost.newTabSpec(TAB_1_TAG);
        View all_tab = getTextView("ALL");
        tabSpec.setIndicator(all_tab);
        tabSpec.setContent((TabHost.TabContentFactory) new TabContentFactory());
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_2_TAG);
        View my_tab = getTextView("My");
        tabSpec.setIndicator(my_tab);
        tabSpec.setContent((TabHost.TabContentFactory) new TabContentFactory());
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_3_TAG);
        View about_tab = getTextView("About me");
        tabSpec.setIndicator(about_tab);
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
                    case TAB_3_TAG:
                        viewpager.setCurrentItem(2);
                        break;
                }
            }
        });

        viewpager.setAdapter(new PageAdapter(getChildFragmentManager()));
//
        tabHost.setCurrentTab(0);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_empty, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();

        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("Tellit");


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);

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

                    return new TellitAll();

                case 1:


                    return new TellitMe();

                case 2:


                    return new TellitAboutMe();

            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }





}
