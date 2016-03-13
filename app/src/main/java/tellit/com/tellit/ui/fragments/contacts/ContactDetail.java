package tellit.com.tellit.ui.fragments.contacts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;


import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllFromReq;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllFromResp;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllToReq;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllToResp;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.tools.RoundedTransformationForPicasso;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;
import tellit.com.tellit.ui.fragments.chat.ReviewChat;
import tellit.com.tellit.ui.fragments.chat.SimpleChat;
import tellit.com.tellit.ui.fragments.review.ContactsReviewList;
import tellit.com.tellit.ui.fragments.review.TellitTabsAboutUser;

/**
 * Created by probojnik on 24.06.15.
 */
public class ContactDetail extends BaseFragment {
    @Inject
    AbstractXMPPConnection connection;
    @Inject
    VCardModule vCardModule;
    Button favor;
    private Button review_btn;
    private Button tellit_btn;
    private ImageView photo;
    private TextView phone;
    private RatingBar rating;
    private Button chat_btn;
    private String jid;
    ContactData contactData;
    private Button invite_btn;
    TextView num_review;

    public static ContactDetail getInstance(ContactData contactData){
        ContactDetail result = new ContactDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", contactData);
        result.setArguments(bundle);
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Injector.inject(this);

        View rootView =  inflater.inflate(R.layout.im_contact_detail,container,false);
        favor = (Button) rootView.findViewById(R.id.im_contact_detail_favor_btn);
        review_btn = (Button) rootView.findViewById(R.id.im_contact_detail_reviews_btn);
        chat_btn = (Button) rootView.findViewById(R.id.im_contact_detail_chat_btn);
        tellit_btn = (Button) rootView.findViewById(R.id.im_contact_detail_tellit_btn);
        invite_btn = (Button) rootView.findViewById(R.id.im_contact_detail_invite_btn);
        photo = (ImageView) rootView.findViewById(R.id.im_contact_detail_photo);
        phone = (TextView) rootView.findViewById(R.id.im_contact_detail_phone_txt);
        rating = (RatingBar) rootView.findViewById(R.id.im_contact_detail_rating);
        num_review = (TextView) rootView.findViewById(R.id.contact_detail_num_review);

        favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactData.isFavorite()) {
                    contactData.setFavorite(false);
                    Toast.makeText(getActivity(),"Removed from favorites",Toast.LENGTH_SHORT ).show();
                }
                else {
                    contactData.setFavorite(true);
                    Toast.makeText(getActivity(),"Added to favorites",Toast.LENGTH_SHORT ).show();
                }
                try {
                    HelperFactory.getInstans().getDao(ContactData.class).update(contactData);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseContainerFragment) getParentFragment()).replaceFragment(instanceSimpleChat(contactData), true);
            }
        });
        tellit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connection==null || !connection.isConnected()){
                    Toast.makeText(getActivity(),"No connection",Toast.LENGTH_SHORT).show();
                    return;
                }

                String from = connection.getUser(); //todo null pointer
                if(from != null){
                    int index = from.lastIndexOf("/");
                    if(index>0) {
                        from = from.substring(0, index);
                    }
                }

                if(vCardModule.isMyJid(contactData.getJid())) {
                    Toast.makeText(getActivity(), "You can't send review about yourself", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    List<ReviewData> list = HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().where().eq("fromJID",from).and().eq("toJID",contactData.getJid()).query();
                    if(list==null || list.size()>0){
                        Toast.makeText(getActivity(), "You have review for this contact", Toast.LENGTH_SHORT).show();

                        ReviewChat reviewChat = new ReviewChat();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("params", list.get(0));
                        reviewChat.setArguments(bundle);
                        ((BaseContainerFragment)getParentFragment()).replaceFragment(reviewChat, true);

                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ReviewData reviewData = new ReviewData();
                reviewData.setFromJID(from);
                reviewData.setToJID(contactData.getJid());
                reviewData.setState((byte) 1);
                reviewData.setContacID(contactData);

                ((BaseContainerFragment)getParentFragment()).replaceFragment(ContactReviewRate.newInstance(reviewData, contactData.getName()), true);
            }
        });

        review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                ContactsReviewList contactsReviewList = new ContactsReviewList();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("params", contactData);
