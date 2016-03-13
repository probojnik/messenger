package tellit.com.tellit.model.contacts;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import tellit.com.tellit.model.user_creation.PhonesNormaliseReq;
import tellit.com.tellit.tools.CRUD;
import tellit.com.tellit.tools.MyCursorJoiner;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by Stas on 10.09.2015.
 */
public class ContactCompareBean implements Comparable<ContactCompareBean> {
    public MyCursorJoiner.Result side;
    public int androidID;
    public int tellitID;
    public String name;
    public String number;
    public String photoUri;
    private String oldJID;

    public ContactCompareBean(MyCursorJoiner.Result side, int androidID, int tellitID, String name, String number, String photoUri, String oldJID) {
        this.side = side;
        this.androidID = androidID; // _id in SQLite
        this.tellitID = tellitID;
        this.name = name;
        this.number = number;
        this.photoUri = photoUri;
        this.oldJID = oldJID;
        TraceHelper.print(side, androidID, tellitID, name, number, photoUri);
    }

    public String getOldJID() {
        if (side != MyCursorJoiner.Result.LEFT) new IllegalStateException("Only for LEFT side, current side is: "+side);
        return oldJID;
    }



    @Override
    public int compareTo(@NonNull ContactCompareBean another) {
        TraceHelper.print(number, another.number);
        if (androidID == another.androidID) { //  && U.valEquals(name, another.name) && U.valEquals(number, another.number)
            return 0;
        } else {
            return androidID > another.androidID ? 1 : -1;
        }
    }

    @Override
    public String toString() {
        return U.overrideToString(getClass(), androidID, tellitID, name, number, photoUri);
    }
}