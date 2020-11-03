package com.example.autoclick;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        String appId = "5fa0b67b45b2b751a921f395";
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, appId, "default", UMConfigure.DEVICE_TYPE_PHONE, null);
    }
}
