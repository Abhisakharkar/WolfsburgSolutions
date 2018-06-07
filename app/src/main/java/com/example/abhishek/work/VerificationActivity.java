package com.example.abhishek.work;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private String mail,password;

    private Context context;
    private Authentication authentication;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        context = this;
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata",MODE_PRIVATE);
        authentication = new Authentication(context);

        Toast.makeText(context, "Please verify your email !", Toast.LENGTH_SHORT).show();

        codeEdittext = (EditText) findViewById(R.id.verification_activity_code_edittext_id);
        verifyBtn = (Button) findViewById(R.id.verification_activity_verify_btn_id);

        mail = sharedPreferences.getString("mail","");
        password = sharedPreferences.getString("password","");

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeEdittext.getText().toString() != null || !codeEdittext.getText().toString().isEmpty()) {
                    if (!mail.isEmpty() && !password.isEmpty()) {
                        code = Integer.parseInt(codeEdittext.getText().toString());
                        authentication.sendVerificationCode(code, mail, password);
                    }else {
                        Toast.makeText(context, "Please Sign In !", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Enter verification code !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                //process the response
                try {
                    boolean result = responseJSONObject.getBoolean("result");
                    if (result){
                        Intent intent = new Intent(VerificationActivity.this,HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else {
                        Toast.makeText(context, "Wrong Code !\nTry Again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
