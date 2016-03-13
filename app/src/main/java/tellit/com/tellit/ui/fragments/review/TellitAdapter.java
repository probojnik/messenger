package tellit.com.tellit.ui.fragments.review;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.ReviewController;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackIQ;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllFromReq;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllFromResp;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllToReq;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllToResp;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.tools.RoundedTransformationForPicasso;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.fragments.chat.ReviewChat;

/**
 * Created by probojnik on 18.08.15.
 */
public class TellitAdapter extends ArrayAdapter<ReviewData> {
    boolean b_from, b_to;
    private final Context context;
    private final ITellitList fragment;
    @Inject
    UserData userData;
    @Inject
    VCardModule vCard;

    public TellitAdapter(ITellitList fragment, Context context) {
        super(context, R.layout.im_tellit_item);
        Injector.inject(this);
        this.context = context;
        this.fragment = fragment;
    }

    //
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ReviewData reviewData = getItem(position);


        ReviewData find_review = null;

        ViewHolder viewHolder = null;
        final String myJid = userData.getMyJid();

        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(context);
            convertView = li.inflate(R.layout.im_tellit_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(R.layout.im_tellit_item, viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.layout.im_tellit_item);
        }

        final TextView from_fio = viewHolder.imReviewItemFromTxt;
        final TextView to_fio = viewHolder.imReviewItemToTxt;
        TextView text_review = viewHolder.tellitItemReviewText;
        final RatingBar from_rate = viewHolder.imReviewItemFromRate;
        final RatingBar to_rate = viewHolder.imReviewItemToRate;
        RatingBar ratingBar = viewHolder.imReviewItemRateRateBar;
        final ImageView image_to = viewHolder.imRaviewToPhoto;
        LinearLayout content_lt = viewHolder.tellitItemContentLt;
        final ToggleButton like_btn = viewHolder.tellitLikeBtn;
        final ToggleButton dis_like_btn = viewHolder.tellitDisLikeBtn;
        final ViewGroup tellitReviewFromLt = viewHolder.tellitReviewFromLt;
        text_review.setText(getItem(position).getMsg());

        like_btn.setChecked(false);
        dis_like_btn.setChecked(false);

