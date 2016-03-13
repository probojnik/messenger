package tellit.com.tellit.controller;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.IQ;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;



import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.messages.LikeNotifycation;
import tellit.com.tellit.model.custom_xmpp.messages.ReviewNotifycation;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackIQ;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListByIDReq;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListResp;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingAllUserResp;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingAllUsersReq;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingUserByJidReq;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingUserByJidResp;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewListByIDReq;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewListResp;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;

/**
 * Created by probojnik on 30.06.15.
 */
public class ReviewController {
//    List<IReviewActions> reviewActionsList = new ArrayList<>();
    boolean isSynced = false;
    private static ReviewController ourInstance = new ReviewController();
//    private Handler mainHandler;
    StanzaListener stanzaListener;
    @Inject
    UserData userData;
    @Inject
    MyApplication myApplication;

    public static ReviewController getInstance() {
        return ourInstance;
    }

    private ReviewController() {
        Injector.inject(this);


    }

    public void like(final ReviewData reviewData, final CustomStanzaCallback<FeedbackIQ> callback){
        FeedbackIQ feedbackIQ = new FeedbackIQ();
        feedbackIQ.setReviewId("" + reviewData.getId());
        feedbackIQ.setState("1");
        feedbackIQ.setVote("1");
        feedbackIQ.setType(IQ.Type.set);
        CustomStanzaController.getInstance().sendStanza(feedbackIQ, new CustomStanzaCallback<FeedbackIQ>() {
            @Override
            public void resultOK(FeedbackIQ result) {
                deleteFromBase(reviewData);
                LikeData likeData = new LikeData();
                likeData.setReviewId(reviewData);
                likeData.setState(1);
                likeData.setVote(1);
                likeData.setCreateDate(new Date());
                likeData.setFromJID(userData.getMyJid());
                likeData.setId(Integer.parseInt(result.getFeedbackId()));
                try {
                    HelperFactory.getInstans().getDao(LikeData.class).create(likeData);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                callback.resultOK(result);
            }

            @Override
            public void error(Exception ex) {
                callback.error(ex);
            }
        });
    }
    public void dislike(final ReviewData reviewData, final CustomStanzaCallback<FeedbackIQ> callback){
        FeedbackIQ feedbackIQ = new FeedbackIQ();
        feedbackIQ.setReviewId("" + reviewData.getId());
        feedbackIQ.setState("1");
        feedbackIQ.setVote("-1");
        feedbackIQ.setType(IQ.Type.set);
        CustomStanzaController.getInstance().sendStanza(feedbackIQ, new CustomStanzaCallback<FeedbackIQ>() {
            @Override
            public void resultOK(FeedbackIQ result) {
                deleteFromBase(reviewData);
                LikeData likeData = new LikeData();
                likeData.setReviewId(reviewData);
                likeData.setState(1);
                likeData.setVote(-1);
                likeData.setCreateDate(new Date());
                likeData.setFromJID(userData.getMyJid());
                likeData.setId(Integer.parseInt(result.getFeedbackId()));
                try {
                    HelperFactory.getInstans().getDao(LikeData.class).create(likeData);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                callback.resultOK(result);
            }

            @Override
            public void error(Exception ex) {
                callback.error(ex);
            }
        });
    }
    public void deleteLike(final ReviewData reviewData, final CustomStanzaCallback<FeedbackIQ> callback){
        FeedbackIQ feedbackIQ = new FeedbackIQ();
        feedbackIQ.setReviewId("" + reviewData.getId());
        feedbackIQ.setState("0");
        feedbackIQ.setVote("0");
        feedbackIQ.setType(IQ.Type.set);
        CustomStanzaController.getInstance().sendStanza(feedbackIQ, new CustomStanzaCallback<FeedbackIQ>() {
            @Override
            public void resultOK(FeedbackIQ result) {
               deleteFromBase(reviewData);
                callback.resultOK(result);
            }

            @Override
            public void error(Exception ex) {
                callback.error(ex);
            }
        });
    }

    public void deleteFromBase(ReviewData reviewData){
        try {
            LikeData likeData = (LikeData) HelperFactory.getInstans().getDao(LikeData.class).queryBuilder().where().eq("reviewId_id",reviewData).and()
                    .eq("fromJID", userData.getMyJid()).queryForFirst();
            if(likeData!=null)
                HelperFactory.getInstans().getDao(LikeData.class).delete(likeData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addLike(LikeNotifycation likeNotifycation){
        FeedbackListByIDReq feedbackListByIDReq = new FeedbackListByIDReq(likeNotifycation.getId());

        CustomStanzaController.getInstance().sendStanza(feedbackListByIDReq, new CustomStanzaCallback<FeedbackListResp>() {

            @Override
            public void resultOK(FeedbackListResp result) {
                for(LikeData likeData : result.getLikeDataList()){
                    Dao<LikeData,Integer> likeDao = HelperFactory.getInstans().getDao(LikeData.class);
                    try {
                        LikeData find_like = likeDao.queryBuilder().where().eq("id",likeData.getId()).queryForFirst();
                        if(find_like == null) {
                            try {
                                ReviewData reviewData = (ReviewData) HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().where().eq("id", likeData.getReviewId().getId()).queryForFirst();
                                if (reviewData != null) {
                                    likeData.setReviewId(reviewData);
                                }
                            }catch (SQLException ex){ex.printStackTrace();}
                            likeDao.create(likeData);
                            EventBus.getDefault().post(likeData);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void error(Exception ex) {

            }
        });

    }
    public void addReview(ReviewNotifycation reviewNotifycation){
        ReviewListByIDReq reviewByIDReq = new ReviewListByIDReq();
        reviewByIDReq.setId(reviewNotifycation.getId());

        CustomStanzaController.getInstance().sendStanza(reviewByIDReq, new CustomStanzaCallback<ReviewListResp>() {
            @Override
            public void resultOK(ReviewListResp result) {
                for(ReviewData reviewData :result.getReviewDataList()){
                    try {
                        ReviewData findReview = (ReviewData) HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().where().eq("id",reviewData.getId()).queryForFirst();
                        if(findReview==null) {
                            HelperFactory.getInstans().getDao(ReviewData.class).create(reviewData);
                        }else{
                            findReview.update(reviewData);
                            HelperFactory.getInstans().getDao(ReviewData.class).update(findReview);
                        }
                        if(!userData.getMyJid().equals(reviewData.getFromJID())) {
                            userData.incTellitCounter();
                        }
                        uppRateToContact(reviewData);
                        EventBus.getDefault().post(reviewData);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void error(Exception ex) {
                Log.e("!!!!!!!ERROR!!!!!!!!!!", ex.getMessage());
            }
        });
    }

    private void uppRateToContact(final ReviewData reviewData){
        try {


            RatingUserByJidReq req = new RatingUserByJidReq();
            req.setJid(reviewData.getToJID());
            CustomStanzaController.getInstance().sendStanza(req, new CustomStanzaCallback<RatingUserByJidResp>() {
                @Override
                public void resultOK(RatingUserByJidResp result) {
                    ContactData contactData = null;
                    try {
                        contactData = (ContactData) HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("jid", reviewData.getToJID()).queryForFirst();
                        if(contactData!=null) {
                            contactData.setRate(result.getRating());
                            contactData.setReviewNumber(result.getReviewNumber());
                            HelperFactory.getInstans().getDao(ContactData.class).update(contactData);
                            EventBus.getDefault().post(contactData);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(Exception ex) {

                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void uppRateToContacts(){
        RatingAllUsersReq ratingAllUsersReq = new RatingAllUsersReq();
        ratingAllUsersReq.setOwnerRosterJID(userData.getMyJid());
        CustomStanzaController.getInstance().sendStanza(ratingAllUsersReq, new CustomStanzaCallback<RatingAllUserResp>() {
            @Override
            public void resultOK(RatingAllUserResp result) {
                List<RatingAllUserResp.RatingUser> ratingUserList = result.getRatingUserList();
                for(RatingAllUserResp.RatingUser ratingUser : ratingUserList){
                    try {
                        ContactData contactData = (ContactData) HelperFactory.getInstans().getDao(ContactData.class).queryBuilder().where().eq("jid", ratingUser.getJid()).queryForFirst();
                        if(contactData!=null) {
                            contactData.setRate(ratingUser.getRating());
                            contactData.setReviewNumber(ratingUser.getReviewNumber());
                            HelperFactory.getInstans().getDao(ContactData.class).update(contactData);
                            EventBus.getDefault().post(this);

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void error(Exception ex) {

            }
        });


    }



}
