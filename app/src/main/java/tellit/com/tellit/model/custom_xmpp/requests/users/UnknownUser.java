package tellit.com.tellit.model.custom_xmpp.requests.users;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 07.09.15.
 *
 * <query xmlns="custom:iq:unknownUser">
 <username>59b8db9a4e2e64044ec902a7ac656c15</username>
 </query>
 <query xmlns="custom:iq:unknownUser">
 <username>59b8db9a4e2e64044ec902a7ac656c15</username>
 <mode>1</mode>
 </query>

 mode - 0 (get row phone), 1 - get mask

 */
public class UnknownUser extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:unknownUser";
    String username,phoneMask;
    private int mode;

    public UnknownUser() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<username>" + username + "</username>");
        xml.append("<mode>" + mode + "</mode>");

        return xml;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneMask() {
        return phoneMask;
    }

    public void setPhoneMask(String phoneMask) {
        this.phoneMask = phoneMask;
    }

    public int getMode() {
        return mode;
    }

    /**
     *
     * @param mode 0 (get row phone), 1 - get mask
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
}
