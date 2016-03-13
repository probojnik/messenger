package tellit.com.tellit.model.custom_xmpp.requests.review;

import org.jivesoftware.smack.packet.IQ;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.model.review.ReviewData;


/**
 * Created by probojnik on 23.06.15.
 *
 *  <review>
 <id>1</id>
 <fromJID>root</fromJID>
 <toJID>to</toJID>
 <review>cool</review>
 <rate>2</rate>
 <state>1</state>
 <createDate>1050</createDate>
 <updateDate>2</updateDate>
 </review>

 */
public class ReviewListResp extends IQ implements Serializable {
    public static final String ELEMENT = "reviewList",NAMESPACE = "custom:iq:review";
    List<ReviewData> reviewDataList = new ArrayList<>();


    public ReviewListResp() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

    public List<ReviewData> getReviewDataList() {
        return reviewDataList;
    }

    public void setReviewDataList(List<ReviewData> reviewDataList) {
        this.reviewDataList = reviewDataList;
    }
}
