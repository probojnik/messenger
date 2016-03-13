package tellit.com.tellit.ui.fragments.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.model.IChatMessage;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.history.ChatIQReq;
import tellit.com.tellit.model.custom_xmpp.requests.history.ChatResp;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.tools.CollectionUtil;
import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;

/**
 * Created by Stas on 12.08.2015.
 */
public class ChatListMain extends ChatList {
    @Inject
    UserData userData;
    @Inject
    VCardModule vCardModule;
    private boolean firstStart = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(firstStart) {
            firstStart = false;
            upArchive();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onRefresh() {
        upArchive();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chatlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        TraceHelper.print();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_plus:
                ((BaseContainerFragment) getParentFragment()).replaceFragment(new NewMessageFragment(), true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void openSimpleChat(int i) {
        ((BaseContainerFragment) getParentFragment()).replaceFragment(SimpleChat.getInstance(chatAdapter.getItem(i).getJid()), true);

        // EventBus.getDefault().post(new ReplNavigation(SimpleChat.class,R.id.main_content,chatAdapter.getItem(i).getJid()));
//                bus.post(new NavBuild(SimpleChat.class).param(new NavigationParams().setParam(chatAdapter.getItem(i).getJid())).build());
    }

    private void upArchive() {
        CustomStanzaController.getInstance().sendStanza(new ChatIQReq(userData.getMyJid()),
                new CustomStanzaCallback<ChatResp>() {
                    @Override
                    public void resultOK(ChatResp result) {
//                        TraceHelper.print(result);
                        /**
                         * Получает историю контактов в виде списка, с которыми были беседы.
                         * Перебирает список.
                         */
                        for (final ChatData chatData : result.getList()) {
                            vCardModule.getContactByJid(chatData.getJid(), new VCardModule.ContactDataCallback() {
                                @Override
                                public void result(ContactData contactData) {
                                    if(contactData.isValid()){
                                        chatData.setName(contactData.getName());

                                        if (replace(chatData, ChatData.class) > 0) {
                                            updateUI();

                                            SimpleChat.execMessages(null, chatData, 0);
                                        }
                                    }else{
                                        vCardModule.requestUnknownUser(chatData.getJid(), 0, new VCardModule.ContactDataCallback() {
                                            @Override
                                            public void result(ContactData contactData) {
                                                chatData.setName(contactData.getNumber());

                                                if (replace(chatData, ChatData.class) > 0) {
                                                    updateUI();

                                                    SimpleChat.execMessages(null, chatData, 0);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
//                            vCardModule.getNameByJid(chatData.getJid(), new VCardModule.NameCallback() {
//                                @Override
//                                public void result(String name) {
//                                    if (!TextUtil.isEmpty(name)) {
//                                        chatData.setName(name);
//
//                                        if (replace(chatData, ChatData.class) > 0) {
//                                            updateUI();
//
//                                            SimpleChat.execMessages(null, chatData, 0);
//                                        }
//                                    }
//                                }
//                            });
                        }
                    }

                    @Override
                    public void error(Exception ex) {
                        TraceHelper.print(ex);
                    }
                });
        setRefreshing(false);
    }

    protected void updateUI() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                updateData();
            }
        };
        getActivity().runOnUiThread(run);
    }

    public static <T extends IChatMessage> int replace(IChatMessage bean, Class<T> clazz) {
        Dao dao = HelperFactory.getInstans().getDao(clazz);
        int result = -1;
        try {
            Where where = dao.queryBuilder().limit((long) 1).where();
            if (bean instanceof ChatData) {
                where.eq("jid", bean.getJid());
            } else {
                where.eq("jid", bean.getJid()).and().eq("createDate", bean.getDate());
            }
            List<T> list = where.query();

            if (CollectionUtil.isEmpty(list)) {
                result = dao.create(bean);
            } else {
                bean.set_id(list.get(0).get_id());
                result = dao.update(bean);
            }
            TraceHelper.print(list.size() > 0 ? "update" : "create", result, bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}