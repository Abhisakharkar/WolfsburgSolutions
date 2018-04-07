package com.example.abhishek.work.SupportClasses.CustomEventListeners.LocationResponseListener;

import com.google.android.gms.location.LocationRequest;

public class LocationResponse {

    private OnLocationResponseReceiveListener onLocationResponseReceiveListener;

    public void setOnLocationResponseReceiveListener(OnLocationResponseReceiveListener onLocationResponseReceiveListener){
        this.onLocationResponseReceiveListener = onLocationResponseReceiveListener;
    }

    public void saveLocationResponse(double longitude,double latitude){
        if (this.onLocationResponseReceiveListener != null){
            this.onLocationResponseReceiveListener.onLocationResponseReceive(longitude,latitude);
        }
    }
}
