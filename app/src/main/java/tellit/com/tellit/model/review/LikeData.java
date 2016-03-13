package tellit.com.tellit.model.review;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import tellit.com.tellit.model.CreateDateObject;

/**
 * Created by probojnik on 23.06.15.
 <feedback>
     <id>1</id>
     <reviewId>2</reviewId>
     <fromJID>user1</fromJID>
     <vote>1</vote>
     <state>0</state>
     <createDate>1050</createDate>
     <updateDate>12</updateDate>
 </feedback>
 */
@DatabaseTable(tableName = "like")
public class LikeData  implements Serializable,CreateDateObject {
    @DatabaseField(generatedId = true)
    int _id;
    @DatabaseField(unique = true)
    int id;
    @DatabaseField
    String fromJID;
    @DatabaseField
    int vote,state;
    @DatabaseField
    Date createDate,updateDate;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private ReviewData reviewId;

    public int get_id() {
        return _id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromJID() {
        return fromJID;
    }

    public void setFromJID(String fromJID) {
        this.fromJID = fromJID;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
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

    public ReviewData getReviewId() {
        return reviewId;
    }

    public void setReviewId(ReviewData reviewId) {
        this.reviewId = reviewId;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof LikeData)
            return id == ((LikeData)o).id;
        else
            return false;
    }


    public void update(LikeData likeData) {
        fromJID = likeData.getFromJID();
        vote = likeData.getVote();
        state = likeData.getState();
        reviewId = likeData.getReviewId();
    }
}
