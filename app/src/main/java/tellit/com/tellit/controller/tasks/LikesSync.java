package tellit.com.tellit.controller.tasks;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.controller.operations.SyncReview;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaCallback;
import tellit.com.tellit.model.custom_xmpp.CustomStanzaController;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListAllReq;
import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackListResp;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.LikeData;

/**
 * Created by probojnik on 29.09.15.
 */
public class LikesSync implements Callable {
    private Dao likeDao;
    List<String> jids = new ArrayList<>();
    @Inject
    UserData userData;

    public LikesSync(List<ContactData> contactDatas) {
        Injector.inject(this);
        if(contactDatas == null) return;
        for (ContactData contactData :contactDatas){
            if(contactData.getJid()!=null){
                jids.add(contactData.getJid());
            }
        }
        jids.add(userData.getMyJid());
    }

    @Override
    public Object call() throws Exception {

        likeDao = HelperFactory.getInstans().getDao(LikeData.class);
        Log.d("~~~LikesSync", "sync like - "+jids.size());



        FeedbackListAllReq req = new FeedbackListAllReq(jids);
        FeedbackListResp result = (FeedbackListResp) CustomStanzaController.getInstance().sendStanzaSerial(req);
        for (LikeData likeData : result.getLikeDataList()) {
            try {
                LikeData find_like = (LikeData) likeDao.queryBuilder().where().eq("id", likeData.getId()).queryForFirst();
                if (find_like == null) {
                    likeDao.create(likeData);
                } else {
                    find_like.update(likeData);
                    likeDao.update(find_like);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        EventBus.getDefault().post(new SyncReview.SincOperationResult(true));
        Log.d("~~~LikesSync", "sync like end");

        return null;
    }
}
