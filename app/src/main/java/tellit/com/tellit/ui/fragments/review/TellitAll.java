package tellit.com.tellit.ui.fragments.review;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.operations.profile.SendVCardOperation;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.fragments.BaseContainerFragment;
import tellit.com.tellit.ui.fragments.BaseFragment;


/**
 * Created by probojnik on 30.06.15.
 */
public class TellitAll extends BaseFragment implements ITellitList {
    protected static final long OFFSET = 10l;
    ListView listView;
    TellitAdapter adapter;
    LayoutInflater inflater;
    @Inject
    UserData userData;
    @Inject
    AbstractXMPPConnection connection;
    @Inject
    VCardModule vCard;
    protected SwipyRefreshLayout swipe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Injector.inject(this);
        this.inflater = inflater;
        EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.im_review_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.im_review_list_list);
        swipe = (SwipyRefreshLayout) rootView.findViewById(R.id.im_review_list_swipe_refresh_layout);
        swipe.setDirection(SwipyRefreshLayoutDirection.TOP);
        swipe.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                switch (swipyRefreshLayoutDirection){
                    case TOP:
                        ((Tellit)getActivity()).syncReviews();
                        swipe.setRefreshing(false);
                        break;
                }


            }
        });

        adapter = new TellitAdapter(this, getActivity());
        listView.setAdapter(adapter);

        updateReview();

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    public void onEventMainThread(ReviewData event) {
      updateReview();


    }
    public void onEventMainThread(LikeData event) {
         updateReview();
    }

    public void onEventMainThread(ContactData contactData) {
        updateReview();
    }

    public void onEventMainThread(SendVCardOperation.SendVCardOperationResult result) {
        if (result.isResult()) {
            updateReview();
        }

    }


    @Override
    public BaseContainerFragment getParent() {
        return getContainer();
    }

    public void updateReview() {
        try {
            final List<ReviewData> reviewDataList = HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().orderBy("id", false).query();

                    if (reviewDataList != null && reviewDataList.size() > 0) {
                        adapter.clear();
                        adapter.addAll(reviewDataList);
                        adapter.notifyDataSetChanged();
                    }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
