package tellit.com.tellit.model.custom_xmpp.messages;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by probojnik on 12.08.15.
 *
 * <message to="user254@igor-host" from="igor-host"><subject>feedback</subject><feedback xmlns="custom:iq::notification"><id>649</id></feedback></message>
 *
 */
public class LikeNotifycation implements ExtensionElement {
    public static final String ELEMENT = "feedback";
    public static final String NAMESPACE = "custom:iq::notification";
    long id;
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

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
