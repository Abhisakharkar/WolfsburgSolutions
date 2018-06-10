package com.example.abhishek.work;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
                    if (email.isEmpty()) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            authentication.checkEmailExists(email);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Please enter correct email !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Enter email !", Toast.LENGTH_SHORT).show();
                    }
                }else {
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
                    }

                    //response : sign_up
                    if (responseFrom.equals("sign_up")){
                        boolean signUpSuccessStatus = responseJSONObject.getBoolean("signUpSuccessStatus");
                        boolean mailSent = responseJSONObject.getBoolean("mailSent");

                        if (signUpSuccessStatus){
                            //signup successfull
                            startActivity(new Intent(SignUpActivity.this,VerificationActivity.class));
                            finish();
                        }else {
                            Toast.makeText(SignUpActivity.this, "Error in Signing Up !\nTry again later.", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                try {
                    Log.e("signUp response", responseJSONObject.toString());
                    String response_from = responseJSONObject.getString("response_from");
                    if (response_from.equals("check_in")) {
                        Log.e("response_signupActivity", "check_in : true");
                        boolean result = responseJSONObject.getBoolean("result");
                        if (result) {
                            //email exists
                            //tell user that his email exists and go to login
                            Toast.makeText(SignUpActivity.this, "Email Already Registered !", Toast.LENGTH_SHORT).show();
                        } else {
                            //email does not exist
                            //it checks in temp database and sends result
                            //process result of temporary database

                            boolean tempResult = responseJSONObject.getBoolean("temp_result");
                            if (tempResult) {
                                //email exist in temp database
                                //tell user to go to login
                                Toast.makeText(SignUpActivity.this, "Email Already Registered !", Toast.LENGTH_SHORT).show();
                            } else {
                                //email does not exits
                                //this is new user
                                //sign up this new account
                                authentication.signUp(email, password);
                            }
                        }
                    } else if (response_from.equals("signup")) {
                        boolean result = responseJSONObject.getBoolean("result");
                        if (result) {
                            //signup successfull
                            //call to verification script
                            //tell user to verify
                            //send user to verification activity


                            //calling to verification script
                            authentication.verifyEmail(email);

                            editor.putString("mail", email);
                            editor.putString("password", password);
                            Log.e("email : password",email + " : "+ password);
                            int retailerID = responseJSONObject.getInt("retailerID");
                            editor.putInt("retailerID",retailerID);
                            editor.commit();

                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setMessage("Verification code is sent to email.\nPlease verify your account.");
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(SignUpActivity.this, VerificationActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                            builder.setCancelable(false);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } else {
                            //signup failed
                            Toast.makeText(SignUpActivity.this, "Error in Sign Up ! try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                */

                // Deprecated
                                /*
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
                                */

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
}
