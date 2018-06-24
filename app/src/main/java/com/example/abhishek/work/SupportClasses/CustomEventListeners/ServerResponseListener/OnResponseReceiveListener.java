package com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener;

import org.json.JSONObject;

import okhttp3.Response;

public interface OnResponseReceiveListener {
    public void onResponseReceive(JSONObject responseJSONObject);
    public void onResponseErrorReceive(String msg);
}
