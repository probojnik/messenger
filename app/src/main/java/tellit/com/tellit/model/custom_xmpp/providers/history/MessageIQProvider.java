package tellit.com.tellit.model.custom_xmpp.providers.history;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;

import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.custom_xmpp.requests.history.MessageResp;
import tellit.com.tellit.tools.C;
import tellit.com.tellit.tools.DateUtil;
import tellit.com.tellit.tools.log.SmackDumpHelper;
import tellit.com.tellit.tools.log.TraceHelper;

public class MessageIQProvider extends IQProvider<MessageResp> {
    public static final String ELEMENT = "chat";

    /**
     * Перебирает XML без идентификатора конца.
     * <iq type="result" id="uMG2t-13" to="+380936927881@tellit/ffffffff-f5c0-23ef-ffff-ffffa414c9f6">
     * <chat xmlns="urn:xmpp:archive" with="+380637498915@tellit" start="2015-09-03T09:44:08.760Z">
     * <from secs="0">
     * <body> Ttt</body>
     * </from>
     * <from secs="3">
     * <body>Yyy</body>
     * </from>
     * <from secs="6">
     * <body>Iii</body>
     * </from>
     * <from secs="15">
     * <body>F</body>
     * </from>
     * <set xmlns="http://jabber.org/protocol/rsm">
     * <first index="0">0</first>
     * <last>3</last>
     * <count>4</count>
     * </set>
     * </chat>
     * </iq>
     */
    @Override
    public MessageResp parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        MessageResp conversationListResp = new MessageResp();

        String with = null;
        Date start = null;
        boolean bodyState = false;
//        Map<Long, Bean> messages = new TreeMap<Long, Bean>();

        MessageData messageData = null;

        outerloop:
        while (true) {
            int eventType = parser.getEventType();
//            TraceHelper.print(XmlPullParser.TYPES[eventType], parser.getName(), parser.getText(), SmackDumpHelper.loopAttribute(parser));
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("chat".equals(parser.getName())) {
                        with = parser.getAttributeValue(null, "with");
                        start = DateUtil.parse((String) parser.getAttributeValue(null, "start"), DateUtil.UTC);
                    }
                    if ("to".equals(parser.getName()) || "from".equals(parser.getName())) {
                        String secs = parser.getAttributeValue(null, "secs");
                        messageData = new MessageData();
                        messageData.setJid(with);
                        if ("to".equals(parser.getName())) {
                            messageData.setMy(true);
                        }
                        Date date = new Date(start.getTime() + (Integer.parseInt(secs) * C.SECOND_MS));
                        messageData.setCreateDate(date);
//                        TraceHelper.print("secs", date, secs, start);
                    }
                    if ("body".equals(parser.getName())) {
                        bodyState = true;
                    }
                    break;
                case XmlPullParser.TEXT:
                    if (bodyState && messageData != null) {
//                        TraceHelper.print(parser.getText());
                        messageData.setBody(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("to".equals(parser.getName()) || "from".equals(parser.getName())) {
                        if (messageData != null) {
                            conversationListResp.add(messageData);
                        }
                        if ("body".equals(parser.getName())) {
                            bodyState = false;
                        }
                        messageData = null;
                    }

                    if (parser.getDepth() == initialDepth) {
                        break outerloop;
                    }
                    break;
            }
            eventType = parser.next();
        }

        return conversationListResp;
    }
}
