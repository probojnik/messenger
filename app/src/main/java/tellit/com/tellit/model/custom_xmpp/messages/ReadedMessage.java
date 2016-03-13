package tellit.com.tellit.model.custom_xmpp.messages;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

import java.util.Date;

/**
 * Created by probojnik on 25.06.15.
 *
 *  <message to='+380635716703@tellit/00000000-05d6-8cf5-ffff-ffff89ef02cb' id='EQ3Pu-316' type='chat'><read xmlns=‘custom:message:read’> <id>id'Hc84F-353</id><read/></message>
 */
public class ReadedMessage implements ExtensionElement {
    public static final String ELEMENT = "read";
    public static final String NAMESPACE = "custom:message:read";
    String id;
    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML() {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.append("><id>");
        xml.append(id);
        xml.append("</id>");
        xml.closeElement(this);
        return xml;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
