package com.example.abhishek.work;

import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;

import java.util.HashMap;

public class DeliverySettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText maxDistEdittext, maxFreeDistEdittext, chargeEdittext, minAmountEdittext;
    private int maxDist, maxFreeDIst, charge, minAmount;
    private boolean sendRequest = false,deliveryStatus;
    private SendData sendData;
    private Switch deliverySwitch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FrameLayout deliveryFrame;
    private ServerResponse serverResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_settings1);

        sendData = new SendData(DeliverySettingsActivity.this);
        serverResponse = sendData.getServerResponseInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        deliverySwitch=(Switch) findViewById(R.id.activity_delivery_settings_delivery_status_switch_btn_id);
        deliveryFrame=(FrameLayout) findViewById(R.id.delivery_frame);
        maxDistEdittext = (EditText) findViewById(R.id.delivery_setting_activity_edittext_1_id);
        maxFreeDistEdittext = (EditText) findViewById(R.id.delivery_setting_activity_edittext_2_id);
        chargeEdittext = (EditText) findViewById(R.id.delivery_setting_activity_edittext_3_id);
        minAmountEdittext = (EditText) findViewById(R.id.delivery_setting_activity_edittext_4_id);

        deliveryStatus=sharedPreferences.getBoolean("deliveryStatus",false);
        deliverySwitch.setChecked(deliveryStatus);
        setVisibility();
        if (sharedPreferences.getInt("maxDeliveryDistanceInMeters", 0) != 0) {
            maxDist = sharedPreferences.getInt("maxDeliveryDistanceInMeters", 0);
            maxFreeDIst = sharedPreferences.getInt("maxFreeDeliveryDistanceInMeters", 0);
            charge = sharedPreferences.getInt("chargePerHalfKiloMeterForDelivery", 0);
            minAmount = sharedPreferences.getInt("minAmountForFreeDelivery", 0);

            maxDistEdittext.setText(String.valueOf(maxDist));
            maxFreeDistEdittext.setText(Integer.toString(maxFreeDIst));
            chargeEdittext.setText(Integer.toString(charge));
            minAmountEdittext.setText(Integer.toString(minAmount));
        }
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                finish();
            }

            @Override
            public void onResponseErrorReceive(String msg) {
                Toast.makeText(DeliverySettingsActivity.this, "error sending data to server but updated locally. try sending it after some time", Toast.LENGTH_LONG).show();
            }
        });
