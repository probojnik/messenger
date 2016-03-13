package tellit.com.tellit.model.custom_xmpp.requests.feedbwck;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 23.06.15.
 *
 <query xmlns="custom:iq:feedback">
     <reviewId>12</reviewId>
     <vote>1</vote>
     <state>1</state>
 </query>

 */
public class FeedbackIQ extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:feedback";
    private String reviewId,vote,state;
    String feedbackId;

    public FeedbackIQ() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<reviewId>" + reviewId + "</reviewId>");
        xml.append("<vote>"+vote+"</vote>");
        xml.append("<state>"+state+"</state>");
        return xml;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }
}
