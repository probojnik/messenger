package tellit.com.tellit.model.custom_xmpp.providers.muc;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.custom_xmpp.requests.muc.MucHistoryResp;
import tellit.com.tellit.ui.activitys.autorithation.Login;

/**
 * Created by probojnik on 11.09.15.
<query xmlns="custom:muc:history">
 <item>
 <nickName>564aa3c3688760153a4c400998d40234</nickName>
 <body>888</body>
 <msgUUID>e4fa7af4-d750-4147-bd00-81a9aa692f85</msgUUID>
 <vote>1</vote>
 <logTime>1441892455201</logTime>
 </item>
 ....
 </query
 */

public class MucHistoryProvider extends IQProvider<MucHistoryResp> {
    @Override
    public MucHistoryResp parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        MucHistoryResp mucHistoryResp = new MucHistoryResp();
        List<MucHistoryResp.Message> messageList = new ArrayList<>();

        String name = null;
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name){
                        case "item":
                            parseItem(parser, messageList);
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
        mucHistoryResp.setMessageList(messageList);

        return mucHistoryResp;
    }

    private void parseItem(XmlPullParser parser, List<MucHistoryResp.Message> messageList) throws IOException, XmlPullParserException {
        final int initialDepth = parser.getDepth();
        String name = null;
        MucHistoryResp.Message message = new MucHistoryResp.Message();
        outerloop: while (true) {
            int eventType = parser.next();
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name) {
                        case "nickName":
                            message.setNickName(parser.nextText());
                            break;
                        case "body":
                            message.setBody(parser.nextText());
                            break;
                        case "msgUUID":
                            message.setMsgUUID(parser.nextText());
                            break;
                        case "vote":
                            message.setVote(Integer.parseInt(parser.nextText()));
                            break;
                        case "logTime":
                            message.setDate(new Date(Long.parseLong(parser.nextText())));
                            break;

                        default:
                            break;
                    }
                case XmlPullParser.END_TAG:
                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
                    break;
                default:
                    break;
            }
        }
        messageList.add(message);
    }
}
