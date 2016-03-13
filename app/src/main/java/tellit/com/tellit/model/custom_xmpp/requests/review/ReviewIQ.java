package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 22.06.15.
 */
public class ReviewIQ extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:review";
    int rate,state;
    String message,jid;
    long reviewid;


    public ReviewIQ() {
        super("query", "custom:iq:review");
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        //xml.append("<query xmlns='custom:iq:review'>");
        xml.append(">");
        xml.append("<msg>" + message + "</msg>");
        xml.append("<jid>"+jid+"</jid>");
        xml.append("<rate>"+rate+"</rate>");
        xml.append("<state>"+state+"</state>");
       // xml.append("</query>");
        return xml;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public long getReviewid() {
        return reviewid;
    }

    public void setReviewid(long reviewid) {
        this.reviewid = reviewid;
    }
}
