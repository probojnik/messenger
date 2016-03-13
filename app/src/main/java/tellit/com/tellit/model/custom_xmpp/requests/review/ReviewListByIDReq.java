package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by probojnik on 30.06.15.
 */
public class ReviewListByIDReq extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:iq:reviewIds";
    long id;
    private List<Long> ids;

    public ReviewListByIDReq() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        for(long id : ids)
            xml.append("<id>" + id + "</id>");
        return xml;
    }

    public long getId() {
        return id;
    }


    public void setId(int id) {
        this.ids = new ArrayList<>(1);
        ids.add((long) id);
    }

    public void setIds(List<Long> l_ids) {
        this.ids = l_ids;
    }
}
