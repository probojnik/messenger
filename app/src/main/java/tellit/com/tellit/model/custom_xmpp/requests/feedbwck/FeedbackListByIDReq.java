package tellit.com.tellit.model.custom_xmpp.requests.feedbwck;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 01.07.15.
 *
 * <query xmlns="custom:iq:feedbackById">
     <id>1</id>
     </query>
 *
 */
public class FeedbackListByIDReq extends IQ {

    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:feedbackById";
    long id;
    public FeedbackListByIDReq(long id) {
        super(ELEMENT, NAMESPACE);
        this.id = id;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<id>" + id + "</id>");
        return xml;
    }


}
