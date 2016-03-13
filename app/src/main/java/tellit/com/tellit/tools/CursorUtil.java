package tellit.com.tellit.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tellit.com.tellit.tools.log.TraceHelper;

/**
 * Created by Stas on 14.08.2015.
 */
public class CursorUtil {
    public static void printCursorMulti(@Nullable Cursor c, @Nullable StringBuilder appendable){
        try {
            String log = "Count - " + c.getCount() + ", Columns - " + TextUtil.join(" * ", c.getColumnNames());
            if(appendable != null){
                appendable.append(log);
            }else{
                TraceHelper.print(log);
            }

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                printCursorSingle(c, appendable);
            }
        }catch (SQLiteCantOpenDatabaseException | NullPointerException e){
            TraceHelper.error(e);
        }
    }

    public static void printCursorSingle(@NonNull Cursor c, @Nullable StringBuilder appendable){
        StringBuilder cache = new StringBuilder(" <<Position: ");
        cache.append(c.getPosition());
        cache.append(", Values: ");

        for (int idx = 0; idx < c.getColumnCount(); ++idx) {
            String name = c.getColumnName(idx);
            String content = c.getType(idx) == Cursor.FIELD_TYPE_BLOB?"BLOB":c.getString(idx);
            cache.append(name).append(" = ").append(content);
            if (idx < c.getColumnCount() - 1) cache.append("; ");
        }

        cache.append(" >> ");

        if (appendable == null) {
            TraceHelper.print("printCursorSingle", cache.toString());
        }else {
            appendable.append(cache);
        }
    }
}
