package tellit.com.tellit.model.custom_xmpp.requests.history;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by Stas on 02.09.2015.
 */
public class ChatIQReq extends IQ {
    public static final String ELEMENT = "list", NAMESPACE = "urn:xmpp:archive";
    private final String jid;

    public ChatIQReq(String jid) {
        super(ELEMENT, NAMESPACE);
        this.jid = jid;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
//        xml.append(" with='");
//        xml.append("test2@tellit");
//        xml.append("'");
//        xml.append(" start='1469-07-21T02:00:00Z'");
        xml.append(">");
        xml.append("<set xmlns='http://jabber.org/protocol/rsm'>");
        xml.append("<max>1000</max>");
        xml.append("</set>");
        return xml;
    }
}