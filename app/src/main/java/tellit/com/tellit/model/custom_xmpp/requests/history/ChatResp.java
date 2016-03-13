package tellit.com.tellit.model.custom_xmpp.requests.history;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.tools.TextUtil;

/**
 * Created by Stas on 02.09.2015.
 */
public class ChatResp extends IQ {
    public static final String ELEMENT = "list", NAMESPACE = "urn:xmpp:archive";
    private List<ChatData> list = new ArrayList<>();

    public ChatResp() {
        super(ELEMENT, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }

    public boolean add(ChatData item) {
        int location = list.indexOf(item);
        if(location > 0){
            ChatData temp = list.get(location);
            temp.setHistorySession(item.getFirstHistorySession());
            list.set(location, temp);
            return false;
        }else {
            return list.add(item);
        }
    }

    public List<ChatData> getList() {
        return list;
    }

    @Override
    public String toString() {
        return TextUtil.join(" & ", list);
    }
}
