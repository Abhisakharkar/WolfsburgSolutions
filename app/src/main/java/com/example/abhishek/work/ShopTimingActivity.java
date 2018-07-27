package com.example.abhishek.work;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;
import java.util.HashMap;

public class ShopTimingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button open1Btn, open2Btn, close1Btn, close2Btn;
    private Switch manualSwitch, timing2Switch;
    private String open1, open2, close1, close2;
    private FrameLayout frame1,frame2;
    private TextView textView;
    private boolean isManual;
    private int length=0;
    private SendData sendData;
    private ServerResponse serverResponse;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_timing1);

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
        frame1=(FrameLayout) findViewById(R.id.frame_layout_timing_1);
        frame2=(FrameLayout) findViewById(R.id.frame_layout_timing_2);
        isManual= sharedPreferences.getBoolean("openCloseIsManual", false);
        textView=(TextView) findViewById(R.id.text_below_button);
        manualSwitch.setChecked(isManual);
        open1=sharedPreferences.getString("shopOpenTime1",null);
        close1=sharedPreferences.getString("shopCloseTime1",null);
        open2=sharedPreferences.getString("shopOpenTime2",null);
        close2=sharedPreferences.getString("shopCloseTime2",null);
        if(open1 != null && open2 != null){
            open1Btn.setText(open1);
            close1Btn.setText(close1);
        }else{
            open1Btn.setText("Set Time");
            close1Btn.setText("Set Time");
        }
        if(open2 != null && close2 != null){
            timing2Switch.setChecked(true);
            open2Btn.setText(open2);
            close2Btn.setText(close2);
        }else {
            open2Btn.setText("Set Time");
            close2Btn.setText("Set Time");
        }
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                //TODO process response
                Toast.makeText(ShopTimingActivity.this, "response recieved", Toast.LENGTH_SHORT).show();
                if(length>0){
                    editor.putBoolean("openCloseIsManual",manualSwitch.isChecked());
                }
                if (length>1){
                    editor.putString("shopOpenTime1",open1);
                    editor.putString("shopCloseTime1",close1);
                }
                if(length>2){
                    editor.putString("shopOpenTime2",open2);
                    editor.putString("shopCloseTime2",close2);
                }
                editor.apply();
                finish();
            }

            @Override
            public void onResponseErrorReceive(String msg) {
                Toast.makeText(ShopTimingActivity.this, "error sending data to server but updated locally. try sending it after some time", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setButtons();
    }
    public void setButtons() {
        if (manualSwitch.isChecked()) {
            textView.setText("Manual");
            frame1.setVisibility(View.VISIBLE);
            frame2.setVisibility(View.VISIBLE);
            timing2Switch.setEnabled(false);
            open1Btn.setEnabled(false);
            open2Btn.setEnabled(false);
            close1Btn.setEnabled(false);
            close2Btn.setEnabled(false);
        } else {
            timing2Switch.setEnabled(true);
            textView.setText("Time Based");
            frame1.setVisibility(View.INVISIBLE);
            open1Btn.setEnabled(true);
            close1Btn.setEnabled(true);
            if (timing2Switch.isChecked()) {
                frame2.setVisibility(View.INVISIBLE);
                open2Btn.setEnabled(true);
                close2Btn.setEnabled(true);
            } else {
                frame2.setVisibility(View.VISIBLE);
                open2Btn.setEnabled(false);
                close2Btn.setEnabled(false);
            }
        }
    }
    public String format(int hour,int minute){
        String string;
        if (hour<10){
            string="0"+hour+":";
        }else {
            string=hour+":";
        }
        if (minute<10){
            string+="0"+minute+":00";
        }else {
            string+=minute+":00";
        }
        return string;
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.shop_timing_activity_open_1_btn_id:

                TimePickerDialog timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        open1 = format(hour,minute);
                        open1Btn.setText(open1);
                    }
                }, 00, 00, true);
                timePickerDialog1.show();
                break;
            case R.id.shop_timing_activity_close_1_btn_id:
                TimePickerDialog timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        close1 =format(hour,minute);
                        close1Btn.setText(close1);
                    }
                }, 00, 00, true);
                timePickerDialog2.show();
                break;
            case R.id.shop_timing_activity_open_2_btn_id:
                TimePickerDialog timePickerDialog3 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        open2 = format(hour,minute);
                        open2Btn.setText(open2);
                    }
                }, 00, 00, true);
                timePickerDialog3.show();
                break;
            case R.id.shop_timing_activity_close_2_btn_id:
                TimePickerDialog timePickerDialog4 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        close2 = format(hour,minute);
                        close2Btn.setText(close2);
                    }
                }, 00, 00, true);
                timePickerDialog4.show();
                break;
            case R.id.shop_timing_activity_title_switch_id:
                if (sharedPreferences.getInt("locationVerified",0)>0){
                    setButtons();
                }else {
                    Toast.makeText(this, "You cannot change it to time based until you get verified", Toast.LENGTH_LONG).show();
                    manualSwitch.setChecked(true);
                }
                break;
            case R.id.shop_timing_activity_title_3_switch_id:
                setButtons();
                break;
            case R.id.save_timing_btn:
                HashMap<String, String> headers = new HashMap<>();
                if(manualSwitch.isChecked()){
                    length=1;
                    headers.put("openCloseIsManual","1");
                }else {
                    if(open1==null|| close1==null){
                        Toast.makeText(this, "please enter open and close time 1", Toast.LENGTH_SHORT).show();
                        break;
                    }else {
                        length=2;
                        headers.put("openCloseIsManual","0");
                        headers.put("shopOpenTime1",open1);
                        headers.put("shopCloseTime1",close1);
                        if(timing2Switch.isChecked()){
                            if(open2==null||close2==null){
                                Toast.makeText(this, "please enter open and close time 2", Toast.LENGTH_SHORT).show();
                                break;
                            }else {
                                length=3;
                                headers.put("shopOpenTime2",open2);
                                headers.put("shopCloseTime2",close2);
                            }
                        }
                    }
                }
                sendData.sendtime(headers);
                break;
        }
    }


}
