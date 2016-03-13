package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 23.06.15.
 *
 <query xmlns="custom:iq:review">
 <starttime>123</starttime>
 <toJID>test</toJID>
 </query>

 */
public class ReviewListByTimeReq extends IQ {

    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:review";
    long starttime;
    String toJID;
    public ReviewListByTimeReq() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<starttime>" + starttime + "</starttime>");
        xml.append("<toJID>"+toJID+"</toJID>");
        return xml;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public String getToJID() {
        return toJID;
    }

    public void setToJID(String toJID) {
        this.toJID = toJID;
    }
}
