package tellit.com.tellit.model.custom_xmpp.requests.users;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.model.contacts.ContactRosterBean;
import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by probojnik on 25.09.15.
 * <query xmlns='custom:iq:registerFriends'/>
 ответ прийдет в виде
 <query xmlns="custom:iq:registerFriends">
 <username>User</username>
 <username>User2</username>
 ....
 </query>
 *
 *
 *
 */
public class RegisterFriendsReq extends IQ {
    public static final String NAMESPACE = "custom:iq:registerFriends";
    List<String> users = new ArrayList<>();
    public RegisterFriendsReq() {
        super("query", NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        return xml;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
