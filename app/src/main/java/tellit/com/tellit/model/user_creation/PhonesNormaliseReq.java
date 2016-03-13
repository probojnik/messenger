package tellit.com.tellit.model.user_creation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import tellit.com.tellit.model.contacts.ContactComparable;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.tools.U;

/**
 * Created by probojnik on 6/17/15.
 *
 * {"country": "country code", phones":[{"number": “1212”, “id”:”232”}, ...]}

 */
public class PhonesNormaliseReq {
    String country;
    List<Phone> phones;

    public PhonesNormaliseReq(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<ContactData> param) {
        phones = new ArrayList<>();
        for(ContactData src: param){
            String phone = getValidNumber(src.getNumber());
            if(phone.length() > 0) {
                Phone temp = new Phone(src.getId(), phone);
                phones.add(temp);
            }
        }
    }

    public static class Phone {
        String id; // request params
        String number; // request params

        public Phone(int id, String number) {
            this.id = String.valueOf(id);
            this.number = number;
        }

        @Override
        public String toString() {
            return U.overrideToString(getClass(), id, number);
        }
    }

    public String getValidNumber(String number) {

        boolean plus = number.startsWith("+");
        number =(plus)? "+"+ number.replaceAll("\\D",""):number.replaceAll("\\D","") ;
        if(Pattern.matches("\\+?\\d{10,}", number))
            return number;

        return "";
    }
}
