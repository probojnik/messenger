package tellit.com.tellit.model.contacts;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import tellit.com.tellit.tools.U;

/**
 * Created by Stas on 17.09.2015.
 */
public class ContactComparable {
    public int androidID;
    public int tellitID;
    public String name;
    public String number;
    public String uuid;
    public String photoUri;

    public ContactComparable(int androidID, String name, String number, String photoUri) {
        this.androidID = androidID;
        this.name = name;
        this.number = validatePhone(number);
        this.photoUri = photoUri;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        if (obj instanceof ContactComparable){
            ContactComparable other = (ContactComparable) obj;
            if(number.equals(other.number)) return true;
        }

        return false;
    }

    public String getJid(String serviceName){
        return uuid+"@"+serviceName;
    }

    // equal objects must have equal hash codes.
    @Override
    public int hashCode() {
        return number.hashCode(); // ^ name.hashCode() ^ photoUri.hashCode();
    }

    @Nullable
    public String getMetaHash(){
        return U.getMetaHash(name+number+photoUri);
    }

    private String validatePhone(String number) {
        boolean plus = number.contains("+");
        String cleanNumber = number.replaceAll("\\D", "");
        return plus ? "+" + cleanNumber : cleanNumber;
    }

    public boolean isNormalized(){
        return uuid != null;
    }

    public boolean isValid() {
        return number.replaceAll("\\D", "").length() >= 10;
    }

    @Override
    public String toString() {
        return U.overrideToString(getClass(), androidID, tellitID, name, number, uuid, photoUri);
    }
}