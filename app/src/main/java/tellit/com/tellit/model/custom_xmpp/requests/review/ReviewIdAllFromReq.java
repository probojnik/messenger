package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 18.08.15.
 *

 *
 */
public class ReviewIdAllFromReq extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:reviewIdAllFrom";
    private String[] listJids;


    public ReviewIdAllFromReq() {
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

    public void setListJids(String... listJids) {
        this.listJids = listJids;
    }
}
