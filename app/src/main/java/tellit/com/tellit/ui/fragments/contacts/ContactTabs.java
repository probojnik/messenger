package tellit.com.tellit.ui.fragments.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tellit.com.tellit.R;
import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;

/**
 * Created by Stas on 11.08.2015.
 */
public class ContactTabs extends BaseFragment implements ActionBar.TabListener, SearchView.OnQueryTextListener {
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(R.id.tabHost2)
    TabHost tabHost2;
    private SupportMenuItem searchItem;
    private static final String TAB_1_TAG = "tab_1";
    private static final String TAB_2_TAG = "tab_2";
    private boolean searchState;
    private int currentTab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cantact_tabs, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabHost.TabSpec tabSpec;
        tabHost2.setup();
        tabSpec = tabHost2.newTabSpec(TAB_1_TAG);
        View to_tab = getTextView("All");
        tabSpec.setIndicator(to_tab);
        tabSpec.setContent((TabHost.TabContentFactory) new TabContentFactory());
        tabHost2.addTab(tabSpec);

        tabSpec = tabHost2.newTabSpec(TAB_2_TAG);
        View from_tab = getTextView("Tellit");
        tabSpec.setIndicator(from_tab);
        tabSpec.setContent((TabHost.TabContentFactory) new TabContentFactory());
        tabHost2.addTab(tabSpec);
        tabHost2.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                TraceHelper.print("searchView " + searchState + " " + tabId + " " + tabHost2.getCurrentTabTag());
                if (!searchState) {
                    switch (tabId) {
                        case TAB_1_TAG:
                            viewpager.setCurrentItem(0);
                            currentTab = tabHost2.getCurrentTab();
                            break;
                        case TAB_2_TAG:
                            viewpager.setCurrentItem(1);
                            currentTab = tabHost2.getCurrentTab();
                            break;
                    }
                } else {
                    tabHost2.setCurrentTab(currentTab);
                }
            }
        });
        currentTab = tabHost2.getCurrentTab();

        viewpager.setAdapter(new PageAdapter(getChildFragmentManager()));
        viewpager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        tabHost2.setCurrentTab(position);

                        ContactList fragment = (ContactList) ((PageAdapter) viewpager.getAdapter()).instantiateItem(viewpager, position);
                        if (fragment != null) fragment.onSwitch();
                    }
                });

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && searchItem.isActionViewExpanded()) {

                    searchItem.collapseActionView();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_list_search, menu);

        searchItem = (SupportMenuItem) menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("Contacts");

        setHasOptionsMenu(true);

//        ((BaseActivity) getActivity()).getSupportActionBar().removeAllTabs();
//        ((BaseActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        ((BaseActivity) getActivity()).getSupportActionBar().addTab(
//                ((BaseActivity) getActivity()).getSupportActionBar().newTab().setText("All").setTabListener(this));
//        ((BaseActivity) getActivity()).getSupportActionBar().addTab(
//                ((BaseActivity) getActivity()).getSupportActionBar().newTab().setText("Tellit").setTabListener(this));
    }

    @Override
    public void onPause() {
        super.onPause();
        setHasOptionsMenu(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        ((BaseActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        if (searchItem != null)
            searchItem.collapseActionView();
//        MenuItemCompat.collapseActionView(searchItem);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        TraceHelper.print("searchView "+query);
        searchState = false;
        SearchView.OnQueryTextListener onQueryTextListener = getOnQueryTextListener();
        return onQueryTextListener != null ? onQueryTextListener.onQueryTextSubmit(query) : false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        TraceHelper.print("searchView "+newText);
        if(TextUtil.isEmpty(newText)){
            searchState = false;
        }else{
            searchState = true;
        }
        SearchView.OnQueryTextListener onQueryTextListener = getOnQueryTextListener();
        return onQueryTextListener != null ? onQueryTextListener.onQueryTextChange(newText) : false;
    }

    @Nullable
    public SearchView.OnQueryTextListener getOnQueryTextListener() {
        try {
            return ((PageAdapter) viewpager.getAdapter()).getCurrentListener();
        } catch (NullPointerException | ClassCastException e) {
        }
        return null;
    }



    protected BaseContainerFragment getContainer() { // for Override
        return (BaseContainerFragment) getParentFragment();
    }

    public class PageAdapter extends FragmentPagerAdapter {
        private SearchView.OnQueryTextListener mCurrentListener;

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ContactList.getInstance(false);
                case 1:
                    return ContactList.getInstance(true);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentListener != object) {
                mCurrentListener = ((SearchView.OnQueryTextListener) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        public SearchView.OnQueryTextListener getCurrentListener() {
            return mCurrentListener;
        }
    }

}