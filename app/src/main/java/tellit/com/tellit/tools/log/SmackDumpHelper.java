package tellit.com.tellit.tools.log;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stas on 04.09.2015.
 */
public class SmackDumpHelper {
    public static Map<String, String> loopAttribute(XmlPullParser parser){
        Map<String, String> result = new HashMap<>();
        int count = parser.getAttributeCount();
        for(int i = 0; i < count; ++i){
            result.put(parser.getAttributeName(i), parser.getAttributeValue(i));
        }
        return result;
    }
}
