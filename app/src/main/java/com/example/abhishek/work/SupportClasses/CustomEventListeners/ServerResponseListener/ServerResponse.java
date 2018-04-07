package com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener;

import org.json.JSONObject;

public class ServerResponse {
    private OnResponseReceiveListener mOnResponseReceiveListener;

    public void setOnResponseReceiveListener(OnResponseReceiveListener responseReceiveListener){
        mOnResponseReceiveListener = responseReceiveListener;
    }

    public void saveResponse(JSONObject responseJsonObject){
        if(mOnResponseReceiveListener != null){
            mOnResponseReceiveListener.onResponseReceive(responseJsonObject);
        }
    }
}
