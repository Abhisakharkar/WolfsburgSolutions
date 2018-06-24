package com.example.abhishek.work;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    //UI components
    private EditText email_edittext, password_edittext, confirm_password_edittext;
    private Button signUpBtn, signInLinkBtn;

    private FrameLayout mailCheckLayout;
    private ProgressBar mailCheckProgressBar;
    private ImageView doneImageView;
    private View loadingLayout;

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
        mailCheckLayout = (FrameLayout) findViewById(R.id.signup_activity_mail_check_framelayout_id);
        mailCheckProgressBar = (ProgressBar) findViewById(R.id.signup_activity_mail_check_progress_bar_id);
        doneImageView = (ImageView) findViewById(R.id.signup_activity_done_imageview_id);
        loadingLayout = (View) findViewById(R.id.signup_activity_loading_layout);

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
                            mailCheckLayout.setVisibility(View.VISIBLE);
                            mailCheckProgressBar.setVisibility(View.VISIBLE);
                            authentication.checkEmailExists(email);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Please enter correct email !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Enter email !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    signUpBtn.setClickable(false);

                    doneImageView.setVisibility(View.GONE);
                    mailCheckProgressBar.setVisibility(View.GONE);
                    mailCheckLayout.setVisibility(View.GONE);
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                //set progress bar invisible
                                int px = mailCheckProgressBar.getMeasuredWidth() / 2;
                                int py = mailCheckProgressBar.getMeasuredHeight() / 2;
                                int finalRadiusProgressBar = Math.max(mailCheckProgressBar.getWidth(), mailCheckProgressBar.getHeight()) / 2;
                                Animator animProgressBar = ViewAnimationUtils
                                        .createCircularReveal(mailCheckProgressBar, px, py, finalRadiusProgressBar, 0);
                                animProgressBar.setDuration(500);
                                animProgressBar.start();
                                mailCheckProgressBar.setVisibility(View.INVISIBLE);
                                doneImageView.setVisibility(View.INVISIBLE);
                                mailCheckLayout.setVisibility(View.GONE);
                            } else {
                                doneImageView.setVisibility(View.INVISIBLE);
                                mailCheckProgressBar.setVisibility(View.INVISIBLE);
                                mailCheckLayout.setVisibility(View.GONE);
                            }
                        } else {
                            signUpBtn.setClickable(true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                //set progress bar invisible
                                int px = mailCheckProgressBar.getMeasuredWidth() / 2;
                                int py = mailCheckProgressBar.getMeasuredHeight() / 2;
                                int finalRadiusProgressBar = Math.max(mailCheckProgressBar.getWidth(), mailCheckProgressBar.getHeight()) / 2;
                                Animator animProgressBar = ViewAnimationUtils
                                        .createCircularReveal(mailCheckProgressBar, px, py, finalRadiusProgressBar, 0);
                                animProgressBar.setDuration(500);
                                animProgressBar.start();
                                mailCheckProgressBar.setVisibility(View.INVISIBLE);
                                doneImageView.setVisibility(View.INVISIBLE);
                                mailCheckLayout.setVisibility(View.GONE);
                            } else {
                                doneImageView.setVisibility(View.INVISIBLE);
                                mailCheckProgressBar.setVisibility(View.INVISIBLE);
                                mailCheckLayout.setVisibility(View.GONE);
                            }
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
                                hideLoadingProgressbar();
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

            @Override
            public void onResponseErrorReceive(String msg) {
                if (loadingLayout.getVisibility() == View.VISIBLE) {
                    hideLoadingProgressbar();
                }
                //hide mail check progress bar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //set progress bar invisible
                    int px = mailCheckProgressBar.getMeasuredWidth() / 2;
                    int py = mailCheckProgressBar.getMeasuredHeight() / 2;
                    int finalRadiusProgressBar = Math.max(mailCheckProgressBar.getWidth(), mailCheckProgressBar.getHeight()) / 2;
                    Animator animProgressBar = ViewAnimationUtils
                            .createCircularReveal(mailCheckProgressBar, px, py, finalRadiusProgressBar, 0);
                    animProgressBar.setDuration(500);
                    animProgressBar.start();
                    mailCheckProgressBar.setVisibility(View.INVISIBLE);
                    mailCheckLayout.setVisibility(View.GONE);
                } else {
                    mailCheckLayout.setVisibility(View.GONE);
                    doneImageView.setVisibility(View.INVISIBLE);
                    mailCheckProgressBar.setVisibility(View.INVISIBLE);
                }

                Toast.makeText(SignUpActivity.this, "Some Technical Error Occured ! \n Try again later !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoadingProgressbar() {
        View btn = signUpBtn;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int btnX = (btn.getLeft() + btn.getRight()) / 2;
            int btnY = (btn.getTop() + btn.getBottom()) / 2;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            int finalRadius = Math.max(height, width);

            Animator animator = ViewAnimationUtils.createCircularReveal(loadingLayout, btnX, btnY, 0, finalRadius);
            animator.setDuration(300);
            loadingLayout.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            loadingLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoadingProgressbar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            int finalRadius = Math.max(height, width);

            Animator animator = ViewAnimationUtils.createCircularReveal(loadingLayout, (width / 2), (height / 2), finalRadius, 0);
            animator.setDuration(1000);
            animator.start();
            loadingLayout.setVisibility(View.INVISIBLE);
        } else {
            loadingLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_up_btn_id) {
            password = "" + password_edittext.getText().toString();
            String confirmPassword = "" + confirm_password_edittext.getText().toString();

            if (!TextUtils.isEmpty(password) || !TextUtils.isEmpty(confirmPassword)) {

                if (TextUtils.equals(password, confirmPassword)) {

                    showLoadingProgressbar();
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
    public void onBackPressed() {
        if (loadingLayout.getVisibility() == View.VISIBLE) {
            hideLoadingProgressbar();
        }else {
            super.onBackPressed();
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
