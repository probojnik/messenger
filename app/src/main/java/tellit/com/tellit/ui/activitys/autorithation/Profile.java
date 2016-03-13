package tellit.com.tellit.ui.activitys.autorithation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.service.XmppService;
import tellit.com.tellit.ui.activitys.BaseActivity;
import tellit.com.tellit.ui.activitys.Tellit;

/**
 * Created by probojnik on 22.07.15.
 */
public class Profile extends BaseActivity {
    @Inject
    AbstractXMPPConnection connection;
    @Inject
    UserData userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tellit_profile_act);
        Injector.inject(this);
        if(!connection.isConnected()) {
            startService(new Intent(this,
                    XmppService.class).putExtra(XmppService.COMMAND, XmppService.CONNECT));
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!userData.getIS_PROFILE()) {
            startService(new Intent(this,
                    XmppService.class).putExtra(XmppService.COMMAND, XmppService.STOP));
        }
    }
}
