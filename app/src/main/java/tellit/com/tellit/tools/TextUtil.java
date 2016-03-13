package tellit.com.tellit.tools;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Stas on 14.08.2015.
 */
public class TextUtil {

    public static String join(@NonNull CharSequence delimiter, @Nullable Object[] tokens) {
        return tokens != null ? TextUtils.join(delimiter, tokens) : "";
    }

    public static String join(@NonNull CharSequence delimiter, @Nullable Iterable tokens) {
        return tokens != null ? TextUtils.join(delimiter, tokens) : "";
    }

    public static String join(@Nullable Collection tokens) {
        return CollectionUtil.isEmpty(tokens) ? "Empty" : join(", ", tokens); // Arrays.toString(tokens);
    }

    /**
     * Use for log
     */
    public static String join(@Nullable Object[] tokens) {
        return isEmpty(tokens) ? "Empty" : join(", ", tokens); // Arrays.toString(tokens);
    }

    public static String join(CharSequence delimiter, CharSequence token, int length) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (int i = 1; i <= length; i++) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0 || str.toString().toLowerCase().equals("null");
    }

    public static boolean isEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(Set set) {
        return set == null || set.isEmpty();
    }

    /**
     * Возвращает под-строку если length > 0
     * возвращает строку если length = 0
     * возвращает пустую строку если length < 0
     */
    @NonNull
    public static String substring(@Nullable CharSequence src, int length) {
        if (!isEmpty(src) && length > 0 && length <= src.length()) {
            return TextUtils.substring(src, 0, length);
        } else if (length == 0) {
            return String.valueOf(src);
        } else
            return "";
    }

    /**
     * Возвращает часть строки или строку из параметров
     */
    public static String substring(@NonNull String src, @NonNull String cut) {
        int position = indexOf(src, cut);
        if(position < 0) position = 0;
        return substring(src, position);
    }

    public static boolean contains(@NonNull String src, @NonNull CharSequence soughtFor) {
        if(isEmpty(src) || isEmpty(soughtFor)) return false;
        return src.indexOf(soughtFor.toString()) >= 0;
    }

    /**
     * Возвращает первый индекс или -1
     */
    private static int indexOf(@Nullable String src, @Nullable String cut) {
        return (src == null || cut == null) ? -1 : src.indexOf(cut);
    }

}