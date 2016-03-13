package tellit.com.tellit.ui.fragments.contacts;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.packet.IQ;

import java.sql.SQLException;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tellit.com.tellit.R;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIQ;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.ui.fragments.BaseFragment;


/**
 * Created by probojnik on 24.06.15.
 */
public class ContactReviewFinish extends BaseFragment {
    @InjectView(R.id.im_review_finish_rating)
    RatingBar imReviewFinishRating;
    @InjectView(R.id.im_review_finish_text)
    TextView imReviewFinishText;
    @InjectView(R.id.review_finish_chb)
    CheckBox reviewFinishChb;
    @InjectView(R.id.im_review_finish_btn)
    Button imReviewFinishBtn;

    ReviewData reviewData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.im_review_finish, container, false);
        ButterKnife.inject(this, rootView);

        reviewData = (ReviewData) getArguments().getSerializable("params");

        imReviewFinishText.setText(reviewData.getMsg());
        imReviewFinishRating.setRating(reviewData.getRate());

        if (reviewData.getContacID() != null && reviewData.getContacID().isInstalls()) {
            reviewFinishChb.setChecked(false);
            reviewFinishChb.setVisibility(View.GONE);
        }

        imReviewFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewIQ reviewIQ = new ReviewIQ();
                reviewIQ.setState(reviewData.getState());
                reviewIQ.setJid(reviewData.getToJID());
                reviewIQ.setRate(reviewData.getRate());
                reviewIQ.setMessage(reviewData.getMsg());
                reviewIQ.setType(IQ.Type.set);

                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Wait");
                dialog.setCancelable(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return false;
                    }


                });
                dialog.show();

                CustomStanzaController.getInstance().sendStanza(reviewIQ, new CustomStanzaCallback<ReviewIQ>() {
                    @Override
                    public void resultOK(final ReviewIQ result) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                reviewData.setId(result.getReviewid());
                                reviewData.setCreateDate(new Date());
                                try {
                                    HelperFactory.getInstans().getDao(ReviewData.class).create(reviewData);
                                    if (reviewFinishChb.isChecked()) {
                                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                        i.putExtra("address", reviewData.getContacID().getNumber());
                                        i.putExtra("sms_body", "Hi! I left a review about you. Install tellit app to view www.google.com");
                                        i.setType("vnd.android-dir/mms-sms");
                                        try {
                                            startActivity(i);
                                        } catch (ActivityNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    getContainer().popFragment(false);
                                    getContainer().popFragment(false);
                                    getContainer().popFragment(true);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void error(final Exception ex) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

            }
        });


        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
