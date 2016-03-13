package tellit.com.tellit.ui.fragments.chat;

import android.os.Bundle;

import tellit.com.tellit.tools.U;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;

/**
 * Created by Stas on 12.08.2015.
 */
public class ChatListMenu extends ChatList {
    private BaseContainerFragment containerFragment;

    @Override
    protected void openSimpleChat(int i) {
        SimpleChat simpleChat = new SimpleChat();
        Bundle bundle = new Bundle();
        bundle.putString("params", chatAdapter.getItem(i).getJid());
        simpleChat.setArguments(bundle);

        containerFragment.clearStack();
        containerFragment.replaceFragment(simpleChat, true);
    }

    public void setGotoFragment(BaseContainerFragment gotoFragment) {
        this.containerFragment = gotoFragment;
    }

    @Override
    public void onRefresh() {
        U.toast(getActivity(), "Not implemented");
        setRefreshing(false);
    }
}
