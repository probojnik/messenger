package tellit.com.tellit.model.custom_xmpp.providers;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.custom_xmpp.requests.users.UnknownUser;

/**
 * Created by probojnik on 07.09.15.
 *
 * <query xmlns="custom:iq:unknownUser">
 <phoneMask>+380******959</phoneMask>
 </query>

 <query xmlns="custom:iq:unknownUser">
 <phone>+380******959</phone>
 </query>

 */
public class UnknownUserProvider extends IQProvider<UnknownUser> {
    @Override
    public UnknownUser parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        UnknownUser unknownUser = new UnknownUser();

        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "phone":
                            unknownUser.setPhoneMask(parser.nextText());
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


        return unknownUser;
    }
}