//        maxDistEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    if (!maxDistEdittext.getText().toString().trim().isEmpty()) {
//                        maxDist = Integer.parseInt(maxDistEdittext.getText().toString());
//                    }
//                } else {
//                    if (maxDistEdittext.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(DeliverySettingsActivity.this, "This Field cannot be " +
//                                "empty !", Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (!String.valueOf(maxDist).equals(maxDistEdittext.getText().toString())) {
//                            maxDist = Integer.parseInt(maxDistEdittext.getText().toString());
//                            sendRequest = true;
//                        }
//                    }
//                }
//            }
//        });
//
//        maxFreeDistEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) {
//                    if (!maxFreeDistEdittext.getText().toString().trim().isEmpty()) {
//                        maxFreeDIst = Integer.parseInt(maxFreeDistEdittext.getText().toString());
//                    }
//                } else {
//                    if (maxFreeDistEdittext.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(DeliverySettingsActivity.this, "This field cannot be " +
//                                "empty !", Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (!String.valueOf(maxFreeDIst).equals(maxFreeDistEdittext.getText().toString())) {
//                            maxFreeDIst = Integer.parseInt(maxFreeDistEdittext.getText()
//                                    .toString());
//                            sendRequest = true;
//                        }
//                    }
//                }
//            }
//        });
//
//        chargeEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) {
//                    if (!chargeEdittext.getText().toString().trim().isEmpty()) {
//                        charge = Integer.parseInt(chargeEdittext.getText().toString());
//                    }
//                } else {
//                    if (chargeEdittext.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(DeliverySettingsActivity.this, "This field cannot be " +
//                                "empty !", Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (!String.valueOf(charge).equals(chargeEdittext.getText().toString())) {
//                            charge = Integer.parseInt(chargeEdittext.getText().toString());
//                            sendRequest = true;
//                        }
//                    }
//                }
//            }
//        });
//
//        minAmountEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) {
//                    if (!minAmountEdittext.getText().toString().trim().isEmpty()) {
//                        minAmount = Integer.parseInt(minAmountEdittext.getText().toString());
//                    }
//                } else {
//                    if (minAmountEdittext.getText().toString().trim().isEmpty()) {
//                        Toast.makeText(DeliverySettingsActivity.this, "This field cannot be " +
//                                "empty !", Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (!String.valueOf(minAmount).equals(minAmountEdittext.getText().toString())) {
//                            minAmount = Integer.parseInt(minAmountEdittext.getText().toString());
//                            sendRequest = true;
//                        }
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (sendRequest) {
//            editor.putInt("maxDeliveryDistanceInMeters", maxDist);
//            editor.putInt("maxFreeDeliveryDistanceInMeters", maxFreeDIst);
//            editor.putInt("chargePerHalfKiloMeterForDelivery", charge);
//            editor.putInt("minAmountForFreeDelivery", minAmount);
//            editor.commit();
//            sendData.updateDeliverySettings(maxDist, maxFreeDIst, charge, minAmount);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_delivery_settings_delivery_status_switch_btn_id:
                if (sharedPreferences.getInt("verifiedByTeam",0)>0){
                    setVisibility();
                }else
                {   deliverySwitch.setChecked(false);
                    Toast.makeText(this, "if your shop is in Mumbai you can contact us to start deliverying", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_delivery_settings_save_btn_id:
                HashMap<String,String> header=new HashMap<>();
                    if (deliverySwitch.isChecked()){
                        try{
                            maxDist = Integer.parseInt(maxDistEdittext.getText().toString());
                            maxFreeDIst = Integer.parseInt(maxFreeDistEdittext.getText().toString());
                            charge = Integer.parseInt(chargeEdittext.getText().toString());
                            minAmount = Integer.parseInt(minAmountEdittext.getText().toString());
                            if (maxDist!=0 && maxFreeDIst!=0 && charge!=0 && minAmount!=0){
                                editor.putBoolean("deliveryStatus",true);
                                editor.putInt("maxDeliveryDistanceInMeters", maxDist);
                                editor.putInt("maxFreeDeliveryDistanceInMeters", maxFreeDIst);
                                editor.putInt("chargePerHalfKiloMeterForDelivery", charge);
                                editor.putInt("minAmountForFreeDelivery", minAmount);
                                editor.commit();
                                header.put("deliveryStatus",String.valueOf(1));
                                header.put("maxDeliveryDistanceInMeters",String.valueOf(maxDist));
                                header.put("maxFreeDeliveryDistanceInMeters", String.valueOf(maxFreeDIst));
                                header.put("chargePerHalfKiloMeterForDelivery",String.valueOf(charge));
                                header.put("minAmountForFreeDelivery", String.valueOf(minAmount));
                                sendData.updateDeliveryData(header);
                            }else {
                                Toast.makeText(this, "value cannot be 0", Toast.LENGTH_LONG).show();
                            }

                        }catch (Exception e){
                            Toast.makeText(this, "value cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if(sharedPreferences.getBoolean("deliveryStatus",false)){
                            editor.putBoolean("deliveryStatus",false);
                            editor.commit();
                            header.put("deliveryStatus",String.valueOf(0));
                            sendData.updateDeliveryData(header);
                        }
                    }
                break;


        }
    }

    public void setVisibility(){
        if (deliverySwitch.isChecked()){
            deliveryFrame.setVisibility(View.INVISIBLE);
            maxDistEdittext.setEnabled(true);
            maxFreeDistEdittext.setEnabled(true);
            chargeEdittext.setEnabled(true);
            minAmountEdittext.setEnabled(true);
        }else {
            deliveryFrame.setVisibility(View.VISIBLE);
            maxDistEdittext.setEnabled(false);
            maxFreeDistEdittext.setEnabled(false);
            chargeEdittext.setEnabled(false);
            minAmountEdittext.setEnabled(false);
        }
    }
}
