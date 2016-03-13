package tellit.com.tellit.model.custom_xmpp.providers;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;

import tellit.com.tellit.model.custom_xmpp.messages.ReadedMessage;


/**
 * Created by probojnik on 25.06.15.
 *
 *  <message to='+380635716703@tellit/00000000-05d6-8cf5-ffff-ffff89ef02cb' id='EQ3Pu-316' type='chat'>
 *      <read xmlns=‘custom:message:read’> <id>id'Hc84F-353</id><read/></message>

 */
public class ReadedMessageProvider extends ExtensionElementProvider<ReadedMessage> {
    @Override
    public ReadedMessage parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ReadedMessage readedMessage = new ReadedMessage();
        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "id":
                            readedMessage.setId(parser.nextText());
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
       return readedMessage;
    }
}
