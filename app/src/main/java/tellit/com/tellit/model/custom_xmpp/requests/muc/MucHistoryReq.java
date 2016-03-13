package tellit.com.tellit.model.custom_xmpp.requests.muc;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by probojnik on 11.09.15.
 * <query xmlns='custom:muc:history'>
 <room_name>31</room_name>
 <msg_uuid>c6e7afac-d107-48b5-b21d-c9d4713ddb7f</msg_uuid>
 <number>10</number>
 </query>
 *
 */
public class MucHistoryReq extends IQ {
    public static final String ELEMENT = "query",NAMESPACE = "custom:muc:history";
    private String room_name;
    private String msg_uuid;
    private int number;

    public MucHistoryReq() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.append(">");
        xml.append("<room_name>" + room_name + "</room_name>");
        xml.append("<msg_uuid>" + msg_uuid + "</msg_uuid>");
        xml.append("<number>" + number + "</number>");
        return xml;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getMsg_uuid() {
        return msg_uuid;
    }

    public void setMsg_uuid(String msg_uuid) {
        this.msg_uuid = msg_uuid;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
