package tellit.com.tellit.ui.fragments.contacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.packet.IQ;

import java.util.Date;

import tellit.com.tellit.R;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIQ;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;


/**
 * Created by probojnik on 24.06.15.
 */
public class ContactReviewText extends BaseFragment {
    EditText editText;
    private Button next;
    private ReviewData reviewData;

    public static ContactReviewText newInstance(ReviewData reviewData, String userName){
        ContactReviewText fragment = new ContactReviewText();
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", reviewData);
        bundle.putString("userName", userName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.im_review_text,container,false);
        ((TextView) rootView.findViewById(R.id.im_review_comment)).setText(getString(R.string.review_comment, getArguments().getString("userName")));
        editText = (EditText) rootView.findViewById(R.id.im_review_text_edit);
        editText.setSingleLine(true);
        editText.setLines(10);
        editText.setHorizontallyScrolling(false);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    next();
                    return true;
                }
                return false;
            }
        });


        next = (Button) rootView.findViewById(R.id.im_review_text_next_btn);

        if(getArguments()!=null) {
            reviewData  = (ReviewData) getArguments().getSerializable("params");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   next();
                }
            });
        }
        return  rootView;
    }

    private void next(){
        ContactReviewFinish contactReviewFinish = new ContactReviewFinish();
        reviewData.setMsg(editText.getText().toString());
        reviewData.setCreateDate(new Date());
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", reviewData);
        contactReviewFinish.setArguments(bundle);
        getContainer().addFragment(contactReviewFinish, true);
    }



}
