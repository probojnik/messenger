package tellit.com.tellit.ui.fragments.review;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;
import tellit.com.tellit.ui.fragments.chat.ReviewChat;
import tellit.com.tellit.ui.fragments.contacts.ContactReviewRate;

/**
 * Created by probojnik on 28.06.15.
 */
public class ContactsReviewList extends BaseFragment {
    ListView listView ;
    ReviewAdapter adapter;
    LayoutInflater inflater;
    ContactData contactData;
    @Inject
    UserData userData;
    @Inject
    VCardModule vCard;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView( inflater,  container, savedInstanceState);
        Injector.inject(this);
        this.inflater = inflater;

        View rootView = inflater.inflate(R.layout.im_review_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.im_review_list_list);
        adapter = new ReviewAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ReviewData reviewData = adapter.getItem(i);
                String myJid = userData.getMyJid();
                if(reviewData.getFromJID().equals(myJid)){
                    ReviewChat reviewChat = new ReviewChat();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("params", reviewData);
                    reviewChat.setArguments(bundle);
                    ((BaseContainerFragment)getParentFragment()).replaceFragment(reviewChat, true);
                    return;
                }
                for(LikeData likeData :reviewData.getLikeList()){
                    if(likeData.getFromJID().equals(myJid)){
                        ReviewChat reviewChat = new ReviewChat();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("params", reviewData);
                        reviewChat.setArguments(bundle);
                        ((BaseContainerFragment)getParentFragment()).replaceFragment(reviewChat, true);

                        return;
                    }
                }

                LikeReview likeReview = new LikeReview();
                Bundle bundle = new Bundle();
                bundle.putSerializable("params", reviewData);
                likeReview.setArguments(bundle);
                ((BaseContainerFragment)getParentFragment()).replaceFragment(likeReview, true);


            }
        });


        if(getArguments()!=null) {
            contactData = (ContactData) getArguments().getSerializable("params");
            updateAllReview();



        }

        return rootView;
    }
    public void updateAllReview() {
        try {
            List<ReviewData> reviewDataList = HelperFactory.getInstans().getDao(ReviewData.class).queryForEq("toJID", contactData.getJid());

            adapter.clear();
            adapter.addAll(reviewDataList);
            adapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




//    public void onEventMainThread(ReviewAdd event) {
//        if(contactData != null && contactData.getJid().equals(event.getReviewData().getToJID())) {
//            adapter.add(event.getReviewData());
//            adapter.notifyDataSetChanged();
//        }
//    }
//    public void onEventMainThread(ReviewUpdate event) {
//        List<ReviewData> reviewDataList = null;
//        try {
//            reviewDataList = HelperFactory.getInstans().getDao(ReviewData.class).queryForEq("toJID", contactData.getJid());
//
//            adapter.clear();
//            adapter.addAll(reviewDataList);
//            adapter.notifyDataSetChanged();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    } todo


    class ReviewAdapter extends ArrayAdapter<ReviewData>{

        public ReviewAdapter(Context context) {
            super(context, R.layout.im_review_item_with_like);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.im_review_item_with_like,parent,false);
            }
            final TextView fio = (TextView) convertView.findViewById(R.id.im_review_list_item_fio);
            TextView rate = (TextView) convertView.findViewById(R.id.im_review_item_rate_txt);
            final RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.im_review_item_ratingBar);
            vCard.getContactByJid(getItem(position).getFromJID(), new VCardModule.ContactDataCallback() {
                @Override
                public void result(ContactData contactData) {
                    fio.setText(contactData.getName());
                    ratingBar.setRating(contactData.getRate());
                    if (userData.getMyJid().equals(getItem(position).getFromJID())) {
                        fio.setTextColor(Color.BLUE);
                    } else {
                        fio.setTextColor(Color.BLACK);
                    }
                }
            });

            rate.setText(""+getItem(position).getRate());

            return convertView;
        }
    }
}
