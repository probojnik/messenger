package tellit.com.tellit.model.custom_xmpp.providers;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.model.custom_xmpp.requests.rating.RatingAllUserResp;


/**
 *
 <user>
 <jid>user</jid>
 <rating>1.00</rating>
 </user>

 <user><jid>0390ec81d65ad51f849ae7a219ab6e2e@tellit</jid><rating>4.0</rating><reviewNumber>1</reviewNumber></user>

 */
public class RatingAllUsersProvider extends IQProvider<RatingAllUserResp>{
    public static final String ELEMENT = "reviewList";

    @Override
    public RatingAllUserResp parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        RatingAllUserResp ratingAllUserResp = new RatingAllUserResp();
        List<RatingAllUserResp.RatingUser> ratingUserList = new ArrayList<>();

        String name = null;
        outerloop:while(true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "user":
                            parseRating(parser,ratingUserList);
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
        ratingAllUserResp.setRatingUserList(ratingUserList);

        return ratingAllUserResp;
    }

    private void parseRating(XmlPullParser parser, List<RatingAllUserResp.RatingUser> ratingUserList) throws IOException, XmlPullParserException {
        final int initialDepth = parser.getDepth();
        String name = null;
        RatingAllUserResp.RatingUser ratingUser = new RatingAllUserResp.RatingUser();
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "jid":
                            ratingUser.setJid(parser.nextText());
                            break ;
                        case "rating":
                            ratingUser.setRating(Float.parseFloat(parser.nextText()));
                            break ;
                        case "reviewNumber":
                            ratingUser.setReviewNumber(Integer.parseInt(parser.nextText()));
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
        ratingUserList.add(ratingUser);
    }

}
