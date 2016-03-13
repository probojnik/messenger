package tellit.com.tellit.ui.fragments.containers;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import tellit.com.tellit.R;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.chat.ChatList;
import tellit.com.tellit.ui.fragments.chat.ChatListMain;

/**
 * Created by probojnik on 25.07.15.
 */
public class ChatsContainer extends BaseContainerFragment {
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((Tellit)activity).setGotoFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.container_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mIsViewInited) {
            mIsViewInited = true;
            replaceFragment(new ChatListMain(), false);
        }
    }

}