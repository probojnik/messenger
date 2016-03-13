package tellit.com.tellit.model.contacts;

import tellit.com.tellit.tools.DebugUtil;
import tellit.com.tellit.tools.U;
import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by Stas on 14.09.2015.
 */
public class ContactRosterBean {
    private String jid; // 0 - add, 1 - remove
    private RosterAction action;
    private String name;

    public ContactRosterBean(String jid, RosterAction action, String name) {
//        DebugUtil.assertTrue(jid != null, "jid is NULL");
        this.jid = jid;
        this.action = action;
        this.name = name;
//        TraceHelper.print(jid, action);
    }

    public String getJid() {
        return jid;
    }

    public RosterAction getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return U.overrideToString(getClass(), jid, action);
    }

    public static enum  RosterAction{
        ADD(0), REMOVE(1);
        int value;

        RosterAction(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
