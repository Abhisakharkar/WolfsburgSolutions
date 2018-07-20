package com.example.abhishek.work;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","wolfsburgproject@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/query");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
