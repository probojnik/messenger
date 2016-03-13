package tellit.com.tellit.model.custom_xmpp.requests.history;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.tools.TextUtil;

/**
 * Created by Stas on 02.09.2015.
 */
public class MessageResp extends IQ {
    public static final String ELEMENT = "list", NAMESPACE = "urn:xmpp:archive";
    private List<MessageData> list = new ArrayList<>();

    public MessageResp() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

    public boolean add(MessageData item) {
        return list.add(item);
    }

    public List<MessageData> getList() {
        return list;
    }

    @Override
    public String toString() {
        return TextUtil.join("&", list);
    }
}
