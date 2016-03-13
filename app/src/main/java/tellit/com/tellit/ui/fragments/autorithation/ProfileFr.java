package tellit.com.tellit.ui.fragments.autorithation;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.controller.operations.profile.SaveAva;
import tellit.com.tellit.controller.operations.profile.SendVCardOperation;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.database.HelperFactory;
import tellit.com.tellit.modules.VCardModule;
import tellit.com.tellit.tools.RoundedTransformationForPicasso;
import tellit.com.tellit.ui.activitys.Tellit;
import tellit.com.tellit.ui.activitys.autorithation.Profile;
import tellit.com.tellit.ui.fragments.BaseFragment;

/**
 * Created by probojnik on 24.07.15.
 */
public class ProfileFr extends BaseFragment {
    @InjectView(R.id.im_profile_img)
    ImageView imProfileImg;
    @InjectView(R.id.textView6)
    TextView textView6;
    @InjectView(R.id.im_profile_first)
    EditText imProfileFirst;
    @InjectView(R.id.im_profile_last)
    EditText imProfileLast;
    @InjectView(R.id.im_profile_btn)
    Button imProfileBtn;
    @Inject
    UserData userData;
    private ProgressDialog pd ;
    @Inject
    AbstractXMPPConnection connection;
    View rootView = null;
    @Inject
    VCardModule vCard;
    private boolean profileIsUpdated;

    public static ProfileFr newInstance(boolean profileIsUpdated){
        ProfileFr fragment = new ProfileFr();
        Bundle bundle = new Bundle();
        bundle.putBoolean("profileIsUpdated", profileIsUpdated);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.profileIsUpdated = getArguments().getBoolean("profileIsUpdated");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tellit_profile, container, false);
        EventBus.getDefault().register(this);

        ButterKnife.inject(this, rootView);
        Injector.inject(this);
        pd = new ProgressDialog(getActivity());

        setAva();

        imProfileBtn.setEnabled(false);
        imProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        imProfileFirst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidName();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imProfileLast.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidName();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_first = imProfileFirst.getText().toString();
                String s_last = imProfileLast.getText().toString();

                if (s_first.length() == 0 || s_last.length() == 0) {
                    Toast.makeText(getActivity(), "Input First Name and Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (connection == null || !connection.isConnected()) {
                    Toast.makeText(getActivity(), "Try later", Toast.LENGTH_SHORT).show();
                    return;
                }

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                userData.setUSER_FIRST_NAME(s_first);
                userData.setUSER_LAST_NAME(s_last);

                pd.setTitle("Wait");
                pd.setMessage(profileIsUpdated?"Profile is updated":"Create profile");
                pd.setCancelable(false);
                pd.show();

                SendVCardOperation.run();
            }
        });
        return rootView;
    }

    private void isValidName(){
        if(imProfileFirst.getText().toString().matches("\\w{3,10}") && imProfileLast.getText().toString().matches("\\w{3,10}"))
            imProfileBtn.setEnabled(true);
        else
            imProfileBtn.setEnabled(false);
    }

    public void onEventMainThread(UserData userData) {
            setAva();
    }
    public void onEventMainThread(SaveAva.SaveAvaResult saveAvaResult) {
            setAva();
    }

    public void onEventMainThread(SendVCardOperation.SendVCardOperationResult result) {
        pd.dismiss();
        if (result.isResult()) {

            if (getActivity() instanceof Profile) {
                userData.setIS_PROFILE(true);
                Intent intent = new Intent(getActivity(), Tellit.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            } else {
                Toast.makeText(getActivity(), "Profile changed", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity(), "Try later.", Toast.LENGTH_SHORT).show();
        }

    }


    private void setAva(){
        if(userData == null || userData.getMyJid().length()==0) return;
        imProfileFirst.setText(userData.getUserFirstName());
        imProfileLast.setText(userData.getUserLastName());
        if(userData.getAva() == null || userData.getAva().length() == 0) return;
        String picturePath = userData.getAva();

        if(picturePath!=null && picturePath.length()>0) {


                Picasso.with(getActivity())
                        .load(Uri.parse(picturePath))
                        .fit().centerCrop()
                        .transform(new RoundedTransformationForPicasso(500,0))
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(imProfileImg);

        }else{
            Picasso.with(getActivity())
                    .load(R.mipmap.ic_launcher)
                    .fit().centerCrop()
                    .transform(new RoundedTransformationForPicasso(500, 0))
                    .into(imProfileImg);
        }
    }



    private void delAva(){
        if(userData == null || userData.getMyJid().length()==0) return;
        if(userData.getAva() == null || userData.getAva().length() == 0) return;

        vCard.getContactByJid(userData.getMyJid(), new VCardModule.ContactDataCallback() {
            @Override
            public void result(ContactData contactData) {
                contactData.setPhoto_uri("");
                try {
                    HelperFactory.getInstans().getDao(ContactData.class).update(contactData);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        userData.setAva("");

        Picasso.with(getActivity())
                .load(R.mipmap.ic_launcher)
                .fit().centerCrop()
                .transform(new RoundedTransformationForPicasso(500, 0))
                .into(imProfileImg);
    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Delete","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Profile photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                Intent intent_crop = new Intent("com.android.camera.action.CROP");
                intent_crop.setType("image/*");
                List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent_crop, 0);
                int size = list.size();


                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

                   // intent.putExtra("crop", "true");

                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("scale", true);


                    intent.putExtra("return-data", true);


                    getActivity().startActivityForResult(intent, 1);


                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
//
//                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);

                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);


                    intent.putExtra("return-data", true);
                    getActivity().startActivityForResult(Intent.createChooser(intent,
                            "Complete action using"), 2);


                } else if (options[item].equals("Delete")) {
                    dialog.dismiss();
                    delAva();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
