package tellit.com.tellit.ui.fragments.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.controller.MultiThreadController;
import tellit.com.tellit.controller.SingleThreadController;
import tellit.com.tellit.controller.operations.OperationCalback;
import tellit.com.tellit.controller.operations.SearchContact;
import tellit.com.tellit.controller.operations.SincOperation;
import tellit.com.tellit.controller.tasks.SyncTasks;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.users.RegisterFriendsReq;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.SwipeRefreshListFragment;


/**
 * Created by probojnik on 09.06.15.
 */
public class ContactList extends SwipeRefreshListFragment implements SearchView.OnQueryTextListener {
    private final static String MP_ONLY_TELLIT_CONTACTS = "MAPPING_KEY_ONLY_TELLYT_CONTACTS";
    protected ContactAdapter contactAdapter;
    private List<ContactData> contactList = new ArrayList<>();
    private boolean onlyTellitContacts;

    public static ContactList getInstance(boolean onlyTellitContacts){
        Bundle b = new Bundle();
        b.putBoolean(MP_ONLY_TELLIT_CONTACTS, onlyTellitContacts);
        ContactList result = new ContactList();
        result.setArguments(b);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            onlyTellitContacts = getArguments().getBoolean(MP_ONLY_TELLIT_CONTACTS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactAdapter = obtainInstanceOfAdapter(contactList);
        setListAdapter(contactAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    @Override
    public void onRefresh() {
        setRefreshing(false);
        getAct().showProgress();

        SyncTasks.getInstance().syncAll();

    }

    public void onSwitch(){
        setData();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        U.hideKeyboard(getActivity());
        getContainer().replaceFragment(ContactDetail.getInstance(contactAdapter.getItem(position)), true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    protected ContactAdapter obtainInstanceOfAdapter(List<ContactData> list){ // for Override
        return new ContactAdapter(getAct(), list, false);
    }

    protected BaseContainerFragment getContainer(){ // for Override
        return (BaseContainerFragment) getParentFragment().getParentFragment();
    }

    private void setData() {
        try {
            Where where = HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().orderBy("name", true).where().eq("isValid", true);
            if(onlyTellitContacts)
                where.and().eq("isInstalls", true);
            contactList = where.query();
//            TraceHelper.print(contactList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        contactAdapter.clear();
        contactAdapter.addAll(contactList);
        contactAdapter.notifyDataSetChanged();
    }

//    public void onEventMainThread(SyncContacts.SincOperationResult result) {
//        if (result.isResult())
//            setData();
//    }

    public void onEventMainThread(ContactData contactData) {
        setData();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0) {
            contactAdapter = new ContactAdapter(getAct(), contactList, false);
            setListAdapter(contactAdapter);
            return true;
        }
        List<ContactData> tempList = new ArrayList<>();
        for (ContactData c : contactList) {
            if (c.getName() != null && c.getName().length() > 0 && c.getName().toLowerCase().contains(newText.toString().toLowerCase())) {
                    tempList.add(c);
            }else if(c.getNumber() != null && c.getNumber().length()>0 && c.getNumber().toLowerCase().contains(newText.toLowerCase())){
                    tempList.add(c);
            }

        }
        contactAdapter = new ContactAdapter(getAct(), tempList, false);
        setListAdapter(contactAdapter);
        return true;
    }

}