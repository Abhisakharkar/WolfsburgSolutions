package com.example.abhishek.work.SupportClasses;

import android.os.Build;

public class AndroidVersionChecker {

    public static boolean isMarshmallowOrGreater(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public static boolean isLollipopOrGreater(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public static boolean isKitkatOrGreater(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
    }
}
