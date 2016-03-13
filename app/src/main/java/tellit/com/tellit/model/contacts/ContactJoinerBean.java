package tellit.com.tellit.model.contacts;

import tellit.com.tellit.tools.CRUD;
import tellit.com.tellit.tools.DebugUtil;
import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.U;

/**
 * Created by Stas on 14.09.2015.
 */
public class ContactJoinerBean {
    private CRUD crud;
    private int tellitID;
    public int androidID;
    public String name;
    public String number;
    public String photoUri;

    private String oldNumber; // old used for compare before send to roster and to normalization
    private String oldJID; // only for UPDATE state

    private String jid;

    // for UPDATE
    public ContactJoinerBean(int tellitID, int androidID, String name, String number, String photoUri, String oldNumber, String oldJID) {
        this(CRUD.UPDATE, tellitID, androidID, name, number, photoUri);
        this.oldNumber = oldNumber;
        this.oldJID = oldJID;
    }

    // for CREATE DELETE
    public ContactJoinerBean(CRUD crud, int tellitID, int androidID, String name, String number, String photoUri) {
        this.crud = crud;
        this.tellitID = tellitID;
        this.androidID = androidID;
        this.name = name;
        this.number = number;
        this.photoUri = photoUri;
    }

    public void setTellitID(int tellitID) {
        this.tellitID = tellitID;
    }

    public int getTellitID() {
        return getTellitID(true);
    }

    public int getTellitID(boolean check) {
        if(check) DebugUtil.assertTrue(tellitID >= 0, "tellitID >= 0");
        return tellitID;
    }

    public String getOldJID() {
        if (crud != CRUD.UPDATE) new IllegalStateException("Only for UPDATE state, current state is: "+crud);
        return oldJID;
    }

    public void setOldJID(String oldJID) {
        if (crud != CRUD.UPDATE) new IllegalStateException("Only for UPDATE state, current state is: "+crud);
        this.oldJID = oldJID;
    }

    public CRUD getCrud() {
        return crud;
    }

    public boolean isPhoneChanged(){
        if (crud != CRUD.UPDATE) new IllegalStateException("Only for UPDATE state, current state is: "+crud);
        return !U.valEquals(number, oldNumber);
    }

    @Override
    public String toString() {
        return U.overrideToString(getClass(), crud, tellitID, androidID, name, number, oldNumber);
    }
}
