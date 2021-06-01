package com.example.autoclick;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        CrashReport.initCrashReport(getApplicationContext(), "9336cd040d", false);
    }
}
