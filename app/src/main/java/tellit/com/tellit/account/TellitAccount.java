package tellit.com.tellit.account;

import android.accounts.Account;

/**
 * Created by probojnik on 01.08.15.
 */
public class TellitAccount extends Account {

    public static final String TYPE = "com.tellit.com";


    public static final String TOKEN_FULL_ACCESS = "com.tellit.com.TOKEN_FULL_ACCESS";


    public static final String KEY_PASSWORD = "com.tellit.com.KEY_PASSWORD";

    public TellitAccount(String name) {
        super(name, TYPE);
    }
}
