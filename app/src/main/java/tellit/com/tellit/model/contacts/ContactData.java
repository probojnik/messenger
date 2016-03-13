package tellit.com.tellit.model.contacts;

import android.support.annotation.Nullable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.model.review.ReviewData;
import tellit.com.tellit.tools.U;


/**
 * Created by probojnik on 10.06.15.
 */
@DatabaseTable(tableName = "contacts")
public class ContactData implements Serializable {

    @DatabaseField(generatedId = true)
    int _id;
    @DatabaseField
    int id = -1; // Androids ID
    @DatabaseField(unique = true, index = true)
    String jid; // +380501234567@tellit
    @DatabaseField
    String uuid; // +380501234567
    @DatabaseField(index = true)
    String name;
    @DatabaseField
    boolean isValid = false;
    @DatabaseField
    String photo_uri="";
    @DatabaseField
    boolean isInstalls = false; //пользователь в системе или нет
    @ForeignCollectionField
    Collection<ReviewData> reviewDataList = new ArrayList<>();
    @DatabaseField
    boolean favorite = false; // отображать в фаворитах
    @DatabaseField
    float rate = 0; // rate
    @DatabaseField
    int reviewNumber; // кол-во теллитов(на основании котор построен рейтинг)
    @DatabaseField
    String lastName;
    @DatabaseField
    String firsName;
    @DatabaseField
    String number;

    public ContactData() {
    }


    @Nullable
    public String getMetaHash(){
        return U.getMetaHash(name+number+photo_uri);
    }

    public int getId() {
        return id;
    }

    public void setId(int androidID) {
        this.id = androidID;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public int get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public ContactData setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isInstalls() {
        return isInstalls;
    }

    public void setIsInstalls(boolean isInstalls) {
        this.isInstalls = isInstalls;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public static Dao<ContactData,Integer> getDao(){
        return HelperFactory.getInstans().getDao(ContactData.class);
    }

    @Override
    public String toString() {
        return U.overrideToString(getClass(), _id, id, jid, uuid, name, isValid, photo_uri, isInstalls, favorite, rate, lastName, firsName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirsName(String firsName) {
        this.firsName = firsName;
    }

    public String getFirsName() {
        return firsName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public int getReviewNumber() {
        return reviewNumber;
    }

    public void setReviewNumber(int reviewNumber) {
        this.reviewNumber = reviewNumber;
    }


    @Override
    public boolean equals(Object o) {
        if(id > 0)
            return id == ((ContactData)o).getId();
        return false;
    }
}