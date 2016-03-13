package tellit.com.tellit.model;

import java.util.Date;

/**
 * Created by Stas on 07.09.2015.
 */
public interface IChatMessage {
    public String getJid();
    public void set_id(int _id);
    public int get_id();
    public Date getDate();
}
