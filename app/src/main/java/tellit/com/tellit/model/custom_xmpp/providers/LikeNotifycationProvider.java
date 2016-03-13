package tellit.com.tellit.model.custom_xmpp.providers;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.custom_xmpp.messages.LikeNotifycation;
import tellit.com.tellit.model.custom_xmpp.messages.ReviewNotifycation;


/**
 <message to="user254@igor-host" from="igor-host"><subject>feedback</subject><feedback xmlns="custom:iq::notification"><id>649</id></feedback></message>
 */
public class LikeNotifycationProvider extends ExtensionElementProvider<LikeNotifycation>{
    @Override
    public LikeNotifycation parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        LikeNotifycation likeNotifycation = new LikeNotifycation();

        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "id":
                            likeNotifycation.setId(Integer.parseInt(parser.nextText()));
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


        return likeNotifycation;
    }
}
