package tellit.com.tellit.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * Created by probojnik on 06.08.15.
 */
public class RoundedTransformationForPicasso implements com.squareup.picasso.Transformation {
    private final int radius;
    private final int margin;  // dp


    // radius is corner radii in dp
    // margin is the board in dp
    public RoundedTransformationForPicasso(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;
    }


    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));


        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);


        if (source != output) {
            source.recycle();
        }


        return output;
    }


    @Override
    public String key() {
        return "rounded";
    }
}