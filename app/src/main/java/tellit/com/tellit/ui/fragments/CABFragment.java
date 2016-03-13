package tellit.com.tellit.ui.fragments;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.R;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.tools.C;
import tellit.com.tellit.tools.Clipboard;
import tellit.com.tellit.tools.TextUtil;

/**
 * Created by Stas on 27.08.2015.
 */
public abstract class CABFragment extends BaseFragment {

    public abstract ListView getListView();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.menu_cab_chat, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_copy:
                        SparseBooleanArray checked = getListView().getCheckedItemPositions();
                        List<String> intoClipboard = new ArrayList<String>();
                        for (int i = 0; i < getListView().getCount(); i++) {
                            if (checked.get(i)) {
                                MessageData itemData = (MessageData) getListView().getItemAtPosition(i);
                                intoClipboard.add(itemData.getBody());
                            }
                        }
                        Clipboard.intoClipboard(getActivity(), intoClipboard);

                        Toast.makeText(getActivity(), TextUtil.join(CharBuffer.wrap(new char[]{C.NEXT_LINE}), intoClipboard), Toast.LENGTH_SHORT).show();
                        mode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(String.valueOf(getListView().getCheckedItemCount()));
            }
        });
    }
}