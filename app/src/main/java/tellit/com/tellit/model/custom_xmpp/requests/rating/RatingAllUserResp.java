package tellit.com.tellit.model.custom_xmpp.requests.rating;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by probojnik on 06.07.15.
 *
 * <user>
 <jid>user</jid>
 <rating>1.00</rating>
 </user>

 new vers -- <user><jid>0390ec81d65ad51f849ae7a219ab6e2e@tellit</jid><rating>4.0</rating><reviewNumber>1</reviewNumber></user>

 *
 */
public class RatingAllUserResp extends IQ {
    List<RatingUser> ratingUserList = new ArrayList<>();
    public RatingAllUserResp() {
        super(RatingAllUsersReq.ELEMENT, RatingAllUsersReq.NAMESPACE);

    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

    public List<RatingUser> getRatingUserList() {
        return ratingUserList;
    }

    public void setRatingUserList(List<RatingUser> ratingUserList) {
        this.ratingUserList = ratingUserList;
    }

    public static class RatingUser{
        String jid;
        float rating;
        int reviewNumber;

        public String getJid() {
            return jid;
        }

        public void setJid(String jid) {
            this.jid = jid;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public int getReviewNumber() {
            return reviewNumber;
        }

        public void setReviewNumber(int reviewNumber) {
            this.reviewNumber = reviewNumber;
        }
    }
}
