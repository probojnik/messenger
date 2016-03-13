package tellit.com.tellit.ui.activitys;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.controller.operations.profile.SaveAva;
import tellit.com.tellit.model.UserData;


public class BaseActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Inject
    UserData userData;
    @Inject
    SaveAva saveAva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

    }
    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {


            if (requestCode == 1 || requestCode == 2) {

                Intent intent = new Intent(this, CropImageActivity.class);

                if (data.hasExtra("data")) {
                    Bundle extras = data.getExtras();
                    Bitmap photo = extras.getParcelable("data");
                    intent.putExtra("data", photo);
                } else if (data.getData() != null) {
                    intent.setData(data.getData());
                }

//                intent.putExtra("return-data", true);

                startActivityForResult(intent, 3);
            }

//            if (requestCode == 3) {
//                saveAva(data);
//            }
        }



    }

    public void showProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setCustomView(progressBar);
            }
        });
    }

    public void hideProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setCustomView(null);

            }
        });
    }

    private void saveAva(Intent data) {
        if (data.hasExtra("data")) {
            Bundle extras = data.getExtras();
            Bitmap photo = extras.getParcelable("data");
            saveAva.run(photo);
        }
        else if(data.getData()!=null){
            saveAva.run(data.getData());
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        EventBus.getDefault().post(this);
        return super.onSupportNavigateUp();
    }

    public MyApplication getApp(){
        return (MyApplication) getApplication();
    }
}

