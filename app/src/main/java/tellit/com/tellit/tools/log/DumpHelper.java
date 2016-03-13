package tellit.com.tellit.tools.log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import org.jivesoftware.smack.packet.Stanza;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tellit.com.tellit.tools.C;
import tellit.com.tellit.tools.CursorUtil;

public class DumpHelper {

    private static final int SHORT_DUMP_TEXT_MAX_LENGTH = 50;

    public static String simpleDump(Object obj) {
        return String.valueOf(obj);
    }

    public static String join(CharSequence delimiter, @Nullable Object[] tokens) {
        StringBuilder sb = new StringBuilder();
        if(tokens != null && tokens.length > 0){
            boolean firstTime = true;
            for (Object token: tokens) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append(delimiter);
                }
                sb.append(dump(token));
            }
        } else
            sb.append("Empty");
        return sb.toString();
    }

    public static String dump(@Nullable Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Collection<?>) return dump((Collection<?>) obj);
        if (obj instanceof Cursor) return dump((Cursor) obj);
        if (obj instanceof ViewGroup) return dump((ViewGroup) obj);
        if (obj instanceof View) return dump((View) obj);
        if (obj instanceof Object[]) return dump((Object[]) obj);
        if (obj instanceof int[]) return dump((int[]) obj);
        if (obj instanceof URI) return dump((URI) obj);
        if (obj instanceof Map) return dump((Map) obj);
        if (obj instanceof Activity) return dump((Activity) obj);
        if (obj instanceof Intent) return dump((Intent) obj);
        if (obj instanceof Throwable) return dump((Throwable) obj);
        if (obj instanceof Location) return dump((Location) obj);
        if (obj instanceof Stanza) return dump((Stanza) obj);
