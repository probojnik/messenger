package tellit.com.tellit.ui.fragments.review;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.ReviewController;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackIQ;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.ui.activitys.*;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;
import tellit.com.tellit.ui.fragments.chat.ReviewChat;


/**
 * Created by probojnik on 29.06.15.
 */
public class LikeReview extends BaseFragment {
    ProgressDialog pd;
    ReviewData reviewData;
    @Inject
    UserData userData;
    @Inject
    VCardModule vCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Injector.inject(this);
        View rootView = inflater.inflate(R.layout.im_like_review, container, false);
        final TextView fromName = (TextView) rootView.findViewById(R.id.im_like_from_name);
        final TextView toName = (TextView) rootView.findViewById(R.id.im_like_to_name);
        TextView likeCount = (TextView) rootView.findViewById(R.id.im_like_count_like);
        TextView dislikeCount = (TextView) rootView.findViewById(R.id.im_like_count_dislike);
        ImageView fromPhoto = (ImageView) rootView.findViewById(R.id.im_like_from_photo);
        ImageView toPhoto = (ImageView) rootView.findViewById(R.id.im_like_to_photo);
        TextView rate = (TextView) rootView.findViewById(R.id.im_like_rate);
        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.im_like_rating_bar);
        TextView reviewText = (TextView) rootView.findViewById(R.id.im_like_review_txt);

        final Button like = (Button) rootView.findViewById(R.id.im_like_like_btn);
        Button dislike = (Button) rootView.findViewById(R.id.im_like_dislike_btn);
        Button discuss_btn = (Button) rootView.findViewById(R.id.im_like_discuss_btn);



        if (getArguments() != null) {
            reviewData = (ReviewData) getArguments().getSerializable("params");
            int c_like=0,c_dislike=0;
            for(LikeData likeData :reviewData.getLikeList()){
                if(likeData.getVote() == 1)
                    c_like++;
                else
                    c_dislike++;
            }
            likeCount.setText(""+c_like);
            dislikeCount.setText("" + c_dislike);



            vCard.getNameByJid(reviewData.getFromJID(), new VCardModule.NameCallback() {
                @Override
                public void result(String name) {
                    if(LikeReview.this.isVisible())
                        ((BaseActivity)getActivity()).getSupportActionBar().setTitle("Review from " + name);
                    fromName.setText(name);

                }
            });
            vCard.getNameByJid(reviewData.getToJID(), new VCardModule.NameCallback() {
                @Override
                public void result(String name) {
                    toName.setText(name);

                }
            });


            rate.setText("" + reviewData.getRate());
            try {
                List<ContactData> contactDatas = HelperFactory.getInstans().getDao(ContactData.class).queryForEq("jid", reviewData.getToJID());
                if (contactDatas != null && contactDatas.size() > 0) {
                    ratingBar.setRating(contactDatas.get(0).getRate());
                }
            }catch (Exception ex){ex.printStackTrace();}
            reviewText.setText(reviewData.getMsg());

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   showDialog();

                    ReviewController.getInstance().like(reviewData, new CustomStanzaCallback<FeedbackIQ>() {

                        @Override
                        public void resultOK(FeedbackIQ result) {
                            hideDialog();
                            goToChat();
                        }

                        @Override
                        public void error(final Exception ex) {
                            showError(ex);

                        }
                    });


                }
            });

            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showDialog();
                    ReviewController.getInstance().dislike(reviewData, new CustomStanzaCallback<FeedbackIQ>() {

                        @Override
                        public void resultOK(FeedbackIQ result) {
                            hideDialog();
                            goToChat();
                        }

                        @Override
                        public void error(final Exception ex) {
                           showError(ex);
                        }
                    });



                }


            });
            discuss_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showDialog();
                    ReviewController.getInstance().deleteLike(reviewData, new CustomStanzaCallback<FeedbackIQ>() {

                        @Override
                        public void resultOK(FeedbackIQ result) {
                            hideDialog();
                            goToChat();
                        }

                        @Override
                        public void error(Exception ex) {
                            showError(ex);
                        }
                    });
                }
            });
        }
        return rootView;
    }

    private void showDialog(){
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Wait");
        pd.setCancelable(false);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd.show();
            }
        });
    }
    private void hideDialog(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
            }
        });
    }

    private void showError(final Exception ex){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToChat(){
        if(reviewData == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ReviewChat reviewChat = new ReviewChat();
                Bundle bundle = new Bundle();
                bundle.putSerializable("params", reviewData);
                reviewChat.setArguments(bundle);
                getContainer().popFragment(true);
                getContainer().replaceFragment(reviewChat, true);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

    }
}
