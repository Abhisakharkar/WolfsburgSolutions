package com.example.abhishek.work.SupportClasses.CustomEventListeners.ImageUploadResponseListener;

import okhttp3.Response;

/**
 * Created by Abhishek on 12-06-2018.
 */

public class ImageUploadResponse  {

    private OnImageUploadResponseReceiveListener onImageUploadResponseReceiveListener;

    public void setOnImageUploadResponseReceiveListener(OnImageUploadResponseReceiveListener onImageUploadResponseReceiveListener){
        this.onImageUploadResponseReceiveListener = onImageUploadResponseReceiveListener;
    }

    public void saveImageUploadResponse(Response response){
        if (this.onImageUploadResponseReceiveListener != null){
            this.onImageUploadResponseReceiveListener.onImageUploadResponseReceive(response);
        }
    }
}
