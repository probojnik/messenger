package tellit.com.tellit.model.custom_xmpp.messages;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by probojnik on 30.06.15.
 * <message to="user1@test.com" from="test.com">
 <subject>review</subject>
 <review xmlns="custom:iq::notification">
 <toJID>user1</toJID>
 <id>12</id>
 </review>
 </message>
 */
public class ReviewNotifycation implements ExtensionElement {
    public static final String ELEMENT = "review";
    public static final String NAMESPACE = "custom:iq::notification";
    String toJID;
    int id;
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
        return null;
    }

    public String getToJID() {
        return toJID;
    }

    public void setToJID(String toJID) {
        this.toJID = toJID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
