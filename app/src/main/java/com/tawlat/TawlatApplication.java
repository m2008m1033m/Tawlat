package com.tawlat;

import android.app.Application;
import android.content.Context;

import com.tawlat.core.User;


/*
@ReportsCrashes(
        formKey = "",
        mailTo = "m2008m1033m@gmail.com",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.opening_email)
*/


public class TawlatApplication extends Application {
    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        User.getInstance();

        // The following line triggers the initialization of ACRA
        //ACRA.init(this);
    }

    public static Context getContext() {
        return mContext;
    }
}
