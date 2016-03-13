package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 12.08.15.
 * <query xmlns="custom:iq:reviewAllFrom">
 <jid>test@domain</jid>
 ...
 </query>

 *
 */
public class ReviewAllFromReq extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:reviewAllFrom";
    private String[] listJids;

    public ReviewAllFromReq() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        for (String jid : listJids) {
            xml.append("<jid>" + jid + "</jid>");
        }
        return xml;
    }

    public String[] getListJids() {
        return listJids;
    }

    public void setListJids(String ... listJids) {
        this.listJids = listJids;
    }
}
