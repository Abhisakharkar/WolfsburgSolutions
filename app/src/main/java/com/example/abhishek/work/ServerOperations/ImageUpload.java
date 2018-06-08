package com.example.abhishek.work.ServerOperations;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.VolleyError;
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

    private ServerResponse serverResponse;
    private Request request;
    private Context context;

    public ImageUpload(Context context) {
        serverResponse = new ServerResponse();
        this.context = context;
    }

    public ServerResponse getServerResponseInstance() {
        return serverResponse;
    }

    public void uploadImage2(String photoName, String photoPath) {
        String uploadId = UUID.randomUUID().toString();
        String url = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/upload";

        try {
            new MultipartUploadRequest(context, uploadId, url)
                    .addFileToUpload(photoPath, photoName, photoName)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(1)
                    .setMethod("POST")
                    .addHeader("ENCTYPE","recfile")
                    .startUpload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void uploadImage4(String name,String path){
        String url = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/upload";
        MultipartRequest multipartRequest = new MultipartRequest(context);
        multipartRequest.addFile(name,path,name);
        multipartRequest.execute(url);
    }

    public void uploadImage(String photoName, String photoPath) {

        String url = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/upload";

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
                Log.e("imageUpload Response", response.message().toString());
            }
        });


    }
}
