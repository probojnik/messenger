package tellit.com.tellit.model.custom_xmpp.requests.feedbwck;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 01.07.15.
 */
public class FeedbackListByTimeReq extends IQ {

    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:feedback";
    long starttime;
    long reviewId;
    public FeedbackListByTimeReq() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<starttime>" + starttime + "</starttime>");
        xml.append("<reviewId>"+reviewId+"</reviewId>");
        return xml;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getReviewId() {
        return reviewId;
    }

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }
}
