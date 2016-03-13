package tellit.com.tellit.tools;

import android.annotation.TargetApi;
import android.database.Cursor;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Stas on 09.09.2015.
 */
public class IOUtil {
    public static void closeQuietly(AutoCloseable c) { // TODO AutoCloseable since API level 19
        if (c == null) return;
        try {
            if(U.targetApi(19)) {
                close(c);
            }else{
                /**
                 * Грабли! Интерфейс Cursor расширен интерфейсом Closeable только с 16 версии.
                 */
                if(!U.targetApi(16) && c instanceof Cursor){
                    ((Cursor) c).close();
                } else{
                    close((Closeable) c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(19)
    private static void close(AutoCloseable c) throws Exception {
        c.close();
    }
    private static void close(Closeable c) throws IOException {
        c.close();
    }


}
