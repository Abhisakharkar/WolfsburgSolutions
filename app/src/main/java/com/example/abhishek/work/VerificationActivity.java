package com.example.abhishek.work;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.google.android.gms.auth.api.Auth;

import org.json.JSONObject;

public class VerificationActivity extends AppCompatActivity {

    private EditText codeEdittext;
    private Button verifyBtn;

    private int code;

    private Context context;
    private Authentication authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        context = this;
        authentication = new Authentication(context);

        codeEdittext = (EditText) findViewById(R.id.verification_activity_code_edittext_id);
        verifyBtn = (Button) findViewById(R.id.verification_activity_verify_btn_id);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeEdittext.getText().toString() != null || !codeEdittext.getText().toString().isEmpty()) {
                    code = Integer.parseInt(codeEdittext.getText().toString());
                    authentication.sendVerificationCode(code);
                }
            }
        });

        authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                //process the response
            }
        });
    }
}
