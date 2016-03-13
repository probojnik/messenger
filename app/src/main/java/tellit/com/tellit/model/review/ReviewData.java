package tellit.com.tellit.model.review;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;

import tellit.com.tellit.Injector;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by probojnik on 23.06.15.
 <msg>
     <id>1</id>
     <fromJID>root</fromJID>
     <toJID>to</toJID>
     <msg>cool</msg>
     <rate>2</rate>
     <state>1</state>
     <createDate>1050</createDate>
     <updateDate>2</updateDate>
     <rating>20</rating>
 </msg>
 */
@DatabaseTable(tableName = "review")
public class ReviewData implements Serializable{
    @DatabaseField(generatedId = true)
    int _id;
    @DatabaseField(unique = true)
    long id;
    @DatabaseField
    String fromJID;
    @DatabaseField
    String toJID;
    @DatabaseField
    String msg;

    @DatabaseField
    int rate;
    @DatabaseField
    float rating;
    @DatabaseField
    int state;
    @DatabaseField
    Date createDate, updateDate;
    @ForeignCollectionField
    Collection<LikeData> likeList = new ArrayList<>();

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    ContactData contacID;

    @Inject
    transient UserData userData;

    public ReviewData() {
        Injector.inject(this);
    }

    public void update(ReviewData reviewData) {
       rating = reviewData.getRating();
        state = reviewData.getState();
    }

    @Override
    public boolean equals(Object o) {
        return id == ((ReviewData)o).getId();
    }

    public String getFromJID() {
        return fromJID;
    }

    public void setFromJID(String fromJID) {
        this.fromJID = fromJID;
    }

    public String getToJID() {
        return toJID;
    }

    public void setToJID(String toJID) {
        this.toJID = toJID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<LikeData> getLikeList() {
        return likeList;
    }

    public void setLikeList(Collection<LikeData> likeList) {
        this.likeList = likeList;
    }

    public ContactData getContacID() {
        return contacID;
    }

    public void setContacID(ContactData contacID) {
        this.contacID = contacID;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LikeData getMyLike(){
        for(LikeData likeData : likeList){
            if(userData.getMyJid().equals(likeData.getFromJID())){
                return likeData;
            }
        }
        return null;
    }
}
