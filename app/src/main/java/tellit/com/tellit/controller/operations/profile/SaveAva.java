package tellit.com.tellit.controller.operations.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;


import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.controller.MultiThreadController;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;

/**
 * Created by probojnik on 24.07.15.
 */
@Module(library = true)
public class SaveAva {
    File f;
    final String FILENAME ="my_ava.jpg";
    @Inject
    UserData userData;
    @Inject
    MyApplication myApplication;
    @Inject
    VCardModule vCard;

    @Provides
    @Singleton
    SaveAva providerSaveAva(){
        return new SaveAva();
    }



    public void run(Uri data) { // todo
        Injector.inject(this);


        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Void doInBackground(Object... params) {
                    final Uri data = (Uri) params[0];
                    vCard.getContactByJid(userData.getMyJid(), new VCardModule.ContactDataCallback() {
                        @Override
                        public void result(ContactData contactData) {
                            contactData.setPhoto_uri(data.toString());
                            try {
                                HelperFactory.getInstans().getDao(ContactData.class).update(contactData);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    userData.setAva(data.toString());


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                EventBus.getDefault().post(new SaveAvaResult(FILENAME));

            }
        } ;
        MultiThreadController.getInstance().execute(asyncTask, data);

    }
    public void run(Bitmap photo) {
        Injector.inject(this);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    File f_out = new File(myApplication.getApplicationContext().getFilesDir() + File.separator + FILENAME);
                    ((Bitmap)params[0]).compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(f_out));
                    vCard.getContactByJid(userData.getMyJid(), new VCardModule.ContactDataCallback() {
                        @Override
                        public void result(ContactData contactData) {
                            contactData.setPhoto_uri("file://" + myApplication.getApplicationContext().getFilesDir() + File.separator + FILENAME);
                            try {
                                HelperFactory.getInstans().getDao(ContactData.class).update(contactData);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    userData.setAva("file://" + myApplication.getApplicationContext().getFilesDir() + File.separator + FILENAME);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                EventBus.getDefault().post(new SaveAvaResult(FILENAME));

            }
        } ;
        MultiThreadController.getInstance().execute(asyncTask, photo);

    }


    public void run(byte[] bytes) {


        Injector.inject(this);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d("SaveAva", "");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File(myApplication.getApplicationContext().getFilesDir()+File.separator+FILENAME));
                    fos.write((byte[]) objects[0]);
                    fos.close();
                    vCard.getContactByJid(userData.getMyJid(), new VCardModule.ContactDataCallback() {
                        @Override
                        public void result(ContactData contactData) {
                            contactData.setPhoto_uri("file://" + myApplication.getApplicationContext().getFilesDir() + File.separator + FILENAME);
                            try {
                                HelperFactory.getInstans().getDao(ContactData.class).update(contactData);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    userData.setAva("file://" + myApplication.getApplicationContext().getFilesDir() + File.separator + FILENAME);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                EventBus.getDefault().post(new SaveAvaResult(FILENAME));

            }
        };
        if(bytes != null && bytes.length>0)
            MultiThreadController.getInstance().execute(asyncTask,bytes);

    }

    public void run(byte[] bytes, ContactData contactData) {

        Injector.inject(this);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d("SaveAva", "");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(new File(myApplication.getApplicationContext().getFilesDir()+File.separator+FILENAME));
                    fos.write((byte[]) objects[0]);
                    fos.close();
                    vCard.getContactByJid(userData.getMyJid(), new VCardModule.ContactDataCallback() {
                        @Override
                        public void result(ContactData contactData) {
                            contactData.setPhoto_uri("file://" + myApplication.getApplicationContext().getFilesDir() + File.separator + FILENAME);
                            try {
                                HelperFactory.getInstans().getDao(ContactData.class).update(contactData);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    userData.setAva("file://" + myApplication.getApplicationContext().getFilesDir() + File.separator + FILENAME);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                EventBus.getDefault().post(new SaveAvaResult(FILENAME));

            }
        };
        if(bytes != null && bytes.length>0)
            MultiThreadController.getInstance().execute(asyncTask,bytes);

    }




    public final class SaveAvaResult {
        String file;

        public SaveAvaResult(String s) {
            file = s;
        }

        public String getFile() {
            return file;
        }
    }
}
