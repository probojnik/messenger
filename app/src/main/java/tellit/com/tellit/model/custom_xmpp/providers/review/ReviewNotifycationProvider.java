package tellit.com.tellit.model.custom_xmpp.providers.review;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.custom_xmpp.messages.ReviewNotifycation;


/**
 * Created by probojnik on 30.06.15.

 <review xmlns="custom:iq::notification">
 <toJID>user1</toJID>
 <id>12</id>
 </review>

 */
public class ReviewNotifycationProvider extends ExtensionElementProvider<ReviewNotifycation>{
    @Override
    public ReviewNotifycation parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        ReviewNotifycation reviewNotifycation = new ReviewNotifycation();
        int eventType = parser.getEventType();
        while(eventType!= XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG){
                if("toJID".equals(parser.getName())){
                   parser.next();
                    reviewNotifycation.setToJID(parser.getText());
                }
                if("id".equals(parser.getName())){
                    parser.next();
                    if(parser.getText()!=null && parser.getText().matches("\\d+"))
                        reviewNotifycation.setId(Integer.parseInt(parser.getText()));
                }
            }
            eventType = parser.next();
            if(eventType == XmlPullParser.END_TAG && ReviewNotifycation.ELEMENT.equals(parser.getName()))
                break;

        }

        return reviewNotifycation;
    }
}