        boolean block_like = false;
        if (reviewData.getFromJID().equals(myJid) || reviewData.getToJID().equals(myJid)) {
            block_like = true;
        }
        try {
            find_review = (ReviewData) HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().where().eq("id", reviewData.getId()).queryForFirst();
            if (find_review == null) {
                block_like = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            block_like = true;
        }
        for (LikeData likeData : reviewData.getLikeList()) {
            if (likeData.getFromJID().equals(myJid)) {
                switch (likeData.getVote()) {
                    case 1:
                        like_btn.setChecked(true);
                        dis_like_btn.setChecked(false);
                        break;
                    case -1:
                        like_btn.setChecked(false);
                        dis_like_btn.setChecked(true);
                        break;
                }
            }
        }

        String date_txt = "";
        if (getItem(position).getCreateDate() != null)
            date_txt = U.myDateFormat(userData.getContext(), getItem(position).getCreateDate().getTime());
        viewHolder.tellitReviewDate.setText(date_txt);

        int c_like = 0, c_dislike = 0;
        for (LikeData likeData : getItem(position).getLikeList()) {
            if (likeData.getVote() > 0)
                c_like++;
            else if(likeData.getVote() < 0)
                c_dislike++;
        }

        like_btn.setText("" + c_like);
        like_btn.setTextOff("" + c_like);
        like_btn.setTextOn("" + c_like);
        dis_like_btn.setText("" + c_dislike);
        dis_like_btn.setTextOff("" + c_dislike);
        dis_like_btn.setTextOn("" + c_dislike);


        final ViewHolder finalViewHolder = viewHolder;
        vCard.getContactByJid(getItem(position).getToJID(), new VCardModule.ContactDataCallback() {
            @Override
            public void result(final ContactData contactData) {
                to_rate.setRating(contactData.getRate());
                finalViewHolder.reviewItemToNum.setText("" + contactData.getReviewNumber());
                to_fio.setText("About " + contactData.getName());
                if (contactData.getPhoto_uri() != null && contactData.getPhoto_uri().length() > 0) {

                    Picasso.with(context)
                            .load(Uri.parse(contactData.getPhoto_uri()))
                            .fit().centerCrop()
                            .transform(new RoundedTransformationForPicasso(500, 0))
//                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(image_to);


                } else {
                    Picasso.with(context)
                            .load(R.mipmap.ic_launcher)
                            .fit().centerCrop()
                            .transform(new RoundedTransformationForPicasso(500, 0))
                            .into(image_to);
                }
                image_to.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUserTellit(contactData);
                    }
                });
                to_fio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUserTellit(contactData);
                    }
                });
                to_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUserTellit(contactData);
                    }
                });
            }
        });

        vCard.getContactByJid(getItem(position).getFromJID(), new VCardModule.ContactDataCallback() {
            @Override
            public void result(final ContactData contactData) {
                from_rate.setRating(contactData.getRate());
                finalViewHolder.reviewItemFromNum.setText("" + contactData.getReviewNumber());
                from_fio.setText("From " + contactData.getName());

                tellitReviewFromLt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUserTellit(contactData);
                    }
                });

            }
        });


        ratingBar.setRating(getItem(position).getRate());

        if (userData.getMyJid().equals(getItem(position).getToJID())) {
            to_fio.setTextColor(Color.BLUE);
        } else {
            to_fio.setTextColor(Color.BLACK);
        }

        if (block_like) {
            like_btn.setEnabled(false);
            dis_like_btn.setEnabled(false);
        } else {
            like_btn.setEnabled(true);
            dis_like_btn.setEnabled(true);
            final ReviewData finalFind_review = find_review;
            like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (like_btn.isChecked()) {
                        dis_like_btn.setChecked(false);
                        ReviewController.getInstance().like(finalFind_review, new CustomStanzaCallback<FeedbackIQ>() {
                            @Override
                            public void resultOK(FeedbackIQ result) {
                                updateFragment(finalFind_review);
                            }

                            @Override
                            public void error(final Exception ex) {
                                showError(ex);
                            }
                        });
                    } else {
                        if (!dis_like_btn.isChecked()) {
                            ReviewController.getInstance().deleteLike(finalFind_review, new CustomStanzaCallback<FeedbackIQ>() {
                                @Override
                                public void resultOK(FeedbackIQ result) {
                                    updateFragment(finalFind_review);
                                }

                                @Override
                                public void error(Exception ex) {
                                    showError(ex);
                                }
                            });
                        }
                    }
                }
            });
            dis_like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dis_like_btn.isChecked()) {
                        like_btn.setChecked(false);
                        ReviewController.getInstance().dislike(finalFind_review, new CustomStanzaCallback<FeedbackIQ>() {
                            @Override
                            public void resultOK(FeedbackIQ result) {
                                updateFragment(finalFind_review);
                            }

                            @Override
                            public void error(final Exception ex) {
                                showError(ex);
                            }
                        });
                    } else {
                        if (!like_btn.isChecked()) {
                            ReviewController.getInstance().deleteLike(finalFind_review, new CustomStanzaCallback<FeedbackIQ>() {
                                @Override
                                public void resultOK(FeedbackIQ result) {
                                    updateFragment(finalFind_review);
                                }

                                @Override
                                public void error(Exception ex) {
                                    showError(ex);
                                }
                            });
                        }
                    }
                }
            });
        }
        viewHolder.tellit_likes_group.removeAllViews();
        viewHolder.tellit_likes_group.addView(getLikes(getItem(position)));


        final ReviewData finalFind_review2 = find_review;
        final boolean finalHave_like = block_like;
        content_lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalFind_review2 != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("params", finalFind_review2);
                    if (like_btn.isChecked() || dis_like_btn.isChecked() || finalHave_like) {
                        ReviewChat reviewChat = new ReviewChat();
                        reviewChat.setArguments(bundle);
                        fragment.getParent().addFragment(reviewChat, true);
                    } else {
                        LikeReview likeReview = new LikeReview();
                        likeReview.setArguments(bundle);
                        fragment.getParent().addFragment(likeReview, true);
                    }
                }
            }
        });

        return convertView;
    }

    private LinearLayout getLikes(ReviewData reviewData){
        final LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(LLParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final TextView like_txt = new TextView(getContext());
        final TextView dislike_txt = new TextView(getContext());

        like_txt.setMinLines(2);
        dislike_txt.setMinLines(2);

        if(reviewData.getLikeList().size() == 0)
            linearLayout.setVisibility(View.GONE);



        final Set<String> likeNames = new HashSet<>();
        final Set<String> dislikeNames = new HashSet<>();

        final SpannableStringBuilder sb_l = new SpannableStringBuilder();
        final SpannableStringBuilder sb_d = new SpannableStringBuilder();

        for (final LikeData likeData : reviewData.getLikeList()) {
            if (likeNames.contains(likeData.getFromJID()) || dislikeNames.contains(likeData.getFromJID())) continue;

            vCard.getContactByJid(likeData.getFromJID(), new VCardModule.ContactDataCallback() {

                @Override
                public void result(final ContactData contactData) {
                    if (likeData.getVote() == 1 && likeNames.size() < 10) {
                        sb_l.append(contactData.getName() + " ");
                        sb_l.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                showUserTellit(contactData);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.linkColor = Color.parseColor("#7CBF6B");
                                super.updateDrawState(ds);

                            }
                        }, sb_l.length() - (contactData.getName().length() + 1), sb_l.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (likeData.getVote() == -1 && dislikeNames.size() < 10) {
                        sb_d.append(contactData.getName() + " ");
                        sb_d.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                showUserTellit(contactData);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.linkColor = Color.parseColor("#C24431");
                                super.updateDrawState(ds);

                            }
                        }, sb_d.length() - (contactData.getName().length() + 1), sb_d.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (sb_l.length() > 0) {
                        like_txt.setText(sb_l);

                    }
                    if (sb_d.length() > 0) {
                        dislike_txt.setText(sb_d);

                    }


                }
            });
        }

        like_txt.setMovementMethod(LinkMovementMethod.getInstance());
        dislike_txt.setMovementMethod(LinkMovementMethod.getInstance());

        linearLayout.addView(like_txt);
        linearLayout.addView(dislike_txt);

        return linearLayout;
    }

    private void updateFragment(final ReviewData reviewData) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (fragment instanceof TellitFromUser) {
                    EventBus.getDefault().post(reviewData);
                } else {
                    fragment.updateReview();

                }
            }
        });
    }


    private void showUserTellit(final ContactData contactData) {
        b_from = false;
        b_to = false;
        ((Tellit) context).showProgress();
        final Bundle bundle = new Bundle();
        bundle.putString("contact_name", contactData.getName());
        bundle.putString("jid", contactData.getJid());

        ReviewIdAllFromReq reviewIdAllFromReq = new ReviewIdAllFromReq();
        reviewIdAllFromReq.setListJids(contactData.getJid());
        CustomStanzaController.getInstance().sendStanza(reviewIdAllFromReq, new CustomStanzaCallback<ReviewIdAllFromResp>() {
            @Override
            public void resultOK(final ReviewIdAllFromResp result) {
                b_from = true;
                bundle.putLongArray("params_from", result.getId_list());
                if (b_to == true) {
                    goToNextFragment(bundle);
                }
            }

            @Override
            public void error(final Exception ex) {
                showError(ex);
            }
        });

        ReviewIdAllToReq reviewIdAllToReq = new ReviewIdAllToReq();
        reviewIdAllToReq.setListJids(contactData.getJid());
        CustomStanzaController.getInstance().sendStanza(reviewIdAllToReq, new CustomStanzaCallback<ReviewIdAllToResp>() {
            @Override
            public void resultOK(final ReviewIdAllToResp result) {
                b_to = true;
                bundle.putLongArray("params_to", result.getId_list());
                if (b_from == true) {
                    goToNextFragment(bundle);
                }
            }

            @Override
            public void error(final Exception ex) {
                showError(ex);
            }
        });
    }

    private void goToNextFragment(final Bundle bundle) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ((Tellit) context).hideProgress();
                TellitTabsAboutUser tellitTabsAboutUser = new TellitTabsAboutUser();
                tellitTabsAboutUser.setArguments(bundle);
                fragment.getParent().addFragment(tellitTabsAboutUser, true);
            }
        });
    }

    private void showError(final Exception ex) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ((Tellit) context).hideProgress();
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    static

            /**
             * This class contains all butterknife-injected Views & Layouts from layout file 'im_tellit_item.xml'
             * for easy to all layout elements.
             *
             * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
             */


    class ViewHolder {
        @InjectView(R.id.review_item_to_num)
        TextView reviewItemToNum;
        @InjectView(R.id.review_item_from_num)
        TextView reviewItemFromNum;
        @InjectView(R.id.im_raview_to_photo)
        ImageView imRaviewToPhoto;
        @InjectView(R.id.im_review_item_to_txt)
        TextView imReviewItemToTxt;
        @InjectView(R.id.im_review_item_to_rate)
        RatingBar imReviewItemToRate;
        @InjectView(R.id.tellit_review_date)
        TextView tellitReviewDate;
        @InjectView(R.id.im_review_item_rate_rate_bar)
        RatingBar imReviewItemRateRateBar;
        @InjectView(R.id.tellit_item_review_text)
        TextView tellitItemReviewText;
        @InjectView(R.id.im_review_item_from_txt)
        TextView imReviewItemFromTxt;
        @InjectView(R.id.im_review_item_from_rate)
        RatingBar imReviewItemFromRate;
        @InjectView(R.id.tellit_review_from_lt)
        LinearLayout tellitReviewFromLt;
        @InjectView(R.id.tellit_item_content_lt)
        LinearLayout tellitItemContentLt;
        @InjectView(R.id.tellit_like_btn)
        ToggleButton tellitLikeBtn;
        @InjectView(R.id.tellit_dis_like_btn)
        ToggleButton tellitDisLikeBtn;
        @InjectView(R.id.tellit_like_btns_lt)
        LinearLayout tellitLikeBtnsLt;
        @InjectView(R.id.tellit_likes_group)
        LinearLayout tellit_likes_group;


        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }



}
