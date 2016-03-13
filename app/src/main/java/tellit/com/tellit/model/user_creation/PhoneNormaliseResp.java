package tellit.com.tellit.model.user_creation;

import java.util.List;
import java.util.Set;

import tellit.com.tellit.tools.TextUtil;
import tellit.com.tellit.tools.U;

/**
 * deprecate
 * Created by probojnik on 6/17/15.
 * {"phones":[{"number":"+380662909889","type":"MOBILE"},{"number":"+380505678789","type":"MOBILE"},{"number":"+18185749510","type":"FIXED_LINE_OR_MOBILE"},
 * {"number":"+298323456","type":"FIXED_LINE"},{"number":"+380567322536","type":"FIXED_LINE"}]}
 *
 *
 * {"phones":[
 {"uuid":"b43809f76bf0a008eda51866aa8cbf30","id":"0"}
 ,...,
 {"uuid":"a49537e1038d748a4cf313f89f195ef8","id":"1"}
 ]}
 */
public class PhoneNormaliseResp {
    private Set<Phone> phones;

    public Set<Phone> getPhones() {
        return phones;
    }

    public static class Phone {
        private String id;
        private String uuid;

        public int getId() {
            return Integer.parseInt(id);
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;

            if (o == this)
                return true;

            if (o instanceof Phone){
                Phone other = (Phone) o;
                if(uuid.equals(other.uuid)) return true;
            }

            return false;
        }

        // equal objects must have equal hash codes.
        @Override
        public int hashCode() {
            return uuid.hashCode();
        }

        @Override
        public String toString() {
            return U.overrideToString(getClass(), id, uuid);
        }
    }

    @Override
    public String toString() {
        return TextUtil.join(phones);
    }
}