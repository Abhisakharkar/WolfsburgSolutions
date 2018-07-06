package com.example.abhishek.work;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;

public class ShopTimingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button open1Btn, open2Btn, close1Btn, close2Btn;
    private Switch manualSwitch, timing2Switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_timing);

        open1Btn = (Button) findViewById(R.id.shop_timing_activity_open_1_btn_id);
        close1Btn = (Button) findViewById(R.id.shop_timing_activity_close_1_btn_id);
        open2Btn = (Button) findViewById(R.id.shop_timing_activity_open_2_btn_id);
        close2Btn = (Button) findViewById(R.id.shop_timing_activity_close_2_btn_id);
        manualSwitch = (Switch) findViewById(R.id.shop_timing_activity_title_switch_id);
        timing2Switch = (Switch) findViewById(R.id.shop_timing_activity_title_3_switch_id);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shop_timing_activity_open_1_btn_id:

                TimePickerDialog timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        open1Btn.setText(String.valueOf(hour)+":"+String.valueOf(minute));
                    }
                },00,00,false);
                timePickerDialog1.show();
                break;
            case R.id.shop_timing_activity_close_1_btn_id:
                TimePickerDialog timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        close1Btn.setText(String.valueOf(hour)+":"+String.valueOf(minute));
                    }
                },00,00,false);
                timePickerDialog2.show();
                break;
            case R.id.shop_timing_activity_open_2_btn_id:
                TimePickerDialog timePickerDialog3 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        open2Btn.setText(String.valueOf(hour)+":"+String.valueOf(minute));
                    }
                },00,00,false);
                timePickerDialog3.show();
                break;
            case R.id.shop_timing_activity_close_2_btn_id:
                TimePickerDialog timePickerDialog4 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        close2Btn.setText(String.valueOf(hour)+":"+String.valueOf(minute));
                    }
                },00,00,false);
                timePickerDialog4.show();
                break;
            case R.id.shop_timing_activity_title_switch_id:

                break;
            case R.id.shop_timing_activity_title_3_switch_id:
                break;
        }
    }
}
