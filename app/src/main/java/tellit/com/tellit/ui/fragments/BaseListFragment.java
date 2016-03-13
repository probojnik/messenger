package tellit.com.tellit.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tellit.com.tellit.MyApplication;
import tellit.com.tellit.tools.C;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;

/**
 * Created by probojnik on 21.07.15.
 */
public class BaseListFragment extends ListFragment {
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TraceHelper.printClause(C.LOG_LIFECYCLE, LOG);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
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

}
