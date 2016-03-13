package tellit.com.tellit.model.chat;

import android.text.format.DateFormat;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import tellit.com.tellit.model.CreateDateObject;
import tellit.com.tellit.model.IChatMessage;
import tellit.com.tellit.tools.C;
import tellit.com.tellit.tools.DateUtil;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by probojnik on 11.06.15.
 * chat,groupchat
 */
@DatabaseTable(tableName = "messages")
public class MessageData implements CreateDateObject, IChatMessage {
    public enum MessageStatus {NEW, SEND, DELIVERED, READ}

    @DatabaseField(generatedId = true)
    int _id;
    @DatabaseField
    String jid;
    @DatabaseField
    String fromJid; //for multichat
    @DatabaseField(unique = true)
    String id;
    @DatabaseField
    String body;
    @DatabaseField
    boolean my = false;
    @DatabaseField
    int like;
    @DatabaseField
    Date createDate;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private ChatData chat;
    @DatabaseField
    String status = null;
    @DatabaseField
    String type = "chat";

    public void update(MessageData upd_mess) {
        jid = upd_mess.getJid();
        id = upd_mess.getId();
        body = upd_mess.getBody();
        status = upd_mess.getStatus();
        createDate = upd_mess.getCreateDate();
    }


    @Override
    public boolean equals(Object o) {
        if (this.id == null) return false;
        if (o instanceof MessageData)
            return this.id.equals(((MessageData) o).id);
        else
            return false;
    }

    @Override
    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public Date getDate() {
        return createDate;
    }

    @Override
    public String getFromJID() {
        return fromJid;
    }

    public CharSequence getCreateDateSimple() {
        return DateFormat.format(DateUtil.SQLITE, createDate.getTime()); // hh:mm
    }

    public void setCreateDate(Date createDate) {
//        TraceHelper.print("setCreateDate", createDate, jid);
        this.createDate = createDate;
    }

    @Override
    public void set_id(int _id) {
        this._id = _id;
    }

    @Override
    public int get_id() {
        return _id;
    }

    public ChatData getChat() {
        return chat;
    }

    public void setChat(ChatData chat) {
        this.chat = chat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMy() {
        return my;
    }

    public void setMy(boolean my) {
        this.my = my;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void setFromJid(String fromJid) {
        this.fromJid = fromJid;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return U.overrideToString(getClass(), _id, jid, fromJid, id, body, my, like, createDate, status, type);
    }
}
