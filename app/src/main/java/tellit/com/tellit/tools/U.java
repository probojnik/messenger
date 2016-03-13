package tellit.com.tellit.tools;

import android.app.Activity;
import android.content.Context;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;

import tellit.com.tellit.BuildConfig;
import tellit.com.tellit.R;

/**
 * Created by Stas on 10.08.2015.
 */
public class U {

    @Nullable
    public static String getMetaHash(String out){
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] arr = md.digest(out.getBytes());
            result = Base64.encodeToString(arr, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Возвращает ложь (удобство), для подстановки в ретурн вызываемого метода.
     */
    public static boolean e(String CSN, String methodName, Throwable e, Object msg) {
        String message = TextUtil.substring(String.valueOf(msg), C.LOG_MAXLENGTH);
        if(!isDebug()) CSN = "com.aroglyph.app:"+CSN;
        String text = "";
        if(!TextUtil.isEmpty(methodName)) text = text.concat(methodName).concat(">");
        if(e!=null && !TextUtil.isEmpty(e.getMessage())) text = text.concat(" ").concat(e.getMessage());
        if(!TextUtil.isEmpty(message)) text = text.concat(" ").concat(message);
        Log.e(CSN, text, e);

        if(C.DEBUG_SHOW_EXCEPTIONS) e.printStackTrace();
        return false;
    }

    public static void log(String tag, String msg, int type) {
        switch (type){
            case Log.VERBOSE:
                if(isDebug())
                    Log.v(tag, msg);
                break;
            case Log.DEBUG:
                if(isDebug())
                    Log.d(tag, msg);
                break;
            case Log.INFO:
                if(isDebug())
                    Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.ASSERT:
            default:
                Log.wtf(tag, msg);
                break;
        }
    }



    public static boolean isDebug(){
        return BuildConfig.DEBUG;
    }

    public static String myDateFormat(Context ctx, long lastTime){
        long currTime = System.currentTimeMillis();
        float elapsedTime = currTime - lastTime;

        /**
         * Если прошло больше 1 месяца
         */
        if(elapsedTime / C.MONTH_MS > 1) {
            return String.format(ctx.getString(R.string.myDateFormat_month), (int)elapsedTime / C.MONTH_MS);
        }

        /**
         * Если прошло больше 1 недели
         */
        if(elapsedTime / C.WEAK_MS > 1) {
            return String.format(ctx.getString(R.string.myDateFormat_weeks), (int)elapsedTime / C.WEAK_MS);
        }

        /**
         * Если прошло больше 1 дня
         */
        if(elapsedTime / C.DAY_MS > 1) {
            return String.format(ctx.getString(R.string.myDateFormat_days), (int)elapsedTime / C.DAY_MS);
        }

        /**
         * Если прошло больше 1 часа
         */
        if(elapsedTime / C.HOUR_MS > 1) {
            return String.format(ctx.getString(R.string.myDateFormat_hour), (int)elapsedTime / C.HOUR_MS);
        }

        /**
         * Если прошло больше 1 минуты
         */
        if(elapsedTime / C.MINUTE_MS > 1) {
            return String.format(ctx.getString(R.string.myDateFormat_min), (int)elapsedTime / C.MINUTE_MS);
        }

        /**
         * Если прошло больше 1 секунды
         */
        if(elapsedTime / C.SECOND_MS > 1) {
            return String.format(ctx.getString(R.string.myDateFormat_seconds), (int)elapsedTime / C.SECOND_MS);
        }
        return "Now";
    }


    public static final boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

    /**
     * Если целочисленная версия SDK приложения равна или больше версии {@code minVersion}
     * {@code android.os.Build.VERSION_CODES.LOLLIPOP}
     */
    public static boolean targetApi(int version){
        return Build.VERSION.SDK_INT >= version;
    }

    public static void adjustPan(Activity act){
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public static void hideKeyboard(Activity act){
        View view = act.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String overrideToString(Class clazz, Object... params){
        return overrideToString(" * ", clazz, params);
    }

    public static String overrideToString(String separator, Class clazz, Object... params) {
        return clazz.getSimpleName()+'['+TextUtil.join(separator, params)+']';
//        return Normalizer.normalize(string, Normalizer.Form.NFD);
    }

    public static Toast toast(Context ctx, int resId) { // ApplicationContext
        return toast(ctx, ctx.getString(resId));
    }
    public static Toast toast(Context ctx, CharSequence text) { // ApplicationContext
        return U.toast(ctx, text, Toast.LENGTH_SHORT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    public static Toast toast(Context ctx, CharSequence text, int duration, int gravity) { // ApplicationContext
        Toast toast = Toast.makeText(ctx, text, duration);
        toast.setGravity(gravity, Gravity.NO_GRAVITY, Gravity.NO_GRAVITY);
        toast.show();
        return toast;
    }

    public static int dp2Px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int px2Dp(int px) {
        int dp = (int) (px / Resources.getSystem().getDisplayMetrics().density);
        return dp;
    }

}