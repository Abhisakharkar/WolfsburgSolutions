package com.example.abhishek.work.IntentServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloadIntentService extends IntentService {

    private int retailerId;

    public ImageDownloadIntentService() {
        super("ImageDownloadIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String url = intent.getStringExtra("url");
        String photoName = intent.getStringExtra("photoName");

        File photosLocationFile = new File(getApplicationContext().getFilesDir().getAbsolutePath
                () , "images");
        if (!photosLocationFile.exists()){
            photosLocationFile.mkdir();
        }

        try {
            URL URL = new URL(url);
            if (url != null) {
                if (!url.trim().isEmpty()) {
                    HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    Bitmap photoBitmap = BitmapFactory.decodeStream(inputStream);
                    //TODO save bitmap
                    if (photoBitmap != null) {
                        FileOutputStream fileOutputStream = null;
                        if (photoName != null) {
                            if (!photoName.isEmpty()) {
                                File photoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", photoName);
                                fileOutputStream = new FileOutputStream(photoFile);
                                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                fileOutputStream.close();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
