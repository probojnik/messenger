package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 18.08.15.
 *
 * <query xmlns="custom:iq:reviewIdAllTo">
 <jid>test@domain</jid>
 </query>
 *
 */
public class ReviewIdAllToReq extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:reviewIdAllTo";
    private String[] listJids;


    public ReviewIdAllToReq() {
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

    public void setListJids(String... listJids) {
        this.listJids = listJids;
    }
}
