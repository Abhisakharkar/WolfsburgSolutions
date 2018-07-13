package com.example.abhishek.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.google.android.gms.auth.api.Auth;

import org.json.JSONException;
import org.json.JSONObject;

public class VerificationActivity extends AppCompatActivity {

    private EditText codeEdittext;
    private Button verifyBtn;

    private int code;
    private String mail, password;

    private Context context;
    private Authentication authentication;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        context = this;
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authentication = new Authentication(context);

        Toast.makeText(context, "Please verify your email !", Toast.LENGTH_SHORT).show();

        codeEdittext = (EditText) findViewById(R.id.verification_activity_code_edittext_id);
        verifyBtn = (Button) findViewById(R.id.verification_activity_verify_btn_id);


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mail = sharedPreferences.getString("mail", "");
                //password = sharedPreferences.getString("password", "");
                int code = 0;
                code = Integer.parseInt(codeEdittext.getText().toString());
                if (code != 0) {

                    authentication.sendVerificationCode(String.valueOf(code));

                } else {
                    Toast.makeText(context, "Enter verification code !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                //process the response
                try {

                    boolean isDataFilled = sharedPreferences.getBoolean("isDataFilled", false);
                    //editor.putInt("retailerId", retailerData.getInt("retailerId"));
                    boolean isVerified = responseJSONObject.getBoolean("verificationStatus");
                    editor.putBoolean("isVerified", isVerified);
                    editor.commit();
                    if (isVerified) {
                        if (isDataFilled) {
                            startActivity(new Intent(VerificationActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(VerificationActivity.this, ProfileActivity.class));
                            finish();
                        }
                    }else {
                        Toast.makeText(context, "Wrong code", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseErrorReceive(String msg) {

            }
        });
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

    private void updateUI(boolean isNetworkAbailable) {
        if (!isNetworkAbailable) {
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        } else {
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
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    //connected
                    updateUI(true);
                } else {
                    //not connected
                    updateUI(false);
                }
            }
        }
    };
}
