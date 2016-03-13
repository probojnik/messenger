package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 01.07.15.
 */
public class ReviewListByJIDReq extends IQ {

    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:reviewLast";
    String  jid;
    int lastReviewsNumber;
    public ReviewListByJIDReq() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<jid>" + jid + "</jid>");
        xml.append("<lastReviewsNumber>"+lastReviewsNumber+"</lastReviewsNumber>");
        return xml;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public int getLastReviewsNumber() {
        return lastReviewsNumber;
    }

    public void setLastReviewsNumber(int lastReviewsNumber) {
        this.lastReviewsNumber = lastReviewsNumber;
    }
}
