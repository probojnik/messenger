package tellit.com.tellit.model.custom_xmpp.requests.rating;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 02.07.15.
 *
 <query xmlns="custom:iq:userRating">
 <jid>user@domain</jid>
 </query>


 */
public class RatingUserByJidReq extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:userRating";
    String jid;

    public RatingUserByJidReq() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<jid>" + jid + "</jid>");
        return xml;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }
}
