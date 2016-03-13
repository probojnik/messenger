package tellit.com.tellit.model.custom_xmpp.requests.feedbwck;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 01.07.15.
 */
public class FeedbackListByReviewIDReq extends IQ {

    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:feedbackByReview";
    long id;
    public FeedbackListByReviewIDReq(long id) {
        super(ELEMENT, NAMESPACE);
        this.id = id;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<reviewId>" + id + "</reviewId>");

        return xml;
    }


}
