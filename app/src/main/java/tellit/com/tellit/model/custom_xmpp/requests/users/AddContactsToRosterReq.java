package tellit.com.tellit.model.custom_xmpp.requests.users;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.contacts.ContactRosterBean;
import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.log.TraceHelper;


/**
 * Created by probojnik on 02.07.15.
 *
 <query xmlns="custom:iq:rosterItems">
 <item>
 <jid>user@domain</jid>
 <nick>sadasda</nick>
 </item>
 ...
 </query>


 */
public class AddContactsToRosterReq extends IQ {
    public static final String ELEMENT = "query", NAMESPACE = "custom:iq:rosterItems";
    public static final int ADD = 0, REMOVE = 1;
    private final int action;
    private List<ContactData> list;

    public AddContactsToRosterReq(List<ContactData> list, int action) {
        super(ELEMENT, NAMESPACE);
        this.list = list;
        this.action = action;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        for(ContactData contact : list) {
            xml.append("<item>");
            xml.append("<jid>" + contact.getJid() + "</jid>");
            xml.append("<nick>" + (!TextUtil.isEmpty(contact.getName())?contact.getName():"пусто") + "</nick>");
            xml.append("<action>" + action + "</action>");
            xml.append("</item>");
        }
        TraceHelper.print("getIQChildElementBuilder", xml);
        return xml;
    }


}
