package tellit.com.tellit.model.custom_xmpp.requests.history;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by Stas on 02.09.2015.
 */
public class MessageIQReq extends IQ {
    public static final String ELEMENT = "retrieve", NAMESPACE = "urn:xmpp:archive";
    private final String jid,  time;
    private int length, offset;

    public MessageIQReq(String jid, String time) {
        super(ELEMENT, NAMESPACE);
        this.jid = jid;
        this.time = time;
        this.length = -1;
        this.offset = -1;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(" with='");
        xml.append(jid);
        xml.append("'");
        xml.append(" start='");
        xml.append(time);
        xml.append("'>");
        xml.append("<set xmlns='http://jabber.org/protocol/rsm'>");
        if(length > 0){
            xml.append("<max>");
            xml.append(String.valueOf(length));
            xml.append("</max>");
        }
        if(offset > 0){
            xml.append("<after>");
            xml.append(String.valueOf(offset));
            xml.append("</after>");
        }
        xml.append("</set>");
        return xml;
    }

    public void setLength(int value) {
        this.length = value;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}