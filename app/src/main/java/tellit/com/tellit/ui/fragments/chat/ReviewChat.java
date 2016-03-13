package tellit.com.tellit.ui.fragments.chat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.MultiChatController;
import tellit.com.tellit.model.CreateDateObject;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.muc.MucHistoryReq;
import tellit.com.tellit.model.custom_xmpp.requests.muc.MucHistoryResp;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;
import tellit.com.tellit.ui.fragments.CABFragment;


/**
 * Created by probojnik on 06.07.15.
 *
 */
public class ReviewChat extends CABFragment{
    String jid;
    SwipyRefreshLayout mSwipeRefreshLayout;
    EditText editText;
    ListView listView;
    LayoutInflater inflater;
    ReviewAdapter messsageAdapter;
    TextView status_text,review_text;
    List<CreateDateObject> messageDatas = new ArrayList<>();
    ReviewData reviewData;
    RelativeLayout parentLayout;
    long offset = 0;

    @Inject
    VCardModule vCard;
    @Inject
    AbstractXMPPConnection connection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Injector.inject(this);
        EventBus.getDefault().register(this);

        View rootView = inflater.inflate(R.layout.im_simple_chat, container, false);
        parentLayout = (RelativeLayout) rootView.findViewById(R.id.parent_layout);
        listView = (ListView) rootView.findViewById(R.id.im_simple_chat_list);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);

        final ViewGroup reviewLt = (ViewGroup) rootView.findViewById(R.id.im_review);
        reviewLt.setVisibility(View.VISIBLE);

        editText = (EditText) rootView.findViewById(R.id.im_simple_chat_edt);
        editText.requestFocus();
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review_text.getLineCount() > 1) {
                    review_text.setSingleLine(true);
                }
            }
        });
        status_text = (TextView) rootView.findViewById(R.id.im_simple_chat_status_txt);
        review_text = (TextView) rootView.findViewById(R.id.im_review_text);
        review_text.setMovementMethod(new ScrollingMovementMethod());


        mSwipeRefreshLayout = (SwipyRefreshLayout) rootView.findViewById(R.id.im_simple_chat_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
        mSwipeRefreshLayout.setRefreshing(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {

                loadData();
//                mSwipeRefreshLayout.setRefreshing(false);
            }

        });

       listView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               U.hideKeyboard(getAct());
               return false;
           }
       });

        reviewLt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review_text.getLineCount() > 1) {
                    review_text.setSingleLine(true);
                }
                else {
                    review_text.setSingleLine(false);
                    U.hideKeyboard(getAct());

                }
            }
        });
        review_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(review_text.getLineCount()>1)
                    review_text.setSingleLine(true);
                else {
                    review_text.setSingleLine(false);
                    U.hideKeyboard(getAct());

                }
            }
        });

        status_text.setVisibility(View.GONE);
        if(getArguments()!=null) {
            reviewData = (ReviewData) getArguments().getSerializable("params");
            review_text.setText(reviewData.getMsg());
            if(review_text.getText().length() == 0) reviewLt.setVisibility(View.GONE);
            jid = MultiChatController.getInstance().getJidByID(""+reviewData.getId());

            List<LikeData> likes = null;
            try {
                likes = HelperFactory.getInstans().getDao(LikeData.class).queryBuilder().orderBy("createDate", true).where().eq("reviewId_id", reviewData).query();
                messageDatas.addAll(likes);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                MultiChatController.getInstance().createRoom(reviewData);

                Button btn = (Button) rootView.findViewById(R.id.im_simple_chat_btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mess = editText.getText().toString();
                        if (mess.length() == 0) return;
                        editText.setText("");
                        try {
                            MultiChatController.getInstance().sendMessage(reviewData, mess);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                } catch (Exception e) {
                Toast.makeText(getActivity(), "Can't join to chat", Toast.LENGTH_SHORT).show();
                getContainer().popFragment(true);
            }

        }

        messsageAdapter = new ReviewAdapter(getActivity(),messageDatas);
//        loadData();

        listView.setAdapter(messsageAdapter);
        scrollMyListViewToBottom();

        return rootView;
    }

    protected void loadData(){

        MucHistoryReq mucHistoryReq =null;
        for(int i = messageDatas.size()-1; i>=0 ; i--){
            if(messageDatas.get(i) instanceof MessageData){
                mucHistoryReq = new MucHistoryReq();
                mucHistoryReq.setNumber(10);
                mucHistoryReq.setRoom_name("" + reviewData.getId());
                mucHistoryReq.setMsg_uuid(((MessageData)messageDatas.get(i)).getId());
            }
        }

        if(mucHistoryReq!=null){
            CustomStanzaController.getInstance().sendStanza(mucHistoryReq, new CustomStanzaCallback<MucHistoryResp>() {
                @Override
                public void resultOK(final MucHistoryResp result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);

                        }
                    });
                    for(MucHistoryResp.Message mes:result.getMessageList()){
                        MessageData messageData =  new MessageData();
                        messageData.setBody(mes.getBody());
                        messageData.setId(mes.getMsgUUID());
                        messageData.setFromJid(mes.getNickName() + "@" + connection.getServiceName());
                        messageData.setLike(mes.getVote());
                        messageData.setCreateDate(mes.getDate());
                        if(!messageDatas.contains(messageData))
                            messageDatas.add(messageData);
                    }
                    Collections.sort(messageDatas, new Comparator<CreateDateObject>() {
                        @Override
                        public int compare(CreateDateObject lhs, CreateDateObject rhs) {
                            return (int) (lhs.getCreateDate().getTime() - rhs.getCreateDate().getTime());
                        }
                    });
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messsageAdapter.notifyDataSetChanged();
                            listView.setSelection(result.getMessageList().size() - 1);
                        }
                    });
                }

                @Override
                public void error(Exception ex) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);

                        }
                    });
                }
            });

        }else{
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        vCard.getNameByJid(reviewData.getToJID(), new VCardModule.NameCallback() {
            @Override
            public void result(String name) {
                if (ReviewChat.this.isVisible()) {
                    ((BaseActivity) getActivity()).getSupportActionBar().setTitle("About " + name);
                }

            }
        });
        ((Tellit)getActivity()).hideTabs();




    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_review_chat, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_choice_all:
                item.setChecked(true);
                messsageAdapter.setLike_mode(false);
                messsageAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_choice_like:
                item.setChecked(true);
                messsageAdapter.setLike_mode(true);
                messsageAdapter.notifyDataSetChanged();
                break;
            case android.R.id.home:
                U.hideKeyboard(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        MultiChatController.getInstance().leave(reviewData);
        ((Tellit)getActivity()).showTabs();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasOptionsMenu(false);
    }

    public void onEventMainThread(MessageData messageData) {
        if(messageData.getJid().equals(jid)) {
            CreateDateObject createDateObject = messageData;
            if(!messageDatas.contains(createDateObject)) {
                messageDatas.add(messageData);
                Collections.sort(messageDatas, new Comparator<CreateDateObject>() {
                    @Override
                    public int compare(CreateDateObject lhs, CreateDateObject rhs) {
                        return (int) (lhs.getCreateDate().getTime() - rhs.getCreateDate().getTime());
                    }
                });
                messsageAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }
        }
    }
    public void onEventMainThread(LikeData likeData) {
        if(reviewData.equals(likeData.getReviewId())) {
            if(!messageDatas.contains(likeData)) {
                messageDatas.add(likeData);
                Collections.sort(messageDatas, new Comparator<CreateDateObject>() {
                    @Override
                    public int compare(CreateDateObject lhs, CreateDateObject rhs) {
                        return (int) (lhs.getCreateDate().getTime() - rhs.getCreateDate().getTime());
                    }
                });
                messsageAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }
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


}
