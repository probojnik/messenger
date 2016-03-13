package tellit.com.tellit.model.custom_xmpp.providers.review;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewListResp;
import tellit.com.tellit.model.review.ReviewData;


/**
 *
 * <review>
 <id>1</id>
 <fromJID>root</fromJID>
 <toJID>to</toJID>
 <review>cool</review>
 <rate>2</rate>
 <state>1</state>
 <createDate>1050</createDate>
 <updateDate>2</updateDate>
 </review>


 <iq type="result" id="X50uF-21" to="+380635716703@ip-172-31-7-92/ffffffff-b8c9-d697-ffff-ffff89ef02cb">
 <reviewList xmlns="custom:iq:review">
 <review><id>1</id><fromJID>+380635716703@ip-172-31-7-92</fromJID><toJID>user1@ip-172-31-7-92</toJID><review>Test</review><rate>1</rate><state>1</state><createDate>1434982509000</createDate><updateDate>1434982509000</updateDate></review></reviewList></iq>

 */
public class RewiewListIQProvider extends IQProvider<ReviewListResp>{
    public static final String ELEMENT = "reviewList";

    @Override
    public ReviewListResp parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        ReviewListResp reviewListResp = new ReviewListResp();
        List<ReviewData> reviewDataList = new ArrayList<>();
        int eventType = parser.getEventType();
        while(eventType!= XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && "review".equals(parser.getName())) {
                ReviewData reviewData = new ReviewData();
                int deep = parser.getDepth();
                eventType = parser.next();
                do {
                    if (eventType == XmlPullParser.START_TAG) {
                        if ("id".equals(parser.getName())) {
                            parser.next();
                            reviewData.setId(Long.parseLong(parser.getText()));
                        } else if ("fromJID".equals(parser.getName())) {
                            parser.next();
                            reviewData.setFromJID(parser.getText());
                        } else if ("toJID".equals(parser.getName())) {
                            parser.next();
                            reviewData.setToJID(parser.getText());
                        } else if ("msg".equals(parser.getName())) {
                            parser.next();
                            reviewData.setMsg(parser.getText());
                        } else if ("rate".equals(parser.getName())) {
                            parser.next();
                            reviewData.setRate(Byte.parseByte(parser.getText()));
                        } else if ("state".equals(parser.getName())) {
                            parser.next();
                            reviewData.setState(Byte.parseByte(parser.getText()));
                        } else if ("createDate".equals(parser.getName())) {
                            parser.next();
                            reviewData.setCreateDate(new Date(Long.parseLong(parser.getText())));
                        } else if ("updateDate".equals(parser.getName())) {
                            parser.next();
                            reviewData.setUpdateDate(new Date(Long.parseLong(parser.getText())));
                        }
                    }

                    eventType = parser.next();

                } while (parser.getDepth() > deep);

                reviewDataList.add(reviewData);

            }
            eventType = parser.next();

            if(eventType == XmlPullParser.END_TAG && ELEMENT.equals(parser.getName()))
                break;
        }
        reviewListResp.setReviewDataList(reviewDataList);

        return reviewListResp;
    }
}
