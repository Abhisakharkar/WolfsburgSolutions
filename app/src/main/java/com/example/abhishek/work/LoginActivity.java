package com.example.abhishek.work;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abhishek.work.DatabaseOperations.Authentication;
import com.example.abhishek.work.SupportClasses.NetworkStatusChecker;
import com.example.abhishek.work.SupportClasses.OnResponseReceiveListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RC_SIGN_IN = 105;

    private Context context;

    //Check sign in status
    private Boolean isSignedIn;

    //Ui components
    private EditText email_edittext, password_edittext;
    private Button signin_btn, signUp_link_btn;
    private SignInButton googleSignIn_btn;
    private ProgressDialog progressDialog;

    //GoogleSignIn components
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;

    //User Data
    private String email = "";
    private String password = "";

    //Check if connected to internet or not
    NetworkStatusChecker networkStatusChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = LoginActivity.this;

/*
        networkStatusChecker = new NetworkStatusChecker(this);

        if (!networkStatusChecker.isConnected()) {
            //show dialog
            AlertDialog.Builder noNetworkDialog = new AlertDialog.Builder(this);
            noNetworkDialog.setTitle("No Internet Connection");
            noNetworkDialog.setMessage("Please connect to internet");
            noNetworkDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {

                    Runnable waitTwoSec = new Runnable() {
                        @Override
                        public void run(){
                            if(networkStatusChecker.isConnected()){
                                dialogInterface.dismiss();
                                checkSignInStatus();
                            }
                        }
                    };
                    Handler h = new Handler();
                    h.postDelayed(waitTwoSec, 100);

                }
            });
            noNetworkDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

        } else {
            checkSignInStatus();
        }
*/

        checkSignInStatus();

        if (!isSignedIn) {
            setContentView(R.layout.activity_login);

            email_edittext = (EditText) findViewById(R.id.email_edittext_login_id);
            password_edittext = (EditText) findViewById(R.id.password_edittext_login_id);
            signin_btn = (Button) findViewById(R.id.signIn_btn_id);
            signUp_link_btn = (Button) findViewById(R.id.sign_up_link_btn_id);
            googleSignIn_btn = (SignInButton) findViewById(R.id.google_signIn_btn_id);
            googleSignIn_btn.setSize(SignInButton.SIZE_STANDARD);

            signin_btn.setOnClickListener(this);
            signUp_link_btn.setOnClickListener(this);
            googleSignIn_btn.setOnClickListener(this);

            //Google Sign In Configuration
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        } else if (isSignedIn) {
            checkData();
        }
    }

    public void checkData() {
        if (email != null || !email.isEmpty()) {
            Authentication authentication = new Authentication(LoginActivity.this);
            authentication.checkData(email);

            authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
                @Override
                public void onResponseReceive(JSONObject responseJSONObject) {

                    try {
                        String isAvailable = responseJSONObject.getString("isDataAvailable");

                        if (isAvailable.equals("true")) {
                            //code if user is signin in and required data is available
                        } else {
                            //code if user is signed in but data not available
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void checkSignInStatus() {
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount == null) {
            isSignedIn = false;
        } else {
            isSignedIn = true;
            email = googleSignInAccount.getEmail();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.google_signIn_btn_id) {
            SignInWithGoogle();
        }

        if (view.getId() == R.id.signIn_btn_id) {
            email = email_edittext.getText().toString();
            password = password_edittext.getText().toString();

            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                if (Patterns.EMAIL_ADDRESS.matcher(password).matches()) {

                    Authentication authentication = new Authentication(LoginActivity.this);
                    authentication.signInWithEmail(email, password);

                    authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
                        @Override
                        public void onResponseReceive(JSONObject responseJSONObject) {

                            try {
                                String response = responseJSONObject.getString("signInStatus");

                                if (response.equals("signInPass")) {
                                    //sign in success
                                } else if (response.equals("signInFailed")) {
                                    //sign in failed
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "check yout email", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            }
        }

        if (view.getId() == R.id.sign_up_link_btn_id) {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        }
    }

    private void SignInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                googleSignInAccount = task.getResult(ApiException.class);

                //now signed in successfully
                //code after successfull sign in
                email = googleSignInAccount.getEmail();
                checkData();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
