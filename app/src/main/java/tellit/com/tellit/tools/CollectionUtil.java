package tellit.com.tellit.tools;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Stas on 10.09.2015.
 */
public class CollectionUtil {
    public static boolean isEmpty(Object[] arr){
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(Collection set){
        return set == null || set.isEmpty();
    }
}
