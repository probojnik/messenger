package tellit.com.tellit.model.custom_xmpp.requests.muc;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by probojnik on 11.09.15.
 * <query xmlns="custom:muc:history">
 <item>
 <nickName>564aa3c3688760153a4c400998d40234</nickName>
 <body>888</body>
 <msgUUID>e4fa7af4-d750-4147-bd00-81a9aa692f85</msgUUID>
 <vote>1</vote>
 <logTime>1441892455201</logTime>
 </item>
 ....
 </query
 *
 *
 */
public class MucHistoryResp extends IQ {

    List<Message> messageList = new ArrayList<>();

    public MucHistoryResp() {
        super(MucHistoryReq.ELEMENT, MucHistoryReq.NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public static class Message{
        String nickName,body,msgUUID;
        int vote;
        Date date;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getMsgUUID() {
            return msgUUID;
        }

        public void setMsgUUID(String msgUUID) {
            this.msgUUID = msgUUID;
        }




        public int getVote() {
            return vote;
        }

        public void setVote(int vote) {
            this.vote = vote;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
