package tellit.com.tellit.ui.fragments.review;

import java.sql.SQLException;
import java.util.List;

import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.ReviewData;

/**
 * Created by probojnik on 26.07.15.
 */
public class TellitMe extends TellitAll {

    @Override
    public void updateReview() {
        try {
                    final List<ReviewData> reviewDataList = HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().orderBy("id", false).
                            where().eq("fromJID", userData.getMyJid()).query();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(reviewDataList!=null && reviewDataList.size()>0) {
                                adapter.clear();
                                adapter.addAll(reviewDataList);
                                adapter.notifyDataSetChanged();
                            }
                            swipe.setRefreshing(false);
                        }
                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                }
    }


}
