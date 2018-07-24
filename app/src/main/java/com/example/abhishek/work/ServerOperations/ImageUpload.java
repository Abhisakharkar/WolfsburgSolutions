package com.example.abhishek.work.ServerOperations;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ImageUploadResponseListener.ImageUploadResponse;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.SupportClasses.MultipartRequest;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

/**
 * Created by Abhishek on 08-06-2018.
 */

public class ImageUpload {

    private ImageUploadResponse imageUploadResponse;
    private Request request;
    private Context context;
    private SharedPreferences sharedPreferences;

    public ImageUpload(Context context) {
        this.imageUploadResponse = new ImageUploadResponse();
        this.context = context;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
    }

    public ImageUploadResponse getImageUploadResponseInstance(){
        return imageUploadResponse;
    }

   public void uploadImage(String photoName, String photoPath) {

        String url = "http://ec2-13-58-16-206.us-east-2.compute.amazonaws.com:6868/upload";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(photoName, photoPath)
                .addFormDataPart("imageFile",photoName,RequestBody.create(MediaType.parse("JPEG"),new File(photoPath)))
                .addFormDataPart("type", "imageFile")
                .addFormDataPart("name", "imageFile")
                .build();

        Log.e("requestBody", requestBody.toString());

        request = new Request.Builder()
                .url(url)
                .addHeader("type","imageFile")
                .addHeader("Authorization","bearer "+sharedPreferences.getString("token",""))
                .post(requestBody)
                .build();


        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("imageUpload exception", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                imageUploadResponse.saveImageUploadResponse(response);
            }
        });


    }
}
