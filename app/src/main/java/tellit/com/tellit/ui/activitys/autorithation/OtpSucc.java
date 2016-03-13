package tellit.com.tellit.ui.activitys.autorithation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tellit.com.tellit.Injector;
import tellit.com.tellit.R;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.service.XmppService;
import tellit.com.tellit.ui.activitys.BaseActivity;

/**
 * Created by probojnik on 22.07.15.
 */
public class OtpSucc extends BaseActivity {
    @InjectView(R.id.otp_succ_btn)
    Button otpSuccBtn;
    @Inject
    UserData userData;

    boolean isNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.tellit_otp_succ);
        ButterKnife.inject(this);
        getSupportActionBar().setTitle("Successful");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        otpSuccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNext = true;
                Intent intent = new Intent(OtpSucc.this, Profile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isNext) {
            startService(new Intent(this,
                    XmppService.class).putExtra(XmppService.COMMAND, XmppService.STOP));
        }
    }
}
