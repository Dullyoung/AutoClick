package com.example.autoclick.model.engine;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.autoclick.model.bean.UpdateInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.OkHttpRequest;
import com.zhy.http.okhttp.request.RequestCall;

import okhttp3.Call;
import okhttp3.Response;

public class UpdataEngine {
    private String url = "https://share.weiyun.com/MHdTxl9h";
    public void getInfo(Callback callback) {
        RequestCall requestCall = OkHttpUtils.get().url(url).build();
        requestCall.execute(callback);
    }
}
