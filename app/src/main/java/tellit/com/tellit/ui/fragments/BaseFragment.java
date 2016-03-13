package tellit.com.tellit.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;


import java.util.Objects;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.R;
import tellit.com.tellit.tools.C;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;

/**
 * Created by probojnik on 21.07.15.
 */
public class BaseFragment extends Fragment {
    protected final String LOG = ((Object)this).getClass().getSimpleName();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onStart() {
        super.onStart();
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onResume() {
        super.onResume();
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onPause() {
        super.onPause();
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onStop() {
        super.onStop();
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    protected MyApplication getApp(){
        return (MyApplication) getActivity().getApplication();
    }

    protected BaseActivity getAct(){
        return (BaseActivity) getActivity();
    }

    public class TabContentFactory extends Intent implements TabHost.TabContentFactory {
        @Override
        public View createTabContent(String tag) {
            return new View(getActivity());
        }
    }

    protected View getTextView(String text){
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab_text, null);
        TextView tellit_txt = (TextView) view.findViewById(R.id.text);
        tellit_txt.setText(text);
        TextView counter = (TextView) view.findViewById(R.id.counter);
        counter.setVisibility(View.GONE);
        return view;
    }

    protected BaseContainerFragment getContainer(){
        Fragment parent = getParentFragment();
        while (true){
            if(parent instanceof BaseContainerFragment)
                return (BaseContainerFragment) parent;
            else if(parent == null)
                return null;
            else
                parent = parent.getParentFragment();
        }

    }



}
