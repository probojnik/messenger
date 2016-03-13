package tellit.com.tellit.model.custom_xmpp.providers;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingUserByJidResp;


/**
 *
 <user>
 <jid>user</jid>
 <rating>1.00</rating>
 ><reviewNumber>1</reviewNumber><
 </user>

 */
public class RatingUserByJidProvider extends IQProvider<RatingUserByJidResp>{


    @Override
    public RatingUserByJidResp parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        RatingUserByJidResp ratingUserByJidResp = new RatingUserByJidResp();
        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "rating":
                            ratingUserByJidResp.setRating(Float.parseFloat(parser.nextText()));
                            break ;
                        case "reviewNumber":
                            ratingUserByJidResp.setReviewNumber(Integer.parseInt(parser.nextText()));
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
        return ratingUserByJidResp;
    }
}
