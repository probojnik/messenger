package tellit.com.tellit.tools;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stas on 27.08.2015.
 */
public class Clipboard {
    public static boolean intoClipboard(@NonNull Context ctx, @NonNull Iterable<String> iter){
        ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = null;
        for(String str: iter){
            ClipData.Item clipItem = new ClipData.Item(str);
            if(clipData == null){
                clipData = new ClipData("label", new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN}, clipItem);
            } else{
                clipData.addItem(clipItem);
            }
        }
        if(clipData != null){
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                clipboardManager.setPrimaryClip(clipData);
            }
            return true;
        } else {
            return false;
        }
    }

    @NonNull
    public static List<CharSequence> ontoClipboard(@NonNull Context ctx){
        List<CharSequence> result = new LinkedList<>();
        ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboardManager.getPrimaryClip();
        for(int i = 0; i < clipData.getItemCount(); ++i) {
            Log.d("ontoClipboard", clipData.getItemAt(i).toString());
            result.add(clipData.getItemAt(i).getText());
        }
        return result;
    }
}