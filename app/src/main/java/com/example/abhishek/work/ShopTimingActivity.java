package com.example.abhishek.work;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ShopTimingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button open1Btn, open2Btn, close1Btn, close2Btn;
    private Switch manualSwitch, timing2Switch;
    private String open1, open2, close1, close2;
    private boolean isManual, isTiming2;

    private SendData sendData;
    private ServerResponse serverResponse;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_timing);

        sendData = new SendData(ShopTimingActivity.this);
        serverResponse = sendData.getServerResponseInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        open1Btn = (Button) findViewById(R.id.shop_timing_activity_open_1_btn_id);
        close1Btn = (Button) findViewById(R.id.shop_timing_activity_close_1_btn_id);
        open2Btn = (Button) findViewById(R.id.shop_timing_activity_open_2_btn_id);
        close2Btn = (Button) findViewById(R.id.shop_timing_activity_close_2_btn_id);
        manualSwitch = (Switch) findViewById(R.id.shop_timing_activity_title_switch_id);
        timing2Switch = (Switch) findViewById(R.id.shop_timing_activity_title_3_switch_id);

        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                //TODO process response
            }

            @Override
            public void onResponseErrorReceive(String msg) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setButtons();
    }

    private void setButtons() {
        isManual = sharedPreferences.getBoolean("openCloseIsManual", false);
        if (isManual) {
            open1Btn.setEnabled(false);
            open2Btn.setEnabled(false);
            close1Btn.setEnabled(false);
            close2Btn.setEnabled(false);
        } else {
            open1Btn.setEnabled(true);
            open2Btn.setEnabled(true);
            isTiming2 = sharedPreferences.getBoolean("isTiming2", false);
            if (isTiming2) {
                close1Btn.setEnabled(true);
                close2Btn.setEnabled(true);
            } else {
                close1Btn.setEnabled(false);
                close2Btn.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shop_timing_activity_open_1_btn_id:

                TimePickerDialog timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        open1Btn.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
                        open1 = String.valueOf(hour) + ":" + String.valueOf(minute);
                        saveTime("open1", open1);
                    }
                }, 00, 00, false);
                timePickerDialog1.show();
                break;
            case R.id.shop_timing_activity_close_1_btn_id:
                TimePickerDialog timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        close1Btn.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
                        close1 = String.valueOf(hour) + ":" + String.valueOf(minute);
                        saveTime("close1", close1);
                    }
                }, 00, 00, false);
                timePickerDialog2.show();
                break;
            case R.id.shop_timing_activity_open_2_btn_id:
                TimePickerDialog timePickerDialog3 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        open2Btn.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
                        open2 = String.valueOf(hour) + ":" + String.valueOf(minute);
                        saveTime("open2", open2);
                    }
                }, 00, 00, false);
                timePickerDialog3.show();
                break;
            case R.id.shop_timing_activity_close_2_btn_id:
                TimePickerDialog timePickerDialog4 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        close2Btn.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
                        close2 = String.valueOf(hour) + ":" + String.valueOf(minute);
                        saveTime("close2", close2);
                    }
                }, 00, 00, false);
                timePickerDialog4.show();
                break;
            case R.id.shop_timing_activity_title_switch_id:
                if (manualSwitch.isChecked()) {

                }
                break;
            case R.id.shop_timing_activity_title_3_switch_id:
                if (timing2Switch.isChecked()) {
                    editor.putBoolean("isTiming2", true);
                    editor.commit();
                    setButtons();
                } else {
                    editor.putBoolean("isTiming2", false);
                    editor.commit();
                    setButtons();
                }
                break;
        }
    }

    private void saveTime(String type, String time) {
        HashMap<String, String> headers = new HashMap<>();
        if (type.equals("open1")) {
            headers.put("shopOpenTime1", time);
            editor.putString("shopOpenTime1", time);
        } else if (type.equals("open2")) {
            headers.put("shopOpenTime2", time);
            editor.putString("shopOpenTime2", time);
        } else if (type.equals("close1")) {
            headers.put("shopCloseTime1", time);
            editor.putString("shopCloseTime1", time);
        } else if (type.equals("close2")) {
            headers.put("shopCloseTime2", time);
            editor.putString("shopCloseTime2", time);
        }
        editor.commit();
        sendData.sendtime(headers);
    }
}
