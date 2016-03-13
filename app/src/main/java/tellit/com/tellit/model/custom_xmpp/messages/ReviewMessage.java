package tellit.com.tellit.model.custom_xmpp.messages;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.SHA1;
import org.jivesoftware.smack.util.XmlStringBuilder;

/**
 * Created by probojnik on 25.06.15.
 *    <review_message xmlns="custom:message:review">
 <msg_guid>4CF1E236-DB56-4056-A223-95AA07499F90</msg_guid>
 <vote>0</vote>
 </review_message>

 *
 *
 */
public class ReviewMessage implements ExtensionElement {
    public static final String ELEMENT = "review_message";
    public static final String NAMESPACE = "custom:message:review";
    int vote;
    String uuid;
    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    public ReviewMessage() {
    }

    public ReviewMessage(String uuid , int vote) {
        this.vote = vote;
        this.uuid = uuid;
    }

    @Override
    public CharSequence toXML() {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.append("><msg_uuid>");
        xml.append(uuid);
        xml.append("</msg_uuid>");
        xml.append("<vote>");
        xml.append(""+vote);
        xml.append("</vote>");
        xml.closeElement(this);
        return xml;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
