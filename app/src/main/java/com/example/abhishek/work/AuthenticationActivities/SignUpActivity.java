package com.example.abhishek.work.AuthenticationActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.work.ProfileActivity;
import com.example.abhishek.work.R;
import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //UI components
    private EditText email_edittext, password_edittext, confirm_password_edittext;
    private Button signUpBtn, signInLinkBtn;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

        sharedPreferences = getApplicationContext().getSharedPreferences("userdata",MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_up_btn_id) {

            final String email = "" + email_edittext.getText().toString();
            final String password = "" + password_edittext.getText().toString();
            String confirmPassword = "" + confirm_password_edittext.getText().toString();

            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(confirmPassword)) {

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    if (TextUtils.equals(password, confirmPassword)) {

                        final Authentication authentication = new Authentication(SignUpActivity.this);

                        //check in permanent
                        authentication.checkInPermanent(email,password);

                        //server response listener
                        authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
                            @Override
                            public void onResponseReceive(JSONObject responseJSONObject) {

                                try{
                                    String response_from = responseJSONObject.getString("response_from");
                                    if (response_from.equals("check_in")) {
                                        boolean result = responseJSONObject.getBoolean("result");
                                        if (result) {
                                            //email exists
                                            //tell user that his email exists and go to login
                                        } else {
                                            //email does not exist
                                            //it checks in temp database and sends result
                                            //process result of temporary database

                                            boolean tempResult = responseJSONObject.getBoolean("temp_result");
                                            if (tempResult) {
                                                //email exist in temp database
                                                //tell user to go to login
                                            } else {
                                                //email does not exits
                                                //this is new user
                                                //sign up this new account
                                                authentication.signUp(email,password);
                                            }
                                        }
                                    }else if (response_from.equals("signup")){
                                        boolean result = responseJSONObject.getBoolean("result");
                                        if (result){
                                            //signup successfull
                                        }else {
                                            //signup failed
                                        }
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
















                                // Deprecated

                                try {
                                    boolean result = responseJSONObject.getBoolean("result");
                                    if (result) {
                                        //sign up successfull

                                        //save data to SharedPref
                                        editor.putString("email",email);
                                        editor.putString("password",password);
                                        editor.commit();



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
