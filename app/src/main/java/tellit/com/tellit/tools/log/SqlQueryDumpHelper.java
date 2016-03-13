package tellit.com.tellit.tools.log;

import android.content.ContentValues;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Map.Entry;

import tellit.com.tellit.tools.C;

public class SqlQueryDumpHelper {

    public static String query(Uri uri, String[] columns, String selection,
                               String[] selectionArgs, String orderBy) {
        String table = uri.toString();
        return query(table, columns, selection, selectionArgs, null, null, orderBy, null, null);
    }

    public static String query(String table, String[] columns,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String orderBy) {
        return query(table, columns, selection, selectionArgs, groupBy, having, orderBy, null, null);
    }

    public static String query(String table, String[] columns,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String orderBy, String limit) {
        return query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, null);
    }

    public static String query(String table, String[] columns,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String orderBy, String limit,
                               CancellationSignal cancellationSignal) {

        String result = null;
        try {
            final String sql = SQLiteQueryBuilder.buildQueryString(false,
                    table, columns, selection, groupBy, having, orderBy, limit);
            result = "query : " + sql + C.NEXT_LINE + "args : " + Arrays.toString(selectionArgs);
        } catch (Exception e) {
            result = DumpHelper.dump(e);
        }

        return result;
    }

    public static String delete(Uri uri, String selection, String[] selectionArgs) {
        String table = uri.toString();
        return delete(table, selection, selectionArgs);
    }

    public static String delete(String table, String whereClause, String[] whereArgs) {
        final String sql = "DELETE FROM "
                + table
                + (!TextUtils.isEmpty(whereClause) ? " WHERE " + whereClause
                : "");
        return "query : " + sql + C.NEXT_LINE + "args:" + Arrays.toString(whereArgs);
    }

    public static String update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table = uri.toString();
        return update(table, values, selection, selectionArgs);
    }

    public static String update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        StringBuilder sql = new StringBuilder(120);
        sql.append("UPDATE ");
        sql.append(table);
        sql.append(" SET ");
        int setValuesSize = values.size();
        int bindArgsSize = (whereArgs == null) ? setValuesSize
                : (setValuesSize + whereArgs.length);
        Object[] bindArgs = new Object[bindArgsSize];
        int i = 0;

        for (Entry<String, Object> entry : values.valueSet()) {
            sql.append((i > 0) ? "," : "");
            sql.append(entry.getKey());
            bindArgs[i++] = entry.getValue();
            sql.append("=?");
        }

        if (whereArgs != null) {
            for (i = setValuesSize; i < bindArgsSize; i++) {
                bindArgs[i] = whereArgs[i - setValuesSize];
            }
        }

        if (!TextUtils.isEmpty(whereClause)) {
            sql.append(" WHERE ");
            sql.append(whereClause);
        }

        return "query : " + sql + C.NEXT_LINE + "args : " + Arrays.toString(bindArgs);
    }

    public static String insert(Uri uri, ContentValues values) {
        String table = uri.toString();
        return insert(table, null, values);
    }

    public static String insert(String table, String nullColumnHack, ContentValues initialValues) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT");
        sql.append(" INTO ");
        sql.append(table);
        sql.append('(');

        Object[] bindArgs = null;
        int size = (initialValues != null && initialValues.size() > 0) ? initialValues
                .size() : 0;
        if (size > 0) {
            bindArgs = new Object[size];
            int i = 0;
            for (Entry<String, Object> entry : initialValues.valueSet()) {
                sql.append((i > 0) ? "," : "");
                sql.append(entry.getKey());
                bindArgs[i++] = entry.getValue();
            }
            sql.append(')');
            sql.append(" VALUES (");
            for (i = 0; i < size; i++) {
                sql.append((i > 0) ? ",?" : "?");
            }
        } else {
            sql.append(nullColumnHack + ") VALUES (NULL");
        }
        sql.append(')');

        return "query : " + sql + C.NEXT_LINE + "args : " + Arrays.toString(bindArgs);
    }

    public static String execSQL(String sql, Object[] bindArgs) {
        return "query : " + sql + C.NEXT_LINE + "args : " + Arrays.toString(bindArgs);
    }
}
