package tellit.com.tellit.model.custom_xmpp.providers;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.custom_xmpp.requests.feedbwck.FeedbackIQ;


/**
 * Created by probojnik on 22.06.15.
 */
public class FeedbackIQProvider extends IQProvider<FeedbackIQ>{
    public static final String ELEMENT = "response";

    @Override
    public FeedbackIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        FeedbackIQ feedbackIQ = new FeedbackIQ();

        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "feedbackId":
                            feedbackIQ.setFeedbackId(parser.nextText());
                            break ;
                        default:
                            break ;
                    }
                    break ;
                case XmlPullParser.END_TAG:
                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
                    break;
                default:
                    break;
            }
        }


        return feedbackIQ;
    }
}
