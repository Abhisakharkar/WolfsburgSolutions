package com.example.abhishek.work;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent;
        if (sharedPreferences.getBoolean("isSignedIn", false)) {
            if (sharedPreferences.getBoolean("isVerified", false)) {
                if (sharedPreferences.getBoolean("isDataFilled", false)) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, ProfileActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, VerificationActivity.class);
            }
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
