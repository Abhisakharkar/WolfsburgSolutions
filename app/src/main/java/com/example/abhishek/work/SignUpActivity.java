package com.example.abhishek.work;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    //UI components
    private EditText email_edittext, password_edittext, confirm_password_edittext;
    private Button signUpBtn, signInLinkBtn;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Authentication authentication;
    private ServerResponse serverResponse;

    private String email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_edittext = (EditText) findViewById(R.id.email_edittext_signup_id);
        password_edittext = (EditText) findViewById(R.id.password_edittext_signup_id);
        confirm_password_edittext = (EditText) findViewById(R.id.confirm_password_edittext_signup_id);
        signUpBtn = (Button) findViewById(R.id.sign_up_btn_id);
        signInLinkBtn = (Button) findViewById(R.id.sign_in_link_btn);

        signUpBtn.setClickable(false);
        signUpBtn.setOnClickListener(this);
        signInLinkBtn.setOnClickListener(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        //email focus listener
        email_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    email = email_edittext.getText().toString();
                    if (!email.isEmpty()) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            authentication.checkEmailExists(email);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Please enter correct email !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Enter email !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    signUpBtn.setClickable(false);
                }
            }
        });

        //server response listener
        authentication = new Authentication(SignUpActivity.this);
        serverResponse = authentication.getServerResponseInstance();
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {

                try {
                    String responseFrom = responseJSONObject.getString("responseFrom");

                    //response : check_mail_exist
                    if (responseFrom.equals("check_mail_exist")) {
                        boolean mailExist = responseJSONObject.getBoolean("mailExist");
                        if (mailExist) {
                            Toast.makeText(SignUpActivity.this, "Account already exists !" +
                                    "\n Please Sign In.", Toast.LENGTH_SHORT).show();
                        } else {
                            signUpBtn.setClickable(true);
                        }
                    } else
                        //response : sign_up
                        if (responseFrom.equals("sign_up")) {
                            boolean signUpSuccessStatus = responseJSONObject.getBoolean("signUpSuccessStatus");
                            if (signUpSuccessStatus) {
                                //signup successfull
                                editor.putBoolean("isDataFilled", false);
                                editor.putString("mail", email);
                                editor.putBoolean("isVerified", false);
                                editor.putBoolean("isSignedIn", true);
                                editor.putString("password", password);
                                editor.commit();
                                startActivity(new Intent(SignUpActivity.this, VerificationActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error in Signing Up !\nTry again later.", Toast.LENGTH_SHORT).show();
                            }
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_up_btn_id) {
            password = "" + password_edittext.getText().toString();
            String confirmPassword = "" + confirm_password_edittext.getText().toString();

            if (!TextUtils.isEmpty(password) || !TextUtils.isEmpty(confirmPassword)) {

                if (TextUtils.equals(password, confirmPassword)) {

                    authentication.signUpNew(email, password);

                    //check in permanent
                    //authentication.checkInPermanent(email, password);

                } else {
                    Toast.makeText(this, "passwords are not correct !", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            }


        }

        if (view.getId() == R.id.sign_in_link_btn) {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    private void updateUI(boolean isNetworkAbailable){
        if (!isNetworkAbailable){
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    //connected
                    updateUI(true);
                }else {
                    //not connected
                    updateUI(false);
                }
            }
        }
    };
}
