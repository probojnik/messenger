package tellit.com.tellit.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.R;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;

/**
 * Created by probojnik on 21.07.15.
 */
public abstract class BaseContainerFragment extends Fragment  {
    protected boolean mIsViewInited;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setDisplayHomeAsUpEnabled();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        setDisplayHomeAsUpEnabled();
    }

    private void setDisplayHomeAsUpEnabled(){
        boolean back = getChildFragmentManager().getBackStackEntryCount() > 0;
        ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(back);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public int clearStack(){
        int result = 0;
        while (popFragment(true)){
            ++result;
        }
        return result;
    }

    public int clearStackAsync(){
        FragmentManager fm = getChildFragmentManager();
        int count = fm.getBackStackEntryCount();
        for(int i = 0; i < count; ++i) {
            TraceHelper.print(i, fm.getBackStackEntryAt(i));
            fm.popBackStack();
        }
        return count;
    }

    public  void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container_framelayout, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        getChildFragmentManager().executePendingTransactions();
    }

    public void addFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.add(R.id.container_framelayout, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        getChildFragmentManager().executePendingTransactions();
    }

    public boolean popFragment(boolean immediate) {
        try {
            if(immediate) {
                boolean res = getChildFragmentManager().popBackStackImmediate();
                if(res){
                    try {
                        getCurrentFragment().onResume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return res;
            } else {
                getChildFragmentManager().popBackStack();
                return true;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Fragment getCurrentFragment() throws Exception {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.container_framelayout);
        if(fragment!=null)
            return fragment;
        else throw new Exception();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onEvent(BaseActivity baseActivity){
        popFragment(true);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}