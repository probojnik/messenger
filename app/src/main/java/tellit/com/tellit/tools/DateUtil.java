package tellit.com.tellit.tools;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Stas on 04.09.2015.
 */
public class DateUtil {
    public final static String GTC = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public final static String UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static String SQLITE = "yyyy-MM-dd HH:mm:ss";
    public final static String LOG = "MM-dd HH:mm:ss.S";

    @NonNull
    public static Date parse(@NonNull String date, @NonNull String dateTemplate) {
        if(!TextUtils.isEmpty(date) && !TextUtils.isEmpty(dateTemplate)) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(dateTemplate, Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new Date();
    }

    @NonNull
    public static String format(long date, @NonNull String dateTemplate){
        if(date >= 0 && !TextUtils.isEmpty(dateTemplate)) {
            DateFormat dateFormat = new SimpleDateFormat(dateTemplate, Locale.US);
            return dateFormat.format(date);
        }
        return "Date is empty";
    }

}
