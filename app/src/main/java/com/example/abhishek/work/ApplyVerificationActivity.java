package com.example.abhishek.work;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;

public class ApplyVerificationActivity extends AppCompatActivity {

    public SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView status;
    private Boolean locationVerified,appliedForVerification;
    private Button applyForVerificationbtn;
    private SendData sendData;
    private ServerResponse serverResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_verification);

        sendData = new SendData(ApplyVerificationActivity.this);
        serverResponse = sendData.getServerResponseInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        status=findViewById(R.id.verification_status);
        applyForVerificationbtn=findViewById(R.id.apply_for_verification_btn_id);
        checkStatus();

        applyForVerificationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("appliedForVerification",1);
                editor.commit();
                sendData.applyForVerification();
                checkStatus();
            }
        });

        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                //TODO process response
                Log.e("applied for verify", "onResponseReceive: " );
                finish();
            }

            @Override
            public void onResponseErrorReceive(String msg) {
                Toast.makeText(ApplyVerificationActivity.this, "error sending data to server but updated locally. try sending it after some time", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkStatus(){
        locationVerified=(sharedPreferences.getInt("locationVerified",0)>0);
        appliedForVerification=(sharedPreferences.getInt("appliedForVerification",0)>0);
        if (locationVerified){
            status.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
            status.setText("verified");
        }else {
            if (appliedForVerification){
                status.setTextColor(getApplicationContext().getResources().getColor(R.color.yellow));
                status.setText("Applied");
            }

        }
        if (locationVerified || appliedForVerification){
            applyForVerificationbtn.setClickable(false);
            applyForVerificationbtn.setEnabled(false);
        }
    }
}
