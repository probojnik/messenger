package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

/**
 * Created by probojnik on 02.07.15.
 *
 <query xmlns="custom:iq:reviewAll">
 <jid>test@domain</jid>
 ...
 </query>
 */
public class ReviewsAllRoster extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:reviewAll";
    private List<String> listJids;

    public ReviewsAllRoster(List<String> listJids) {
        super(ELEMENT, NAMESPACE);
        this.listJids = listJids;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        for(String jid : listJids) {
            xml.append("<jid>" + jid + "</jid>");
        }
        return xml;
    }


}
