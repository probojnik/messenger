package tellit.com.tellit.model.custom_xmpp;

/**
 * Created by probojnik on 23.06.15.
 */
public interface CustomStanzaCallback<T> {
    public void resultOK(T result);
    public void error(Exception ex);
}
