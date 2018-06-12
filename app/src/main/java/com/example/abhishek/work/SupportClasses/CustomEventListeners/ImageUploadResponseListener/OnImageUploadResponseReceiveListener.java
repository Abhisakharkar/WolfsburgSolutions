package com.example.abhishek.work.SupportClasses.CustomEventListeners.ImageUploadResponseListener;

import okhttp3.Response;

/**
 * Created by Abhishek on 12-06-2018.
 */

public interface OnImageUploadResponseReceiveListener {
    public void onImageUploadResponseReceive(Response response);
}
