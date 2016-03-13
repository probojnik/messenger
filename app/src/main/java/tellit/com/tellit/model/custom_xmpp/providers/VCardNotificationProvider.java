package tellit.com.tellit.model.custom_xmpp.providers;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.custom_xmpp.messages.ReviewMessage;
import tellit.com.tellit.model.custom_xmpp.messages.VCardNotification;

/**
 * Created by probojnik on 03.09.15.
 *  <message to="+380968405959@tellit" from="tellit" id="-1014408302"><subject>vcard update</subject><vcard xmlns="custom:iq::notification"><user>+380635716703</user></vcard></message>

 */
public class VCardNotificationProvider extends ExtensionElementProvider<VCardNotification> {
    @Override
    public VCardNotification parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        VCardNotification vCardNotification = new VCardNotification();
        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "user":
                            vCardNotification.setUser(parser.nextText());
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

        return vCardNotification;

    }
}
