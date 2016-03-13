package tellit.com.tellit.model.custom_xmpp.providers.history;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.custom_xmpp.requests.history.ChatResp;
import tellit.com.tellit.tools.DateUtil;
import tellit.com.tellit.tools.TextUtil;

public class ChatIQProvider extends IQProvider<ChatResp> {
    public static final String ELEMENT = "list";
    /**
     * Перебирает XML без идентификатора конца.
     <iq type="result" id="MA7fH-94" to="+380936927881@tellit/ffffffff-f5c0-23ef-ffff-ffffa414c9f6">
     <list xmlns="urn:xmpp:archive">
     <chat with="tellit" start="2015-09-03T08:56:48.609Z"/>
     <chat with="+380787878787@tellit" start="2015-09-03T09:38:52.633Z"/>
     <chat with="+380501111111@tellit" start="2015-09-03T09:38:59.135Z"/>
     <chat with="+380633205034@tellit" start="2015-09-03T09:39:11.361Z"/>
     <chat with="+380635716703@tellit" start="2015-09-03T09:39:29.667Z"/>
     <chat with="+380637498915@tellit" start="2015-09-03T09:44:08.760Z"/>
     <chat with="+380633205034@tellit" start="2015-09-03T14:36:20.735Z"/>
     <set xmlns="http://jabber.org/protocol/rsm">
     <first index="0">3</first>
     <last>181</last>
     <count>7</count>
     </set>
     </list>
     </iq>
     *
     * Отдает массив номеров.
     *
     */
    @Override
    public ChatResp parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ChatResp chatResp = new ChatResp();

        outerloop:
        while (true) {
            int eventType = parser.next();
//            TraceHelper.print(XmlPullParser.TYPES[eventType], parser.getName(), parser.getText());
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("chat".equals(parser.getName())) {
                        String jid = parser.getAttributeValue(null, "with");
                        String time = parser.getAttributeValue(null, "start");

                        String date = (String) parser.getAttributeValue(null, "start");
                        if(TextUtil.contains(jid, "@")){
                            ChatData chatData = new ChatData();
                            chatData.setJid(jid);
                            chatData.setType(Message.Type.chat.toString());
                            chatData.setDate(DateUtil.parse(date, DateUtil.UTC));
                            chatData.setHistorySession(time);
                            chatResp.add(chatData);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
                    break;
            }
        }

        return chatResp;
    }
}
