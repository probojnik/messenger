package tellit.com.tellit.tools;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.jivesoftware.smack.util.SHA1;
import org.jivesoftware.smack.util.StringUtils;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Author: valentine.beregovoy
 * Date: 12/3/12
 * Time: 3:21 PM
 */
public class SerialUtils {
    public static String getPhoneSerial(Context context) {
        final TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceID = telephony.getDeviceId();
        String simSerialNumber = telephony.getSimSerialNumber();
        String secureString = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (deviceID == null) deviceID = new String();
        if (simSerialNumber == null) simSerialNumber = new String();
        if (secureString == null) secureString = new String();

        return new UUID(secureString.hashCode(), ((long)deviceID.hashCode() << 32) | simSerialNumber.hashCode()).toString();
//        return  deviceID;
    }


}
