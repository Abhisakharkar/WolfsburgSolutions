package com.example.abhishek.work;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ShopPhotoActivity extends AppCompatActivity {

    private Context context;
    private ImageView shopPhotoImageview;
    private Button changeImageBtn,saveImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_photo);

        context = this;
        shopPhotoImageview = (ImageView) findViewById(R.id.shop_photo_activity_imageview_id);
        changeImageBtn = (Button) findViewById(R.id.shop_photo_activity_edit_btn_id);
        saveImageBtn = (Button) findViewById(R.id.shop_photo_activity_save_btn_id);

        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }
}