//        if (U.isInterface(obj, null)) return dump(obj.getClass());
        return obj.toString();
    }

    public static String dump(Stanza stanza) {
        StringBuilder builder = new StringBuilder();
        builder.append("getFrom=").append(stanza.getFrom());
        builder.append(", getTo=").append(stanza.getTo());
        builder.append(", toString=").append(stanza.toString());
        return builder.toString();
    }

    public static String dump(Class clazz) {
        return clazz.getSimpleName()+" "+clazz.getPackage();
    }

    public static String dump(Cursor cursor) {
//        OutputStream os = new StringBuilderOutputStream();
        StringBuilder sb = new StringBuilder();

        try{
//            DatabaseUtils.dumpCurrentRow(cursor, sb);
            CursorUtil.printCursorMulti(cursor, sb);
        } catch (CursorIndexOutOfBoundsException ex){
            ex.printStackTrace();
            sb.append(ex.getMessage());
        }

        return sb.toString();
    }

    public static String dump(@NonNull View view) {
        StringBuilder sb = new StringBuilder();
        sb.append(view.getClass().getSimpleName());
//        sb.append(" ");
//        sb.append(view.getDrawingTime());
//        sb.append(view.getElevation());
//        sb.append(" ");
//        sb.append(view.getWidth());
//        sb.append(" ");
//        sb.append(view.getHeight());
//        sb.append(" ");
//        sb.append(view.getMeasuredWidth());
//        sb.append(" ");
//        sb.append(view.getMeasuredHeight());
        return sb.toString();
    }

    public static String dump(@NonNull ViewGroup viewGroup) {
        StringBuilder sb = new StringBuilder();
        sb.append(viewGroup.getClass().getSimpleName());
        sb.append(":");
        sb.append(viewGroup.getChildCount());
        sb.append("[");
        boolean firstTime = true;
        for (int i = 0; i <= viewGroup.getChildCount() - 1; ++i){
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(", ");
            }

            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup){
                sb.append(dump((ViewGroup) childAt));
            } else {
                sb.append(dump((View) childAt));
            }
        }
        sb.append("]");

        return sb.toString();
    }

    private static String dump(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        sb.append(ex.toString()).append(" ** ");
        for (StackTraceElement stackTraceElement : stackTrace) {
            sb.append(stackTraceElement.toString()).append(" * ");
        }

        Throwable cause = ex.getCause();
        if (cause != null && cause != ex) {
            sb.append("Caused by: ").append(C.NEXT_LINE).append(dump(cause));
        }
        return sb.toString();
    }

    public static String dump(Collection<?> collection) {
        return collection == null ? "null" : dump(collection.toArray());
    }

    public static String dump(Object[] array) {
        return array == null ? "null" : Arrays.toString(array);
    }

    public static String dump(int[] array) {
        return array == null ? "null" : Arrays.toString(array);
    }

    public static String dump(URI uri) {
        if (uri == null) {
            return "null";
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(uri);

        String authority = uri.getAuthority();
        String fragment = uri.getFragment();
        String host = uri.getHost();
        String path = uri.getPath();
        int port = uri.getPort();
        String query = uri.getQuery();
        String encodedAuthority = uri.getRawAuthority();
        String encodedFragment = uri.getRawFragment();
        String encodedPath = uri.getRawPath();
        String encodedQuery = uri.getRawQuery();
        String encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
        String encodedUserInfo = uri.getRawUserInfo();
        String scheme = uri.getScheme();
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        String userInfo = uri.getUserInfo();

        String lastPathSegment = null;
        List<String> pathSegments = null;

        try {
            Uri androidUri = Uri.parse(uri.toString());
            lastPathSegment = androidUri.getLastPathSegment();
            pathSegments = androidUri.getPathSegments();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sb.append(" authority = ").append(authority);
        sb.append(" encodedAuthority = ").append(encodedAuthority);
        sb.append(" encodedFragment = ").append(encodedFragment);
        sb.append(" encodedPath = ").append(encodedPath);
        sb.append(" encodedQuery = ").append(encodedQuery);
        sb.append(" encodedSchemeSpecificPart = ").append(
                encodedSchemeSpecificPart);
        sb.append(" encodedUserInfo = ").append(encodedUserInfo);
        sb.append(" fragment = ").append(fragment);
        sb.append(" host = ").append(host);
        sb.append(" lastPathSegment = ").append(lastPathSegment);
        sb.append(" path = ").append(path);
        sb.append(" pathSegments = ").append(pathSegments);
        sb.append(" port = ").append(port);
        sb.append(" query = ").append(query);
        sb.append(" scheme = ").append(scheme);
        sb.append(" schemeSpecificPart = ").append(schemeSpecificPart);
        sb.append(" userInfo = ").append(userInfo);
        return sb.toString();
    }

    public static String dump(Location location) {
        if (location == null) {
            return "null";
        }

        StringBuilder s = new StringBuilder();
        s.append("[");
        s.append(location.getProvider());
        s.append(String.format(" %.6f,%.6f", location.getLatitude(), location.getLongitude()));
        if (location.hasAccuracy()) s.append(String.format(" acc=%.0f", location.getAccuracy()));
        else s.append(" acc=???");
        if (location.hasAltitude()) s.append(" alt=").append(location.getAltitude());
        if (location.hasSpeed()) s.append(" vel=").append(location.getSpeed());
        if (location.hasBearing()) s.append(" bear=").append(location.getBearing());

        if (location.getExtras() != null) {
            s.append(" {").append(location.getExtras()).append('}');
        }
        s.append(']');
        return s.toString();
    }

    public static String dump(Intent intent) {
        if (intent == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder("Intent [");
        sb.append("Action=").append(intent.getAction());
        sb.append(", Categories=").append(dump(intent.getCategories()));
        sb.append(", Data=").append(dump(intent.getData()));
        sb.append(", Type=").append(intent.getType());
        sb.append(", Flags=0x").append(Integer.toHexString(intent.getFlags()));
        sb.append(", Package=").append(intent.getPackage());
        sb.append(", Component=").append(intent.getComponent() != null ? intent.getComponent().flattenToShortString() : null);
        sb.append(", SourceBounds=").append(intent.getSourceBounds());
        if (Build.VERSION.SDK_INT >= 16) {
            sb.append(", ClipData=").append("" + intent.getClipData());
        }
        sb.append(", Extras=").append(dump(intent.getExtras()));
        if (Build.VERSION.SDK_INT >= 15) {
            sb.append(", Selector=").append(dump(intent.getSelector()));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String dump(Map<?, ?> map) {
        if (map.isEmpty()) {
            return "{}";
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append('{');
        Iterator<? extends Map.Entry<?, ?>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> entry = it.next();
            Object key = entry.getKey();
            if (key != map) {
                buffer.append(key);
            } else {
                buffer.append("(this Map)");
            }
            buffer.append('=');
            Object value = entry.getValue();
            if (value != map) {
                buffer.append(dump(value));
            } else {
                buffer.append("(this Map)");
            }
            if (it.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }


    public static String dump(Activity activity) {
        return activity == null ? "null" : activity.getClass().getSimpleName();
    }

    public static String resName(Context ctx, int resId) {
        try {
            return ctx.getResources().getResourceEntryName(resId); // return app.getResources().getResourceName(resId);
        } catch (Resources.NotFoundException e) {
            e.getStackTrace();
        }
        return "Not Found";
    }

    public static String resName(View view) {
        int viewId = view.getId();

        if (viewId == View.NO_ID) {
            return "No id";
        } else {
            try {
                return view.getResources().getResourceEntryName(viewId);
            } catch (Resources.NotFoundException e) {
                return "Resource not found";
            }
        }
    }
}