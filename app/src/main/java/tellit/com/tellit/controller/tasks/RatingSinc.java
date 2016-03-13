package tellit.com.tellit.controller.tasks;

import android.util.Log;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingAllUserResp;
import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingAllUsersReq;
import tellit.com.tellit.modules.VCardModule;

/**
 * Created by probojnik on 30.09.15.
 */
public class RatingSinc implements Callable {

    @Inject
    UserData userData;
    @Inject
    VCardModule vCard;

    public RatingSinc() {
        Injector.inject(this);
    }

    @Override
    public Object call() throws Exception {

        Log.d("~~~RatingSinc", "uppRate");
        RatingAllUsersReq ratingAllUsersReq = new RatingAllUsersReq();
        ratingAllUsersReq.setOwnerRosterJID(userData.getMyJid());
        CustomStanzaController.getInstance().sendStanza(ratingAllUsersReq, new CustomStanzaCallback<RatingAllUserResp>() {
            @Override
            public void resultOK(RatingAllUserResp result) {
                List<RatingAllUserResp.RatingUser> ratingUserList = result.getRatingUserList();
                for (final RatingAllUserResp.RatingUser ratingUser : ratingUserList) {
                    vCard.getContactByJid(ratingUser.getJid(), new VCardModule.ContactDataCallback() {
                        @Override
                        public void result(ContactData contactData) {
                            if (contactData != null) {
                                contactData.setRate(ratingUser.getRating());
                                contactData.setReviewNumber(ratingUser.getReviewNumber());
                                try {
                                    ContactData.getDao().update(contactData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void error(Exception ex) {

            }
        });
        return null;
    }
}
