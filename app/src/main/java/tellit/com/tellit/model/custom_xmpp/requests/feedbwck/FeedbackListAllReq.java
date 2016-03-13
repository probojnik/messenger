package tellit.com.tellit.model.custom_xmpp.requests.feedbwck;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

/**
 * Created by probojnik on 01.07.15.
 <query xmlns="custom:iq:likeAll">
 <jid>test@domain</jid>
 <jid>test1@domain</jid>
 ...
 </query>


 */
public class FeedbackListAllReq extends IQ {

    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:likeAll";
    List<String> jidList;

    public FeedbackListAllReq(List<String> listJids) {
        super(ELEMENT, NAMESPACE);
        jidList = listJids;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        for(String jid :jidList)
            xml.append("<jid>" + jid + "</jid>");
        return xml;
    }


}
