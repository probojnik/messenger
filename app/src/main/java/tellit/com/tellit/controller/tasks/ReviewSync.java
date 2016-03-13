package tellit.com.tellit.controller.tasks;

import android.util.Log;

import org.jivesoftware.smack.packet.Stanza;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewListResp;
import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewsAllRoster;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.modules.VCardModule;

/**
 * Created by probojnik on 29.09.15.
 */
public class ReviewSync implements Callable {
    @Inject
    UserData userData;
    private List<String> jids = new ArrayList<>();
    @Inject
    VCardModule vCard;


    public ReviewSync(List<ContactData> contactDatas) {
        if(contactDatas == null) return;
        Injector.inject(this);
       for (ContactData contactData :contactDatas){
           if(contactData.getJid()!=null){
               jids.add(contactData.getJid());
           }
       }
        jids.add(userData.getMyJid());
    }

    @Override
    public Object call() throws Exception {
        if(jids.size() == 0) return null;

        ReviewsAllRoster allRoster = new ReviewsAllRoster(jids);
        Log.d("~~~ReviewSync", "sync review - "+ jids.size());
        ReviewListResp result = (ReviewListResp) CustomStanzaController.getInstance().sendStanzaSerial(allRoster);
        List<ReviewData> reviewList = result.getReviewDataList();
        for (ReviewData reviewData : reviewList) {
            try {
                ReviewData findReview = (ReviewData) HelperFactory.getInstans().getDao(ReviewData.class).queryBuilder().where().eq("id", reviewData.getId()).queryForFirst();
                if (findReview == null) {
                    HelperFactory.getInstans().getDao(ReviewData.class).create(reviewData);
                } else {
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
        Log.d("~~~ReviewSync", "sync review end");


        return null;
    }
}
