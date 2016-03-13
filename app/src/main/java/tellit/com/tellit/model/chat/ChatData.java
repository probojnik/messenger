package tellit.com.tellit.model.chat;

import android.support.annotation.Nullable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import tellit.com.tellit.model.IChatMessage;
import tellit.com.tellit.tools.U;

/**
 * Created by probojnik on 10.06.15.
 */
@DatabaseTable(tableName = "chats")
public class ChatData implements IChatMessage {
    @DatabaseField(generatedId = true)
    int _id;
    @DatabaseField(unique = true)
    String jid;
    @DatabaseField
    String name;
    @DatabaseField
    Date date;
    @DatabaseField
    Date last_date;
    @DatabaseField
    String lastMessage;
    @DatabaseField
    String photo_uri = "";
    @DatabaseField(dataType = DataType.SERIALIZABLE, canBeNull = true)
    ArrayList<String> historySessions;
    @ForeignCollectionField(eager = true)
    Collection<MessageData> messageList = new ArrayList<>();
    @DatabaseField
    String type = "chat";
    @DatabaseField
    long count;

//    @DatabaseField(foreign = true, foreignAutoRefresh = true)
//    ContactData contactData;

    public void update(ChatData chatData) {
        this.name = chatData.getName();
        this.last_date = chatData.getLast_date();
        this.lastMessage = chatData.getLastMessage();
    }


    @Override
    public int get_id() {
        return _id;
    }

    @Override
    public void set_id(int _id) {
        this._id = _id;
    }

    @Override
    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    @Override
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHistorySession(String session) {
        if(historySessions == null)
            historySessions = new ArrayList<>();
        this.historySessions.add(session);
    }

    @Nullable
    public String getFirstHistorySession() {
        if(this.historySessions.size()>0){
            return this.historySessions.iterator().next();
        } else {
            return null;
        }
    }

    public List<String> getHistorySessions() {
        return historySessions;
    }

    public Collection<MessageData> getMessageList() {
        return messageList;
    }

    public void setMessageList(Collection<MessageData> messageList) {
        this.messageList = messageList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public Date getLast_date() {
        return last_date;
    }

    public void setLast_date(Date last_date) {
        this.last_date = last_date;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        if (obj instanceof ChatData){
            return hashCode() == obj.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return jid.hashCode();
    }

    @Override
    public String toString() {
        return U.overrideToString(getClass(), _id, jid, name, date, last_date, lastMessage, photo_uri, messageList, type, count);
    }
}
