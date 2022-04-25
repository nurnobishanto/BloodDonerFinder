package com.zedapp.reddrop_blooddonorfinder;

import android.app.Application;

import com.onesignal.OneSignal;

public class MainApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "d503f620-6318-40c4-ae86-d7d6034dc950";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}
