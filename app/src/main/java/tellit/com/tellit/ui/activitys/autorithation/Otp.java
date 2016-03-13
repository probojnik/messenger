package tellit.com.tellit.ui.activitys.autorithation;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tellit.com.tellit.Injector;
import tellit.com.tellit.MyApplication;
import tellit.com.tellit.R;
import tellit.com.tellit.model.UserData;
import tellit.com.tellit.model.user_creation.LoginPasswAPIFacade;
import tellit.com.tellit.model.user_creation.UserPassw;
import tellit.com.tellit.service.XmppService;
import tellit.com.tellit.ui.activitys.BaseActivity;

/**
 * Created by probojnik on 22.07.15.
 * gsdfgsdgs
 */
public class Otp extends BaseActivity {
    public static final String BROADCAST_ACTION ="android.provider.Telephony.SMS_RECEIVED";
    @InjectView(R.id.textView3)
    TextView textView3;
    @InjectView(R.id.textView4)
    TextView textView4;
    @InjectView(R.id.im_otp_d1)
    EditText imOtpD1;
    @InjectView(R.id.im_otp_d2)
    EditText imOtpD2;
    @InjectView(R.id.im_otp_d3)
    EditText imOtpD3;
    @InjectView(R.id.im_otp_d4)
    EditText imOtpD4;
    @InjectView(R.id.im_otp_d5)
    EditText imOtpD5;
    @InjectView(R.id.im_otp_d6)
    EditText imOtpD6;
    @InjectView(R.id.im_otp_btn)
    Button imOtpBtn;
    private BroadcastReceiver broadcastReceiver;
    HashMap<Integer,EditText> editTextHashMap = new HashMap<>();
    @Inject
    UserData userData;

    String secretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tellit_otp);
        ButterKnife.inject(this);
        Injector.inject(this);
        secretKey = getIntent().getStringExtra("secretKey");
        getSupportActionBar().setTitle("Otp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imOtpD1.requestFocus();
        imOtpD1.setNextFocusLeftId(R.id.im_otp_d2);
        imOtpD2.setNextFocusLeftId(R.id.im_otp_d3);
        imOtpD3.setNextFocusLeftId(R.id.im_otp_d4);
        imOtpD4.setNextFocusLeftId(R.id.im_otp_d5);
        imOtpD5.setNextFocusLeftId(R.id.im_otp_d6);
        imOtpD6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == EditorInfo.IME_ACTION_DONE) || (i == KeyEvent.KEYCODE_ENTER)) {
                    checkOTP();
                    return true;
                }
                return false;
            }
        });
        imOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOTP();
            }
        });

        imOtpD1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imOtpD2.requestFocus();
                    checkOTP();
                }
            }
        });

        imOtpD2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imOtpD3.requestFocus();
                    checkOTP();
                }
            }
        });
        imOtpD3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imOtpD4.requestFocus();
                    checkOTP();
                }
            }
        });
        imOtpD4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imOtpD5.requestFocus();
                    checkOTP();
                }
            }
        });
        imOtpD5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    imOtpD6.requestFocus();
                    checkOTP();
                }
            }
        });
        imOtpD6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                   checkOTP();
                }
            }
        });


        editTextHashMap.put(0, imOtpD1);
        editTextHashMap.put(1,imOtpD2);
        editTextHashMap.put(2, imOtpD3);
        editTextHashMap.put(3, imOtpD4);
        editTextHashMap.put(4, imOtpD5);
        editTextHashMap.put(5, imOtpD6);



        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null &&
                        BROADCAST_ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
                    Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
                    SmsMessage[] messages = new SmsMessage[pduArray.length];
                    for (int i = 0; i < pduArray.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
                    }
                    String sms_from = messages[0].getDisplayOriginatingAddress();
                    if (sms_from.equalsIgnoreCase("+13309462824")) {
                        StringBuilder bodyText = new StringBuilder();
                        for (int i = 0; i < messages.length; i++) {
                            bodyText.append(messages[i].getMessageBody());
                        }
                        String body = bodyText.toString();
                        for(int i=0;i<body.length();i++){
                            editTextHashMap.get(i).setText(""+body.charAt(i));
                        }

                        abortBroadcast();
                    }
                }
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        this.registerReceiver(broadcastReceiver, intFilt);


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

    private void checkOTP(){
        for(int i=0; i< 6;i++)
           if(editTextHashMap.get(i).getText().toString().length() == 0)return;

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Wait");
        pd.setMessage("Validating otp");
        pd.setCancelable(false);
        pd.show();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< 6;i++)
            sb.append(editTextHashMap.get(i).getText().toString());

        LoginPasswAPIFacade.getInstance().getService().getPassword(secretKey, sb.toString(), new Callback<UserPassw>() {
            @Override
            public void success(UserPassw userPassw, Response response) {

                pd.dismiss();

                try {

                    String user = userPassw.getUser();
                    String pwd = userPassw.getPwd();
                    System.out.println(user + " - " + pwd);

                    userData.setMyLogin(user);
                    userData.setUserPassword(pwd);

                    Otp.this.startService(new Intent(Otp.this,
                            XmppService.class).putExtra(XmppService.COMMAND, XmppService.CONNECT));
                    Otp.this.startService(new Intent(Otp.this,
                            XmppService.class).putExtra(XmppService.COMMAND, XmppService.SYNC));
                    Intent intent = new Intent(Otp.this, OtpSucc.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Otp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                Toast.makeText(Otp.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
    }




}
