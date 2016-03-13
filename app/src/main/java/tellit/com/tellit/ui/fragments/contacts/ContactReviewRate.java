package tellit.com.tellit.ui.fragments.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import tellit.com.tellit.R;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;


/**
 * Created by probojnik on 24.06.15.
 */
public class ContactReviewRate extends BaseFragment {

    RatingBar ratingBar;
    private Button next_button;
    private TextView name;
    ReviewData reviewData;

    public static ContactReviewRate newInstance(ReviewData reviewData, String userName){
        ContactReviewRate contactReviewRate = new ContactReviewRate();
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", reviewData);
        bundle.putString("userName", userName);
        contactReviewRate.setArguments(bundle);
        return contactReviewRate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.im_review_rate, container, false);
        ratingBar = (RatingBar) rootView.findViewById(R.id.im_review_rate_rale);
        next_button = (Button) rootView.findViewById(R.id.im_review_rate_next_btn);
        name = (TextView) rootView.findViewById(R.id.im_review_rate_text);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num_star = (int) ratingBar.getRating();
                if (num_star < 1) {
                    Toast.makeText(getActivity(), "Set rating", Toast.LENGTH_SHORT).show();
                    return;
                }
                reviewData.setRate(num_star);

                getContainer().addFragment(ContactReviewText.newInstance(reviewData, getArguments().getString("userName")), true);
            }
        });

        reviewData = (ReviewData) getArguments().getSerializable("params");
        name.setText(getString(R.string.review_rate_name, getArguments().getString("userName")));

        return rootView;
    }


}
