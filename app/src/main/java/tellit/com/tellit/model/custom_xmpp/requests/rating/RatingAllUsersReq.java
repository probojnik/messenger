package tellit.com.tellit.model.custom_xmpp.requests.rating;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 02.07.15.
 *
 <query xmlns="custom:iq:ratingAllUser">
 <ownerRosterJID>test</ownerRosterJID>
 </query>

 */
public class RatingAllUsersReq extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:ratingAllUser";
    String ownerRosterJID;

    public RatingAllUsersReq() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<ownerRosterJID>" + ownerRosterJID + "</ownerRosterJID>");
        return xml;
    }

    public String getOwnerRosterJID() {
        return ownerRosterJID;
    }

    public void setOwnerRosterJID(String ownerRosterJID) {
        this.ownerRosterJID = ownerRosterJID;
    }
}
