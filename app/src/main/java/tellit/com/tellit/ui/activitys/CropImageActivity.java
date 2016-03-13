package tellit.com.tellit.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.operations.profile.SaveAva;

/**
 * Created by probojnik on 14.09.15.
 */
public class CropImageActivity extends Activity {

    @Inject
    SaveAva saveAva;

    @InjectView(R.id.cropImageView)
    CropImageView cropImageView;
    @InjectView(R.id.crop_button)
    Button cropButton;
    @InjectView(R.id.rotate_button)
    Button rotateButton;
    @InjectView(R.id.rotate_buttonl)
    Button rotateButtonl;
    @InjectView(R.id.imageView)
    ImageView imageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image);
        getApplicationContext().setTheme(R.style.Theme_AppTheme);
        ButterKnife.inject(this);
        Injector.inject(this);
        if (getIntent().hasExtra("data")) {
            Bitmap photo = getIntent().getExtras().getParcelable("data");
            cropImageView.setImageBitmap(photo);

        } else if (getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            Picasso.with(this).load(uri).fit().centerInside().into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    cropImageView.setImageBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                }

                @Override
                public void onError() {
                    Toast.makeText(CropImageActivity.this, "Can't load image", Toast.LENGTH_SHORT).show();
                }
            });
        }

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAva.run(cropImageView.getCroppedBitmap());
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

        rotateButtonl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_270D);
            }
        });
    }


}
