package com.example.autoclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.greenrobot.eventbus.EventBus;


public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "aaaa";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "无障碍服务已开启: ");
        Toast.makeText(this, "无障碍服务已开启,请返回软件启动", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        keyword = intent.getStringExtra("key");
        Log.i(TAG, "onStartCommand: " + keyword);
        return super.onStartCommand(intent, flags, startId);
    }

    private String keyword = "";
    AccessibilityNodeInfo rootInfo = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (keyword.equals("")) {
            Log.i(TAG, "关键词为空 不执行: ");
            //  Toast.makeText(this, "为获取到关键字，返回重试", Toast.LENGTH_SHORT).show();
            return;
        }

//
//            for (AccessibilityWindowInfo accessibilityWindowInfo : getWindows()) {
//
//                if (accessibilityWindowInfo.getRoot().getPackageName().equals("com.taobao.taobao")) {
//                    rootInfo = accessibilityWindowInfo.getRoot();
//                }
//
        rootInfo = event.getSource();
        //拿到根节点
//开始找目标节点，这里拎出来细讲，直接往下看正文
        if (rootInfo != null && rootInfo.getChildCount() != 0) {
            MyTask(rootInfo);
            Log.i(TAG, "开始执行任务");
            EventBus.getDefault().post(new EventStub(keyword));
        } else {
            Log.i(TAG, "rootInfo==null");
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        EventBus.getDefault().post(new EventStub("停止"));
    }

    private void MyTask(AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) {
            Log.i(TAG, "MyTask: 根节点不存在或根节点类名为空");
            return;
        }
        Log.i(TAG, "findByText: 开始查找字段````````" + keyword);
        //开始去找
        // findByID(rootInfo, "com.tencent.mobileqq:id/chat_item_content_layout");
//        if (findByText(rootInfo, keyword) != null) {
//            Log.i(TAG, "MyTask: " + rootInfo);
//        }
        get(rootInfo);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "停止运行", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new EventStub("停止运行"));
        return super.onUnbind(intent);
    }

    private AccessibilityNodeInfo back;

    long time;

    private void back(AccessibilityNodeInfo info) {
        if (System.currentTimeMillis() - time > 10000) {
            MyPost.postDelayed(2000,() -> {
                info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                time = System.currentTimeMillis();
            });
        }
    }


    private AccessibilityNodeInfo get(AccessibilityNodeInfo rootInfo) {
        if (rootInfo.getChildCount() > 0) {
            for (int i = 0; i < rootInfo.getChildCount(); i++) {
                AccessibilityNodeInfo info = rootInfo.getChild(i);
                if (info == null) {
                    continue;
                }


                if (info.getContentDescription() != null && (
                        info.getContentDescription().toString().contains("任务完成")
                                || info.getContentDescription().toString().contains("任务已经")
                                || info.getContentDescription().toString().contains("任务已完成"))) {
                    back(info);
                    Log.i("success", "完成 返回列表 ");
                }


                if (info.getText() != null && (
                        info.getText().toString().contains("任务完成")
                                || info.getText().toString().contains("任务已经")
                                || info.getText().toString().contains("任务已完成"))) {

                    back(info);
                    Log.i("success", "完成 返回列表 ");
                }


                if (info.getClassName() != null && info.getClassName().toString().contains("android.widget.Button")) {
                    if (info.getText() != null && info.getText().toString().contains(keyword)) {
                        Log.i("success", "找到节点 点击 " + keyword);
                        performClick(getClickable(info));
                    }
                }


                if (rootInfo.getChild(i).getChildCount() > 0) {
                    Log.i("findnode", rootInfo.getChildCount() + "father " + info.getClassName() + info.getText()
                            + "desp" + info.getContentDescription() + "hascode" + info.hashCode());
                    get(rootInfo.getChild(i));
                } else {
                    Log.i("findnode", "son: " + info.getClassName() + info.getText()
                            + "desp" + info.getContentDescription() + "hascode" + info.hashCode());
                }
            }


        }


        return null;
    }

    void redo() {


    }


    //有些节点不可点击 点击交给父级甚至父级的父级...来做的。
    private AccessibilityNodeInfo getClickable(AccessibilityNodeInfo info) {
        Log.i(TAG, info.getClassName() + ": " + info.isClickable());
        if (info.isClickable()) {
            return info;
        } else {
            return getClickable(info.getParent());
        }
    }

    /**
     * 深度优先遍历寻找目标节点
     */

    private void performClick(AccessibilityNodeInfo targetInfo) {
        MyPost.postDelayed(2000, () -> {
            Log.i("click", "点击: " + targetInfo.getText() + "````" + targetInfo.getClassName());
            targetInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        });
    }


}
