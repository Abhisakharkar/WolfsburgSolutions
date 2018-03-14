package com.example.abhishek.work.SupportClasses;

import android.os.Build;

public class AndroidPermissionChecker {

    public static boolean isApi23OrGreater(){
        return (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}
