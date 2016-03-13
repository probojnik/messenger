package tellit.com.tellit.model.custom_xmpp.providers.review;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.model.custom_xmpp.requests.review.ReviewIdAllToResp;


/**
 *
 <query xmlns="custom:iq:reviewIdAllTo">
 <id>1</id>
 ...
 </query>
 */
public class RewiewIdAllToIQProvider extends IQProvider<ReviewIdAllToResp>{

    @Override
    public ReviewIdAllToResp parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        ReviewIdAllToResp reviewIdAllToResp = new ReviewIdAllToResp();
        List<Long>  reviewIdsList = new ArrayList<>();
        int eventType = parser.getEventType();
        while (true) {


            if (eventType == XmlPullParser.START_TAG) {
                if ("id".equals(parser.getName())) {
                    parser.next();
                    String s_id = parser.getText();
                    try {
                        long l_id = Long.parseLong(s_id);
                        reviewIdsList.add(l_id);

                    }catch (Exception ex){ex.printStackTrace();}
                }
            }

            eventType = parser.next();
            if(eventType == XmlPullParser.END_TAG && "query".equals(parser.getName()))
                break;

        }
        long [] l_arr = new long[reviewIdsList.size()];
        for(int i = 0;i<reviewIdsList.size(); i++) l_arr[i] = reviewIdsList.get(i);
        reviewIdAllToResp.setId_list(l_arr);

        return reviewIdAllToResp;
    }
}
