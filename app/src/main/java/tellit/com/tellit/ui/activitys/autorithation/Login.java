package tellit.com.tellit.ui.activitys.autorithation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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


/**
 * Created by probojnik on 22.07.15.
 */
public class Login extends AccountAuthentificatorSupportActiviti {
    @InjectView(R.id.textView2)
    TextView textView2;
    @InjectView(R.id.login_spinner)
    Spinner loginSpinner;
    @InjectView(R.id.login_code_txt)
    TextView loginCodeTxt;
    @InjectView(R.id.login_phone_txt)
    EditText loginPhoneTxt;
    @InjectView(R.id.login_btn)
    Button loginBtn;

    public static final String EXTRA_TOKEN_TYPE = "com.tellit.com.EXTRA_TOKEN_TYPE";


    @Inject
    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.tellit_login);
        ButterKnife.inject(this);
        getSupportActionBar().setTitle("Login");
        String [] data =  getResources().getStringArray(R.array.im_country_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        loginSpinner.setAdapter(adapter);
        loginSpinner.setPrompt("Country");
        loginSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userData.setLoginCode(i);

                switch (i) {
                    case 0:
                        loginCodeTxt.setText("+1"); // us
                        break;
                    case 1:
                        loginCodeTxt.setText("+38"); //ua
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loginSpinner.setSelection(userData.getLOGIN_CODE());

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String _login = loginCodeTxt.getText().toString() + loginPhoneTxt.getText().toString();
                if (_login.length() < 10) {
                    Toast.makeText(Login.this, "Enter your phone", Toast.LENGTH_SHORT).show();
                    return;
                }


                new AlertDialog.Builder(Login.this)
                        .setCancelable(false)
                        .setTitle("NUMBER CONFIRMATION")
                        .setMessage(_login + "\n" + "Is your phone number correct?")
//                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                userData.setMyLogin(_login);
                                userData.setLOGIN_PHONE(_login);

//                                bus.post(new NavBuild(Verifying.class).build());
                                final ProgressDialog progressDialog = new ProgressDialog(Login.this);
                                progressDialog.setMessage("verifying "+_login);
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                LoginPasswAPIFacade.getInstance().getService().getOTP(_login, new Callback<String>() {

                                    @Override
                                    public void success(String s, Response response) {
                                        progressDialog.dismiss();

                                        Intent intent = new Intent(Login.this, Otp.class);
                                        intent.putExtra("secretKey",s);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();



            }
        });

    }


}
