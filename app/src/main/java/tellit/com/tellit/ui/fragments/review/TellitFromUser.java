package tellit.com.tellit.ui.fragments.review;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.R;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListByReviewIDReq;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListResp;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewListByIDReq;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewListResp;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;

/**
 * Created by probojnik on 07.08.15.
 */
public class TellitFromUser extends BaseFragment  implements ITellitList {
    protected String jid;
    ProgressBar progressBar;

    ListView listView;
    TellitAdapter adapter;
    protected SwipyRefreshLayout swipe;
    protected List<ReviewData> reviewDataList = new ArrayList<>();
    List<Long> l_ids = new ArrayList<>();

    final int LOAD_COUNT = 10;
    int page = 1;


    @Override
    public BaseContainerFragment getParent() {

        return getContainer();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.im_review_list,container,false);
        progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        listView = (ListView) rootView.findViewById(R.id.im_review_list_list);
        swipe = (SwipyRefreshLayout) rootView.findViewById(R.id.im_review_list_swipe_refresh_layout);
        swipe.setEnabled(false);
        adapter = new TellitAdapter(this, getActivity());
        listView.addFooterView(progressBar);
        listView.setAdapter(adapter);
        listView.removeFooterView(progressBar);




        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount == page * LOAD_COUNT) {
                    page++;
                    Log.d("TellitFromUser", "scrol");
                    loadData();
                }
            }
        });

        reviewDataList.clear();
        loadData();

        return rootView;
    }

    protected void loadData(){
        listView.addFooterView(progressBar);
        List<Long> req_ids = new ArrayList<>();
        for(int i=((page-1)*LOAD_COUNT); i < page*LOAD_COUNT && i < l_ids.size() ; i++){
            req_ids.add(l_ids.get(i));
        }
        if(req_ids.size() == 0 ){
            listView.removeFooterView(progressBar);
            return;
        }
        ReviewListByIDReq reviewListByIDReq = new ReviewListByIDReq();
        reviewListByIDReq.setIds(req_ids);
        CustomStanzaController.getInstance().sendStanza(reviewListByIDReq, new CustomStanzaCallback<ReviewListResp>() {


            @Override
            public void resultOK(ReviewListResp result) {
                List<ReviewData> reciveReview = result.getReviewDataList();
                for (final ReviewData reviewData : reciveReview) {
                    FeedbackListByReviewIDReq feedbackListByReviewIDReq = new FeedbackListByReviewIDReq(reviewData.getId());
                    CustomStanzaController.getInstance().sendStanza(feedbackListByReviewIDReq, new CustomStanzaCallback<FeedbackListResp>() {

                        @Override
                        public void resultOK(FeedbackListResp result) {
                            reviewData.setLikeList(result.getLikeDataList());

                            updateReview();
                        }

                        @Override
                        public void error(Exception ex) {

                        }
                    });
                }
                reviewDataList.addAll(reciveReview);
                Collections.sort(reviewDataList, new Comparator<ReviewData>() {
                    @Override
                    public int compare(ReviewData lhs, ReviewData rhs) {
                        return (int) (rhs.getId() - lhs.getId());
                    }
                });

                updateReview();
            }

            @Override
            public void error(final Exception ex) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.removeFooterView(progressBar);
                            Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    public void onEventMainThread(ReviewData event) {
       for(ReviewData reviewData : reviewDataList){
           if(reviewData.equals(event)){
               reviewData.setLikeList(event.getLikeList());
               updateReview();
           }
       }
    }

    @Override
    public void updateReview() {
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.removeFooterView(progressBar);
                    adapter.clear();
                    adapter.addAll(reviewDataList);
                    adapter.notifyDataSetChanged();
                }
            });
        }


    }


    public void setReviewId(long[] i_ids) {
        if(i_ids == null) return;
        l_ids = new ArrayList<>(i_ids.length);
        for(long l : i_ids){
            l_ids.add(l);
        }
        Collections.sort(l_ids, new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return (int) (rhs - lhs);
            }

        });
    }

    public void setJID(String JID) {
        this.jid = JID;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

    }
}
