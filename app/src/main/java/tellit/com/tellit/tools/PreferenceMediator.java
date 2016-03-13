package tellit.com.tellit.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * Created by Stas on 10.08.2015.
 */
public class PreferenceMediator {
    private final static String CSN = PreferenceMediator.class.getSimpleName();
    private final static String NAME = "settings";
    private final static int MODE = Context.MODE_PRIVATE;

    public static String getStringPref(Context ctx, String key, String def) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.getString(key, def);
    }

    public static float getFloatPref(Context ctx, String key, float def) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.getFloat(key, def);
    }

    public static long getLongPref(Context ctx, String key, long def) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.getLong(key, def); //-1l
    }

    public static int getIntPref(Context ctx, String key, int def) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.getInt(key, def); // -1
    }

    public static boolean getBooleanPref(Context ctx, String key, boolean def) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.getBoolean(key, def); // false
    }

    public static boolean checkBooleanPref(Context ctx, String key) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.getBoolean(key, false) && setBooleanPref(ctx, key, false);
    }

    public static boolean checkBooleanPrefDelete(Context ctx, String key, boolean def) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.getBoolean(key, def) && deletePref(ctx, key);
    }

    public static Set<String> getStringSetPref(Context ctx, String key, Set<String> def) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.getStringSet(key, def); //
    }

    public static boolean setBooleanPref(Context ctx, String key, Boolean data) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, data);
        return editor.commit();
    }

    public static void setStringPref(Context ctx, String key, String data){
        setStringPref(ctx, key, data, true);
    }

    public static boolean setStringPref(Context ctx, String key, String data, boolean async) {
        boolean result = false;
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, data);
        if(async) editor.apply();
        else result = editor.commit();
        return result;
    }

    public static void setIntPref(Context ctx, String key, int data) {
        setIntPref(ctx, key, data, true);
    }

    public static boolean setIntPref(Context ctx, String key, int data, boolean async) {
        boolean result = false;
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, data);
        if(async) editor.apply();
        else result = editor.commit();
        return result;
    }

    public static boolean setFloatPref(Context ctx, String key, float data) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, data);
        return editor.commit();
    }

    public static boolean setLongPref(Context ctx, String key, long data) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, data);
        return editor.commit();
    }

    public static boolean setStringSetPref(Context ctx, String key, Set<String> data) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(key, data);
        return editor.commit();
    }

    public static boolean deletePref(Context ctx, String key) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        boolean result = editor.commit();
        return result;
    }

    public static boolean checkPref(Context ctx, String key) {
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        return settings.contains(key);
    }

    public static boolean checkDeletePref(Context ctx, String key) {
        return checkPref(ctx, key) && deletePref(ctx, key);
    }

    public static boolean checkSwitchPref(Context ctx, String key, boolean assertVal, boolean switchIfMissing){
        if(getBooleanPref(ctx, key, switchIfMissing?assertVal:!assertVal) == assertVal){
            setBooleanPref(ctx, key, !assertVal);
            return true;
        }
        return false;
    }

    public static boolean clear(Context ctx){
        SharedPreferences settings = ctx.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        return editor.commit();
    }

    public static Map<String, ?> getAll(Context ctx){
        return ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE).getAll();
    }
}