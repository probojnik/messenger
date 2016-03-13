package tellit.com.tellit.ui.fragments.chat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import org.jivesoftware.smackx.chatstates.ChatState;

import java.sql.SQLException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.ChatController;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.history.MessageIQReq;
import tellit.com.tellit.model.custom_xmpp.requests.history.MessageResp;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.fragments.CABFragment;

/**
 * Created by probojnik on 10.06.15.
 */
public class SimpleChat extends CABFragment {
    String jid;
    SwipyRefreshLayout mSwipeRefreshLayout;
    EditText editText;
    ListView listView;
    LayoutInflater inflater;
    ChatAdapter messsageAdapter;
    TextView status_text;
    List<MessageData> messageDatas = new ArrayList<>();
    private int page = 0;
    AsyncTask composingTask;
    @Inject
    VCardModule vCard;

    public static SimpleChat getInstance(String jid) {
        SimpleChat simpleChat = new SimpleChat();
        Bundle bundle = new Bundle();
        bundle.putString("params", jid);
        simpleChat.setArguments(bundle);
        return simpleChat;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public ListView getListView() {
        return listView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);

        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.im_simple_chat, container, false);
        listView = (ListView) rootView.findViewById(R.id.im_simple_chat_list);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                U.hideKeyboard(getAct());
                return false;
            }
        });

        editText = (EditText) rootView.findViewById(R.id.im_simple_chat_edt);
        editText.requestFocus();
        status_text = (TextView) rootView.findViewById(R.id.im_simple_chat_status_txt);
        mSwipeRefreshLayout = (SwipyRefreshLayout) rootView.findViewById(R.id.im_simple_chat_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        status_text.setVisibility(View.GONE);
        ViewGroup reviewLt = (ViewGroup) rootView.findViewById(R.id.im_review);
        reviewLt.setVisibility(View.GONE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            jid = bundle.getString("params");

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() % 3 == 0) {
                        sendCompising();
                    }
                }
            });
//
            messsageAdapter = new ChatAdapter(getActivity(), messageDatas);
//            execMessages(this, jid, 0, null);
            new LoadData(this, jid).execute(); // TODO: temporarily

            listView.setAdapter(messsageAdapter);
            scrollMyListViewToBottom();
            mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
//                    execMessages(SimpleChat.this, jid, ++page, null);
                    mSwipeRefreshLayout.setRefreshing(false); // TODO: temporarily
                }
            });

            Button btn = (Button) rootView.findViewById(R.id.im_simple_chat_btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mess = editText.getText().toString();
                    if (mess.length() == 0) return;
                    editText.setText("");
                    ChatController.getInstance().sendMessage(mess, jid);

                }
            });
            ChatController.getInstance().all_viewed(jid);
            ChatController.getInstance().sendState(ChatState.active, jid);
