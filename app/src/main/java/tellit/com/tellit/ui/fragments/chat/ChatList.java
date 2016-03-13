package tellit.com.tellit.ui.fragments.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.ChatController;
import tellit.com.tellit.controller.operations.profile.SendVCardOperation;
import tellit.com.tellit.model.IChatMessage;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.tools.RoundedTransformationForPicasso;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.tools.log.TraceHelper;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.activitys.autorithation.Profile;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;
import tellit.com.tellit.ui.fragments.SwipeRefreshListFragment;


/**
 * Created by probojnik on 12.06.15.
 */
public abstract class ChatList extends SwipeRefreshListFragment {
    protected ChatAdapter chatAdapter;
    private LayoutInflater inflater;
    @Inject
    VCardModule vcard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        Injector.inject(this);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatAdapter = new ChatAdapter();
        setListAdapter(chatAdapter);
        updateData();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        openSimpleChat(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isVisible())
        ((BaseActivity)getActivity()).getSupportActionBar().setTitle("Conversations");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    protected abstract void openSimpleChat(int i);

    protected void updateData() {
        chatAdapter.clear();
        chatAdapter.addAll(ChatController.getInstance().getChatDataList());
        chatAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(List<ChatData> chatDataList) {
        updateData();
    }
    public void onEventMainThread(ContactData contactData) {
        updateData();
    }

    public void onEventMainThread(SendVCardOperation.SendVCardOperationResult result) {
        if (result.isResult()) {
            updateData();
        }

    }

    class ChatAdapter extends ArrayAdapter<ChatData> {
        public ChatAdapter() {
            super(getActivity(), R.layout.im_chat_list_item);
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if(view == null) view = inflater.inflate(R.layout.im_chat_list_item, viewGroup,false);
            final TextView name = (TextView) view.findViewById(R.id.im_chat_list_item_name);
            TextView last_mess = (TextView) view.findViewById(R.id.im_chat_list_item_last_mess);
            TextView time = (TextView) view.findViewById(R.id.im_chat_list_item_time);
            final TextView num = (TextView) view.findViewById(R.id.chat_list_item_review_num);
            TextView count_txt = (TextView) view.findViewById(R.id.im_chat_list_item_loss_count);
            final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            final ImageView photo = (ImageView) view.findViewById(R.id.chat_list_item_photo);
            long count = getItem(i).getCount();

            if(count>0){
                count_txt.setVisibility(View.VISIBLE);
                count_txt.setText("" + count);
            }else{
                count_txt.setVisibility(View.GONE);
                count_txt.setText("" + 0);
            }

            vcard.getContactByJid(getItem(i).getJid(), new VCardModule.ContactDataCallback() {
                @Override
                public void result(ContactData contactData) {
                    if(contactData!=null){
                        ratingBar.setRating(contactData.getRate());
                        num.setText(String.valueOf(contactData.getReviewNumber()));
                    }

                    name.setText(getItem(i).getName());
                    if(contactData.getPhoto_uri()!=null && contactData.getPhoto_uri().length()>0){
                        Picasso.with(getActivity())
                                .load(Uri.parse((contactData.getPhoto_uri())))
                                .fit().centerCrop()
                                .transform(new RoundedTransformationForPicasso(500, 0))
                                .into(photo);
                    }else{
                        Picasso.with(getActivity())
                                .load(R.mipmap.ic_launcher)
                                .fit().centerCrop()
                                .transform(new RoundedTransformationForPicasso(500, 0))
                                .into(photo);
                    }
                }
            });

            String mess = getItem(i).getLastMessage();
            mess = mess == null ? "" : mess;
            last_mess.setText(mess);
            Date last_date = getItem(i).getLast_date();
            String date_txt="";
            if(last_date!=null)
                date_txt = U.myDateFormat(getActivity(), last_date.getTime());
            time.setText(date_txt);
            return view;
        }
    }
}