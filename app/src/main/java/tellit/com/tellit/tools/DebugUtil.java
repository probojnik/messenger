package tellit.com.tellit.tools;

/**
 * Created by Stas on 10.09.2015.
 */
public class DebugUtil {
    /**
     * Выбрасывает исключения в режиме отладки
     */
    public static boolean exception(String detailMessage){
        if(U.isDebug()) {
            RuntimeException ex = new RuntimeException(detailMessage);
            throw ex;
        }
        return false;
    }

    public static boolean exceptionNotImplemented(){
        return exception("not implemented");
    }

    public static boolean assertTrue(boolean condition, String detailMessage) {
        return !condition && exception(detailMessage);
    }

    public static boolean assertFalse(boolean condition, String detailMessage) {
        return condition && exception(detailMessage);
    }
}
