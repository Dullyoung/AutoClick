package com.example.autoclick.Utils;

import android.os.Handler;
import android.os.Looper;

/*
 *   Created by Dullyoung on 2020/10/28 0028
 */
public class MyPost {
    private static final Handler gUiHandler = new Handler(Looper.getMainLooper());

    public static void post(Runnable r) {
        gUiHandler.post(r);
    }

    public static void postDelayed(long delay, Runnable r) {
        gUiHandler.postDelayed(r, delay);
    }
}
