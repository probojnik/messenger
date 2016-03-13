package tellit.com.tellit.controller.operations;

import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListAllReq;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListResp;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewListResp;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewsAllRoster;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;

/**
 * Created by probojnik on 11.08.15.
 */
public class SyncReview extends BaseOperation {
    Dao likeDao;
    @Inject
    AbstractXMPPConnection connection;
    @Inject
    UserData userData;
    @Inject
    VCardModule vCard;

    public SyncReview(OperationCalback operationCalback) {
        super(operationCalback);
        Injector.inject(this);
    }

    public SyncReview(){
        this(null);

    }

    @Override
    public boolean equals(Object o) {
        return true;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if(params == null || params.length == 0){
            syncAllReview();
        }else{
            syncReview((List<String>) params[0]);
        }

        return null;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



    public void syncReview(List<String> jids) {
        if(jids.size()>0){
            reviewRequest(jids);
        }

    }
    public void syncAllReview() {

        List<String> jids = new ArrayList<>();

        try {
            List<ContactData> contactDataList = HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("isValid",true).query();
            for(ContactData contactData : contactDataList){
                jids.add(contactData.getJid());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        jids.add(userData.getMyJid());
        reviewRequest(jids);
    }

    public void reviewRequest( List<String> jids){
        ReviewsAllRoster allRoster = new ReviewsAllRoster(jids);
        Log.d("SincOperation", "sync review");
        CustomStanzaController.getInstance().sendStanza(allRoster, new CustomStanzaCallback<ReviewListResp>() {

            @Override
            public void resultOK(ReviewListResp result) {
                List<ReviewData> reviewList = result.getReviewDataList();
                for (ReviewData reviewData : reviewList) {
                    try {
                        ReviewData findReview = (ReviewData) HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().where().eq("id",reviewData.getId()).queryForFirst();
                        if(findReview == null) {
                            HelperFactory.getInstans().getDao(ReviewData.class).create(reviewData);
                        }else{
                            findReview.update(reviewData);
                            HelperFactory.getInstans().getDao(ReviewData.class).update(findReview);
                        }
                        vCard.getContactByJid(reviewData.getFromJID(), new VCardModule.ContactDataCallback() {
                            @Override
                            public void result(ContactData contactData) {

                            }
                        });
                        vCard.getContactByJid(reviewData.getToJID(), new VCardModule.ContactDataCallback() {
                            @Override
                            public void result(ContactData contactData) {

                            }
                        });



                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                syncLikes();

            }

            @Override
            public void error(Exception ex) {

            }
        });
    }

    public void syncLikes(){
        likeDao = HelperFactory.getInstans().getDao(LikeData.class);
        Log.d("SincOperation", "sync like");
        List<ContactData> contactDataList = null;
        if(connection == null || !connection.isConnected()) return;
        try {
            contactDataList = HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().selectColumns("jid").where().eq("isValid",true).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> jids = new ArrayList<>();
        if(contactDataList!=null){
            for(ContactData contactData :contactDataList){
                jids.add(contactData.getJid());
            }
        }
        jids.add(userData.getMyJid());

        FeedbackListAllReq req = new FeedbackListAllReq(jids);
        CustomStanzaController.getInstance().sendStanza(req, new CustomStanzaCallback<FeedbackListResp>() {
            @Override
            public void resultOK(FeedbackListResp result) {
                for(LikeData likeData :result.getLikeDataList()){
                    try {
                        LikeData find_like = (LikeData) likeDao.queryBuilder().where().eq("id",likeData.getId()).queryForFirst();
                        if(find_like == null) {
                            likeDao.create(likeData);
                        }else{
                            find_like.update(likeData);
                            likeDao.update(find_like);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if(operationCalback!=null)
                    operationCalback.onComplete();
                EventBus.getDefault().post(new SincOperationResult(true));
            }

            @Override
            public void error(Exception ex) {
                if(operationCalback!=null)
                    operationCalback.onComplete();
            }
        });
    }

    public static final class SincOperationResult {
        boolean result;

        public SincOperationResult(boolean result) {
            this.result = result;
        }

        public boolean isResult() {
            return result;
        }
    }
}
