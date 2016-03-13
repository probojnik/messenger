package tellit.com.tellit.model.custom_xmpp.providers.review;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.custom_xmpp.messages.ReviewMessage;


/**
 * Created by probojnik on 14.07.15.
 */
public class ReviewMessageProvider extends ExtensionElementProvider<ReviewMessage> {
    @Override
    public ReviewMessage parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ReviewMessage reviewMessage = new ReviewMessage();
        int eventType = parser.getEventType();
        while(eventType!= XmlPullParser.END_DOCUMENT){
            if(eventType == XmlPullParser.START_TAG){
                if("msg_uuid".equals(parser.getName())){
                    parser.next();
                    reviewMessage.setUuid(parser.getText());
                }
                if("vote".equals(parser.getName())){
                    parser.next();
                    if(parser.getText()!=null && parser.getText().length()>0)
                        try{
                        reviewMessage.setVote(Integer.parseInt(parser.getText()));
                        }catch (NumberFormatException ex){

                        }
                }
            }
            eventType = parser.next();
            if(eventType == XmlPullParser.END_TAG && ReviewMessage.ELEMENT.equals(parser.getName()))
                break;

        }

        return reviewMessage;
    }
}