//                contactsReviewList.setArguments(bundle);
//                ((BaseContainerFragment)getParentFragment()).replaceFragment(contactsReviewList, true);

                showUserTellit(contactData);

            }
        });

        if(getArguments()!=null) {
            contactData = (ContactData) getArguments().getSerializable("params");

            if(!contactData.isInstalls()){
                invite_btn.setVisibility(View.VISIBLE);
                invite_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                        i.putExtra("address", contactData.getNumber());
                        i.putExtra("sms_body", "Join us www.google.com");
                        i.setType("vnd.android-dir/mms-sms");
                        startActivity(i);
                    }
                });
            }

            try {
                phone.setText(contactData.getNumber());
                String photo_url = contactData.getPhoto_uri();
                if(photo_url!=null && photo_url.length()>0) {

                    Picasso.with(getActivity())
                            .load(Uri.parse(contactData.getPhoto_uri()))
                            .fit().centerCrop()
//                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .transform(new RoundedTransformationForPicasso(500, 0))
                            .into(photo);
                }else{
                    Picasso.with(getActivity())
                            .load(R.mipmap.ic_launcher)
                            .fit().centerCrop()
                            .transform(new RoundedTransformationForPicasso(500, 0))
                            .into(photo);
                }
            }catch (Exception ex){}
            rating.setRating(contactData.getRate());
            num_review.setText(""+contactData.getReviewNumber());
        }

        return  rootView;
    }


    private void showUserTellit(final ContactData contactData) {
        final boolean _b[] = new boolean[2];
        ((Tellit) getActivity()).showProgress();
        final Bundle bundle = new Bundle();
        bundle.putString("contact_name", contactData.getName());
        bundle.putString("jid", contactData.getJid());

        ReviewIdAllFromReq reviewIdAllFromReq = new ReviewIdAllFromReq();
        reviewIdAllFromReq.setListJids(contactData.getJid());
        CustomStanzaController.getInstance().sendStanza(reviewIdAllFromReq, new CustomStanzaCallback<ReviewIdAllFromResp>() {
            @Override
            public void resultOK(final ReviewIdAllFromResp result) {
                _b[0]  = true;
                bundle.putLongArray("params_from", result.getId_list());
                if (_b[1] == true) {
                    goToNextFragment(bundle);
                }
            }

            @Override
            public void error(final Exception ex) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ReviewIdAllToReq reviewIdAllToReq = new ReviewIdAllToReq();
        reviewIdAllToReq.setListJids(contactData.getJid());
        CustomStanzaController.getInstance().sendStanza(reviewIdAllToReq, new CustomStanzaCallback<ReviewIdAllToResp>() {
            @Override
            public void resultOK(final ReviewIdAllToResp result) {
                _b[1] = true;
                bundle.putLongArray("params_to", result.getId_list());
                if (_b[0] == true) {
                    goToNextFragment(bundle);
                }
            }

            @Override
            public void error(final Exception ex) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToNextFragment(final Bundle bundle) {
        new Handler(getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ((Tellit) getActivity()).hideProgress();
                TellitTabsAboutUser tellitTabsAboutUser = new TellitTabsAboutUser();
                tellitTabsAboutUser.setArguments(bundle);
                getContainer().replaceFragment(tellitTabsAboutUser, true);
            }
        });
    }

    public static SimpleChat instanceSimpleChat(ContactData contactData){
        ChatData chat = null;
        String jid = contactData.getJid();
        try {
            List<ChatData> chatDataList = HelperFactory.getInstans().getDao(ChatData.class).queryForEq("jid", jid);
            if (chatDataList != null && chatDataList.size() > 0) {
                chat = chatDataList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (chat == null) {
            chat = new ChatData();
            chat.setDate(new Date(System.currentTimeMillis()));
            chat.setJid(jid);
            chat.setName(contactData.getName());
            chat.setPhoto_uri(contactData.getPhoto_uri());
            try {
                HelperFactory.getInstans().getDao(ChatData.class).createIfNotExists(chat);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return  SimpleChat.getInstance(contactData.getJid());
    }

    @Override
    public void onResume() {
        super.onResume();
        String contact_name = contactData.getName();
        if(contact_name == null || contact_name.length() == 0)
            contact_name = contactData.getJid();
        if(isVisible())
            ((BaseActivity)getActivity()).getSupportActionBar().setTitle(contact_name);

    }
}
