package tellit.com.tellit.model.custom_xmpp.messages;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by probojnik on 03.09.15.
 *
 *  <message to="+380968405959@tellit" from="tellit" id="-1014408302"><subject>vcard update</subject><vcard xmlns="custom:iq::notification"><user>+380635716703</user></vcard></message>
 */
public class VCardNotification implements ExtensionElement {
    public static final String ELEMENT = "vcard";
    public static final String NAMESPACE = "custom:iq::notification";
    String user;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
