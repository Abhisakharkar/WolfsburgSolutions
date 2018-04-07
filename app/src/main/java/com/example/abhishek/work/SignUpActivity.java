package com.example.abhishek.work;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.work.DatabaseOperations.Authentication;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //UI components
    private EditText email_edittext, password_edittext, confirm_password_edittext;
    private Button signUpBtn, signInLinkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_edittext = (EditText) findViewById(R.id.email_edittext_signup_id);
        password_edittext = (EditText) findViewById(R.id.password_edittext_signup_id);
        confirm_password_edittext = (EditText) findViewById(R.id.confirm_password_edittext_signup_id);
        signUpBtn = (Button) findViewById(R.id.sign_up_btn_id);
        signInLinkBtn = (Button) findViewById(R.id.sign_in_link_btn);

        signUpBtn.setOnClickListener(this);
        signInLinkBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_up_btn_id) {

            String email = "" + email_edittext.getText().toString();
            String password = "" + password_edittext.getText().toString();
            String confirmPassword = "" + confirm_password_edittext.getText().toString();

            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(confirmPassword)) {

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    if (TextUtils.equals(password, confirmPassword)) {

                        Authentication authentication = new Authentication(SignUpActivity.this);
                        authentication.signUp(email, password);

                        //server response listener
                        authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
                            @Override
                            public void onResponseReceive(JSONObject responseJSONObject) {

                                try {
                                    boolean result = responseJSONObject.getBoolean("result");
                                    if (result) {
                                        //go to profile activity
                                        Intent intent = new Intent(SignUpActivity.this,ProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        //some error occured
                                        //try again to sign up
                                        Toast.makeText(SignUpActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(this, "passwords are not correct !", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "check your email", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            }
        }

        if (view.getId() == R.id.sign_in_link_btn) {
            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
