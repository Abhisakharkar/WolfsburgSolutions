package com.example.abhishek.work.SupportClasses;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Abhishek on 08-06-2018.
 */

public class MultipartRequest {
    public Context context;
    public MultipartBody.Builder multipartBody;
    public OkHttpClient okHttpClient;

    public MultipartRequest(Context context)
    {
        this.context = context;
        this.multipartBody = new MultipartBody.Builder();
        this.multipartBody.setType(MultipartBody.FORM);
        this.okHttpClient = new OkHttpClient();
    }

    // Add String
    public void addString(String name, String value)
    {
        this.multipartBody.addFormDataPart(name, value);
    }

    // Add Image File
    public void addFile(String name, String filePath, String fileName)
    {
        this.multipartBody.addFormDataPart(name, fileName, RequestBody.create(MediaType.parse("image/jpeg"), new File(filePath)));
    }

    // Add Zip File
    public void addZipFile(String name, String filePath, String fileName)
    {
        this.multipartBody.addFormDataPart(name, fileName, RequestBody.create(MediaType.parse("application/zip"), new File(filePath)));
    }

    // Execute Url
    public String execute(String url)
    {
        RequestBody requestBody = null;
        Request request = null;
        Response response = null;
        int code = 200;
        String strResponse = null;

        try
        {
            requestBody = this.multipartBody.build();
            // Set Your Authentication key here.
            request = new Request.Builder().header("Key", "Value").url(url).post(requestBody).build();

            Log.v("====== REQUEST ======",""+request);
            okHttpClient.newCall(request).execute();
            Log.v("====== RESPONSE ======",""+response);

            if (!response.isSuccessful())
                throw new IOException();

            code = response.networkResponse().code();

            /*
             * "Successful response from server"
             */
            if (response.isSuccessful())
            {
                strResponse =response.body().string();
            }
            /*
             * "Invalid URL or Server not available, please try again."
             */

        }
        catch (Exception e)
        {
            Log.e("Exception", "some error occured in multipartRequest Class !");
        }
        finally
        {
            requestBody = null;
            request = null;
            response = null;
            multipartBody = null;
            if (okHttpClient != null)
                okHttpClient = null;

            System.gc();
        }
        return strResponse;
    }
}
