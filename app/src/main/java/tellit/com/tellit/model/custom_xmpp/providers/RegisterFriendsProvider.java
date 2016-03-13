package tellit.com.tellit.model.custom_xmpp.providers;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.model.custom_xmpp.requests.users.RegisterFriendsReq;

/**
 * Created by probojnik on 25.09.15.
 *
 *  * Created by probojnik on 25.09.15.
 * <query xmlns='custom:iq:registerFriends'/>
 ответ прийдет в виде
 <query xmlns="custom:iq:registerFriends">
 <username>User</username>
 <username>User2</username>
 ....
 </query>
 *
 *
 */
public class RegisterFriendsProvider extends IQProvider<RegisterFriendsReq> {
    @Override
    public RegisterFriendsReq parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        RegisterFriendsReq users = new RegisterFriendsReq();

        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "username":
                            users.getUsers().add(parser.nextText());
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

        return users;
    }
}
