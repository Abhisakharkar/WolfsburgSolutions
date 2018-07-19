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

public class DeliverySettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText maxDistEdittext, maxFreeDistEdittext, chargeEdittext, minAmountEdittext;
    private int maxDist, maxFreeDIst, charge, minAmount;
    private boolean sendRequest = false,deliveryStatus;
    private SendData sendData;
    private Switch deliverySwitch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FrameLayout deliveryFrame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_settings1);

        sendData = new SendData(DeliverySettingsActivity.this);
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

            maxDistEdittext.setText(maxDist);
            maxFreeDistEdittext.setText(maxFreeDIst);
            chargeEdittext.setText(charge);
            minAmountEdittext.setText(minAmount);
        }

        maxDistEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (!maxDistEdittext.getText().toString().trim().isEmpty()) {
                        maxDist = Integer.parseInt(maxDistEdittext.getText().toString());
                    }
                } else {
                    if (maxDistEdittext.getText().toString().trim().isEmpty()) {
                        Toast.makeText(DeliverySettingsActivity.this, "This Field cannot be " +
                                "empty !", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!String.valueOf(maxDist).equals(maxDistEdittext.getText().toString())) {
                            maxDist = Integer.parseInt(maxDistEdittext.getText().toString());
                            sendRequest = true;
                        }
                    }
                }
            }
        });

        maxFreeDistEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (!maxFreeDistEdittext.getText().toString().trim().isEmpty()) {
                        maxFreeDIst = Integer.parseInt(maxFreeDistEdittext.getText().toString());
                    }
                } else {
                    if (maxFreeDistEdittext.getText().toString().trim().isEmpty()) {
                        Toast.makeText(DeliverySettingsActivity.this, "This field cannot be " +
                                "empty !", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!String.valueOf(maxFreeDIst).equals(maxFreeDistEdittext.getText().toString())) {
                            maxFreeDIst = Integer.parseInt(maxFreeDistEdittext.getText()
                                    .toString());
                            sendRequest = true;
                        }
                    }
                }
            }
        });

        chargeEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (!chargeEdittext.getText().toString().trim().isEmpty()) {
                        charge = Integer.parseInt(chargeEdittext.getText().toString());
                    }
                } else {
                    if (chargeEdittext.getText().toString().trim().isEmpty()) {
                        Toast.makeText(DeliverySettingsActivity.this, "This field cannot be " +
                                "empty !", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!String.valueOf(charge).equals(chargeEdittext.getText().toString())) {
                            charge = Integer.parseInt(chargeEdittext.getText().toString());
                            sendRequest = true;
                        }
                    }
                }
            }
        });

        minAmountEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (!minAmountEdittext.getText().toString().trim().isEmpty()) {
                        minAmount = Integer.parseInt(minAmountEdittext.getText().toString());
                    }
                } else {
                    if (minAmountEdittext.getText().toString().trim().isEmpty()) {
                        Toast.makeText(DeliverySettingsActivity.this, "This field cannot be " +
                                "empty !", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!String.valueOf(minAmount).equals(minAmountEdittext.getText().toString())) {
                            minAmount = Integer.parseInt(minAmountEdittext.getText().toString());
                            sendRequest = true;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sendRequest) {
            editor.putInt("maxDeliveryDistanceInMeters", maxDist);
            editor.putInt("maxFreeDeliveryDistanceInMeters", maxFreeDIst);
            editor.putInt("chargePerHalfKiloMeterForDelivery", charge);
            editor.putInt("minAmountForFreeDelivery", minAmount);
            editor.commit();
            sendData.updateDeliverySettings(maxDist, maxFreeDIst, charge, minAmount);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_delivery_settings_delivery_status_switch_btn_id:
                setVisibility();
                break;
            case R.id.activity_delivery_settings_save_btn_id:

                onStop();
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
