package com.example.abhishek.work;

import android.animation.Animator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.SupportClasses.NetworkStatusChecker;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mail_edittext, password_edittext;
    private Button signin_btn, signUp_link_btn;
    private ImageView doneImageView;
    private SignInButton googleSignIn_btn;
    private FrameLayout mailCheckLayout;
    private View loadingLayout;
    private ProgressBar mailCHeckProgressBar;

    private String mail = "";
    private String password = "";

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Authentication authentication;
    private ServerResponse serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authentication = new Authentication(context);
        serverResponse = authentication.getServerResponseInstance();

        Log.e("mail_edittext", (mail_edittext == null ? "null" : "not null"));
        mail_edittext = (EditText) findViewById(R.id.email_edittext_login_id);
        Log.e("mail_edittext", (mail_edittext == null ? "null" : "not null"));
        password_edittext = (EditText) findViewById(R.id.password_edittext_login_id);
        signin_btn = (Button) findViewById(R.id.signIn_btn_id);
        signUp_link_btn = (Button) findViewById(R.id.sign_up_link_btn_id);
        googleSignIn_btn = (SignInButton) findViewById(R.id.google_signIn_btn_id);
        googleSignIn_btn.setSize(SignInButton.SIZE_STANDARD);
        mailCheckLayout = (FrameLayout) findViewById(R.id.login_activity_mail_check_framelayout_id);
        mailCheckLayout.setVisibility(View.INVISIBLE);
        mailCHeckProgressBar = (ProgressBar) findViewById(R.id.login_activity_mail_check_progress_bar_id);
        mailCHeckProgressBar.setVisibility(View.INVISIBLE);
        doneImageView = (ImageView) findViewById(R.id.login_activity_done_imageview_id);
        doneImageView.setVisibility(View.INVISIBLE);
        loadingLayout = (View) findViewById(R.id.login_activity_loading_layout);

        signin_btn.setClickable(false);
        signin_btn.setOnClickListener(this);
        signUp_link_btn.setOnClickListener(this);
        googleSignIn_btn.setOnClickListener(this);


        //edittext focus istener
        mail_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    mail = mail_edittext.getText().toString();
                    if (!mail.isEmpty()) {
                        if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                            mailCheckLayout.setVisibility(View.VISIBLE);
                            mailCHeckProgressBar.setVisibility(View.VISIBLE);
                            authentication.checkEmailExists(mail);
                        } else {
                            Toast.makeText(LoginActivity.this, "Please enter correct email !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Enter email !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    signin_btn.setClickable(false);

                    doneImageView.setVisibility(View.GONE);
                    mailCHeckProgressBar.setVisibility(View.GONE);
                    mailCheckLayout.setVisibility(View.GONE);
                }
            }
        });


        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(final JSONObject responseJSONObject) {

                try {

                    String responseFrom = responseJSONObject.getString("responseFrom");
                    if (responseFrom.equals("check_mail_exist")) {
                        boolean mailExist = responseJSONObject.getBoolean("mailExist");
                        if (mailExist) {
                            signin_btn.setClickable(true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                mailCheckLayout.setVisibility(View.VISIBLE);
                                //set progress bar invisible
                                int px = mailCHeckProgressBar.getMeasuredWidth() / 2;
                                int py = mailCHeckProgressBar.getMeasuredHeight() / 2;
                                int finalRadiusProgressBar = Math.max(mailCHeckProgressBar.getWidth(), mailCHeckProgressBar.getHeight()) / 2;
                                Animator animProgressBar = ViewAnimationUtils
                                        .createCircularReveal(mailCHeckProgressBar, px, py, finalRadiusProgressBar, 0);
                                animProgressBar.setDuration(500);
                                animProgressBar.start();
                                mailCHeckProgressBar.setVisibility(View.INVISIBLE);

                                //set done view visible
                                int dx = doneImageView.getMeasuredWidth() / 2;
                                int dy = doneImageView.getMeasuredHeight() / 2;
                                int finalRadiusDoneView = Math.max(doneImageView.getWidth(), doneImageView.getHeight()) / 2;
                                Animator animDoneView = ViewAnimationUtils
                                        .createCircularReveal(doneImageView, dx, dy, 0, finalRadiusDoneView);
                                animDoneView.setDuration(500);
                                animDoneView.start();
                                doneImageView.setVisibility(View.VISIBLE);


                            } else {
                                mailCheckLayout.setVisibility(View.VISIBLE);
                                doneImageView.setVisibility(View.VISIBLE);
                                mailCHeckProgressBar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Toast.makeText(context, "Email not registered !", Toast.LENGTH_SHORT).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                //set progress bar invisible
                                int px = mailCHeckProgressBar.getMeasuredWidth() / 2;
                                int py = mailCHeckProgressBar.getMeasuredHeight() / 2;
                                int finalRadiusProgressBar = Math.max(mailCHeckProgressBar.getWidth(), mailCHeckProgressBar.getHeight()) / 2;
                                Animator animProgressBar = ViewAnimationUtils
                                        .createCircularReveal(mailCHeckProgressBar, px, py, finalRadiusProgressBar, 0);
                                animProgressBar.setDuration(500);
                                animProgressBar.start();
                                mailCHeckProgressBar.setVisibility(View.INVISIBLE);
                                doneImageView.setVisibility(View.INVISIBLE);
                                mailCheckLayout.setVisibility(View.GONE);
                            } else {
                                doneImageView.setVisibility(View.INVISIBLE);
                                mailCHeckProgressBar.setVisibility(View.INVISIBLE);
                                mailCheckLayout.setVisibility(View.GONE);
                            }
                        }

                    } else if (responseFrom.equals("sign_in")) {
                        boolean signIn = responseJSONObject.getBoolean("signIn");
                        if (signIn) {
                            JSONObject retailerAuthTableJson = responseJSONObject.getJSONObject("retailerAuthTable");
                            JSONObject retailerDataTableJson = responseJSONObject.getJSONObject("retailerDataTable");

                            String deviceId = retailerAuthTableJson.getString("deviceId");
                            if (deviceId.equals(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID))) {
                                int isVerified = retailerAuthTableJson.getInt("codeVerified");
                                if (isVerified == 1) {
                                    int isDataFilled = retailerAuthTableJson.getInt("mandatoryData");
                                    if (isDataFilled == 1) {
                                        editor.putBoolean("isDataFilled", true);
                                        editor.putString("mail", mail);
                                        editor.putString("password", password);
                                        editor.putBoolean("isVerified", true);
                                        editor.putString("shopName", retailerDataTableJson.getString("enterpriseName"));
                                        editor.putString("proprietor", retailerDataTableJson.getString("proprietor"));
                                        editor.putString("mobileNo", retailerDataTableJson.getString("mobileNo"));
                                        editor.putInt("retailerId", retailerDataTableJson.getInt("retailerId"));
                                        editor.putString("addLine1", retailerDataTableJson.getString("addLine1"));
                                        editor.putString("addLine2", retailerDataTableJson.getString("addLine2"));
                                        editor.putString("city", retailerDataTableJson.getString("city"));
                                        editor.putString("state", retailerDataTableJson.getString("state"));
                                        editor.putString("country", retailerDataTableJson.getString("country"));
                                        editor.putString("profilePhoto", retailerDataTableJson.getString("profilePhoto"));
                                        editor.putString("longitude", String.valueOf(retailerDataTableJson.getDouble("longLoc")));
                                        editor.putString("latitude", String.valueOf(retailerDataTableJson.getDouble("latLoc")));
                                        editor.putBoolean("isSignedIn", true);
                                        editor.commit();
                                        hideLoadingProgressbar();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();

                                    } else if (isDataFilled == 0) {
                                        editor.putBoolean("isDataFilled", false);
                                        editor.putString("mail", mail);
                                        editor.putBoolean("isVerified", true);
                                        editor.putString("password", password);
                                        editor.putInt("retailerId", retailerAuthTableJson.getInt("retailerId"));
                                        if (!retailerDataTableJson.getString("mobileNo").isEmpty()) {
                                            editor.putString("shopName", retailerDataTableJson.getString("enterpriseName"));
                                            editor.putString("proprietor", retailerDataTableJson.getString("proprietor"));
                                            editor.putString("mobileNo", retailerDataTableJson.getString("mobileNo"));
                                            editor.putString("addLine1", retailerDataTableJson.getString("addLine1"));
                                            editor.putString("addLine2", retailerDataTableJson.getString("addLine2"));
                                            editor.putString("city", retailerDataTableJson.getString("city"));
                                            editor.putString("state", retailerDataTableJson.getString("state"));
                                            editor.putString("country", retailerDataTableJson.getString("country"));
                                            editor.putString("longitude", String.valueOf(retailerDataTableJson.getDouble("longLoc")));
                                            editor.putString("latitude", String.valueOf(retailerDataTableJson.getDouble("latLoc")));
                                        }
                                        editor.putBoolean("isSignedIn", true);
                                        editor.commit();
                                        hideLoadingProgressbar();
                                        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                                        finish();
                                    }

                                } else if (isVerified == 0) {
                                    editor.putBoolean("isDataFilled", false);
                                    editor.putString("mail", mail);
                                    editor.putString("password", password);
                                    editor.putInt("retailerId", retailerAuthTableJson.getInt("retailerId"));
                                    editor.putBoolean("isVerified", false);
                                    editor.putBoolean("isSignedIn", true);
                                    editor.commit();
                                    hideLoadingProgressbar();
                                    startActivity(new Intent(LoginActivity.this, VerificationActivity.class));
                                    finish();
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Another phone has logged in into this account." +
                                        "\nDo you want to logout from other device and login from this device ?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            JSONObject tempJsonObject = responseJSONObject.getJSONObject("retailerAuthTable");
                                            int retailerId = tempJsonObject.getInt("retailerId");
                                            authentication.signInFromThisDevice(retailerId);
                                            dialogInterface.dismiss();
                                            showLoadingProgressbar();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mail_edittext.setText("");
                                        password_edittext.setText("");
                                        dialogInterface.dismiss();
                                    }
                                });
                                Dialog dialog = builder.create();
                                hideLoadingProgressbar();
                                dialog.show();

                            }
                        } else {
                            hideLoadingProgressbar();
                            Toast.makeText(context, "Wrong Password !", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
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
                    int px = mailCHeckProgressBar.getMeasuredWidth() / 2;
                    int py = mailCHeckProgressBar.getMeasuredHeight() / 2;
                    int finalRadiusProgressBar = Math.max(mailCHeckProgressBar.getWidth(), mailCHeckProgressBar.getHeight()) / 2;
                    Animator animProgressBar = ViewAnimationUtils
                            .createCircularReveal(mailCHeckProgressBar, px, py, finalRadiusProgressBar, 0);
                    animProgressBar.setDuration(500);
                    animProgressBar.start();
                    mailCHeckProgressBar.setVisibility(View.INVISIBLE);
                    mailCheckLayout.setVisibility(View.GONE);
                } else {
                    mailCheckLayout.setVisibility(View.GONE);
                    doneImageView.setVisibility(View.INVISIBLE);
                    mailCHeckProgressBar.setVisibility(View.INVISIBLE);
                }

                Toast.makeText(context, "Some Technical Error Occured ! \n Try again later !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showLoadingProgressbar() {
        View btn = signin_btn;
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

        if (view.getId() == R.id.signIn_btn_id) {
            password = password_edittext.getText().toString();
            if (!TextUtils.isEmpty(password)) {
                authentication.signIn(mail, password);
                showLoadingProgressbar();
            } else {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            }
        }

        if (view.getId() == R.id.sign_up_link_btn_id) {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        }
    }


    @Override
    public void onBackPressed() {
        if (loadingLayout.getVisibility() == View.INVISIBLE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Do you want to Exit ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create();
            builder.show();
        } else if (loadingLayout.getVisibility() == View.VISIBLE) {
            hideLoadingProgressbar();
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
            Toast.makeText(context, "no internet connection", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "connected to internet", Toast.LENGTH_SHORT).show();
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
