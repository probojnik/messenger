package tellit.com.tellit.ui.fragments.containers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tellit.com.tellit.R;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.chat.ChatList;
import tellit.com.tellit.ui.fragments.contacts.ContactList;
import tellit.com.tellit.ui.fragments.contacts.ContactTabs;

/**
 * Created by probojnik on 25.07.15.
 */
public class ContactsContainer extends BaseContainerFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.container_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mIsViewInited) {
            mIsViewInited = true;
            replaceFragment(new ContactTabs(), false);
        }
    }
}