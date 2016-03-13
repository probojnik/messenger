package tellit.com.tellit.model.custom_xmpp.requests.feedbwck;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.model.review.LikeData;


/**
 * Created by probojnik on 23.06.15.
 */
public class FeedbackListResp extends IQ {
    public static final String ELEMENT = "feedbackList",NAMESPACE = "custom:iq:feedback";
    List<LikeData> likeDataList = new ArrayList<>();


    public FeedbackListResp() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

    public List<LikeData> getLikeDataList() {
        return likeDataList;
    }

    public void setLikeDataList(List<LikeData> likeDataList) {
        this.likeDataList = likeDataList;
    }
}
