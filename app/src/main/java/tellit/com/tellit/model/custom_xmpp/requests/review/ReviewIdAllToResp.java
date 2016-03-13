package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 18.08.15.
 *
 * <query xmlns="custom:iq:reviewIdAllTo">
        <id>1</id>
        ...
    </query>
 *
 */
public class ReviewIdAllToResp extends IQ {
    long[] id_list;
    public ReviewIdAllToResp() {
        super(ReviewIdAllToReq.ELEMENT, ReviewIdAllToReq.NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

    public long[] getId_list() {
        return id_list;
    }

    public void setId_list(long[] id_list) {
        this.id_list = id_list;
    }
}