//            ChatController.getInstance().sendReaded(jid); //todo
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        vCard.getNameByJid(jid, new VCardModule.NameCallback() {
            @Override
            public void result(String name) {
                if (getActivity() != null && ((BaseActivity) getActivity()).getSupportActionBar() != null && SimpleChat.this.isVisible())
                    ((BaseActivity) getActivity()).getSupportActionBar().setTitle(name);
            }
        });

        ((Tellit) getActivity()).hideTabs();
        ((Tellit) getActivity()).lockDrawer(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((Tellit) getActivity()).showTabs();
        ((Tellit) getActivity()).lockDrawer(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                U.hideKeyboard(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendCompising() {
        if (composingTask == null) {
            composingTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    ChatController.getInstance().sendState(ChatState.composing, jid); //todo
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    ChatController.getInstance().sendState(ChatState.paused, jid);
                }
            }.execute();
        } else {
            if (composingTask.getStatus() == AsyncTask.Status.FINISHED) {
                composingTask = null;
                sendCompising();
            } else {
                return;
            }
        }
    }

    /**
     * @param act  - null если не обновлять ListView
     * @param chatData  - jid и sessionHistory
     * @param page - 0 или больше для загрузки с учетом смещения
     */
    public static void execMessages(@Nullable final SimpleChat act, @NonNull final ChatData chatData, int page) {
        TraceHelper.print(chatData.getJid(), chatData.getHistorySessions());
        for(String time: chatData.getHistorySessions()){
            MessageIQReq request = new MessageIQReq(chatData.getJid(), time);
            int length = 20;
            request.setLength(length);
            request.setOffset(page * length);

            CustomStanzaController.getInstance().sendStanza(request,
                    new CustomStanzaCallback<MessageResp>() {
                        @Override
                        public void resultOK(MessageResp result) {
                            long messageCreateDate = -1;
                            for (MessageData messageData : result.getList()) {
                                if (messageData.getCreateDate().getTime() > messageCreateDate)
                                    messageCreateDate = messageData.getCreateDate().getTime();

                                ChatListMain.replace(messageData, MessageData.class);
                            }

                            /**
                             * Проверяет условие необходимости обновить last_date чата
                             */
                            if (chatData != null && messageCreateDate > 0 &&
                                    (chatData.getLast_date() == null || messageCreateDate > chatData.getLast_date().getTime())) {
                                chatData.setLast_date(new Date(messageCreateDate));
                                ChatListMain.replace(chatData, ChatData.class);
                            }

                            /**
                             * Если вызывается из тпребуемого контекста, обновляет вид
                             */
                            if (act != null)
                                new LoadData(act, chatData.getJid()).execute();
                        }

                        @Override
                        public void error(Exception ex) {
                            TraceHelper.print(ex);
                            if (act != null) {
                                act.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        act.mSwipeRefreshLayout.setRefreshing(false);

                                    }
                                });
                            }
                        }
                    });
        }
    }

    private static class LoadData extends AsyncTask<Void, Void, List<MessageData>> {
        private SimpleChat act;
        private String forJID;

        public LoadData(@NonNull SimpleChat act, @NonNull String forJID) {
            this.act = act;
            this.forJID = forJID;
        }

        @Override
        protected List<MessageData> doInBackground(Void... params) {
            List<MessageData> messages = null;
            try {
                messages = HelperFactory.getInstans().getDao(MessageData.class).queryBuilder().orderBy("createDate", false).where().eq("jid", forJID).query(); // .offset(offset * 20).limit(20l)

            } catch (SQLException e) {
                e.printStackTrace();
            }
            TraceHelper.print(messages);
            if (messages == null) return null;
            Collections.sort(messages, new Comparator<MessageData>() {
                @Override
                public int compare(MessageData t, MessageData t1) {
                    return t.getCreateDate().compareTo(t1.getCreateDate());
                }
            });
            return messages;
        }

        @Override
        protected void onPostExecute(@Nullable List<MessageData> messages) {
            super.onPostExecute(messages);
            if (messages != null) {
                act.messageDatas.clear();
                act.messageDatas.addAll(0, messages);
                act.messsageAdapter.notifyDataSetChanged();
//            act.listView.setSelection(messages.size() - 1);
            }
            act.mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    protected void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPosition(messsageAdapter.getCount() - 1);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    public void onEventMainThread(HashMap<String, ChatState> stateHashMap) {
        if (stateHashMap != null && stateHashMap.size() > 0 && stateHashMap.keySet().iterator().next().equals(jid)) {
            ChatState state = stateHashMap.get(jid);
            switch (state) {
                case composing:
                    status_text.setVisibility(View.VISIBLE);
                    String status = "";
                    vCard.getNameByJid(jid, new VCardModule.NameCallback() {
                        @Override
                        public void result(String name) {
                            status_text.setText(name + " composing");
                        }
                    });

                    break;
                default:
                    status_text.setText("");
                    status_text.setVisibility(View.GONE);

            }
        }

    }

    public void onEventMainThread(MessageData messageData) {

        if (messageDatas.contains(messageData)) {
            int ind = messageDatas.indexOf(messageData);
            MessageData updMessage = messageDatas.get(ind);
            updMessage.update(messageData);
            messsageAdapter.notifyDataSetChanged();
        } else {
            if (jid.equals(messageData.getJid())) {
                messageDatas.add(messageData);
                messsageAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }
        }
        ChatController.getInstance().all_viewed(jid);
    }

}