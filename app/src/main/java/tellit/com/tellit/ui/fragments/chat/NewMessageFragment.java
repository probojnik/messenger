package tellit.com.tellit.ui.fragments.chat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.contacts.ContactAdapter;
import tellit.com.tellit.ui.fragments.contacts.ContactDetail;
import tellit.com.tellit.ui.fragments.contacts.ContactList;

/**
 * Created by Stas on 12.08.2015.
 */
public class NewMessageFragment extends ContactList {

   @Override
   protected BaseContainerFragment getContainer() {
      return (BaseContainerFragment) getParentFragment();
   }

   @Override
   public void onListItemClick(ListView l, View v, int position, long id) {
      U.hideKeyboard(getActivity());
      getContainer().replaceFragment(ContactDetail.instanceSimpleChat(contactAdapter.getItem(position)), true);
   }

   @Override
   protected ContactAdapter obtainInstanceOfAdapter(List<ContactData> list){ // for Override
      return new ContactAdapter(getAct(), list, true);
   }
}