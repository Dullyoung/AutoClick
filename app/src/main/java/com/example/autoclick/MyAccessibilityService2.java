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

import java.math.RoundingMode;
import java.util.Random;


public class MyAccessibilityService2 extends AccessibilityService {
    private static final String TAG = "aaaa";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "无障碍服务已开启: ");
        Toast.makeText(this, "无障碍服务已开启,请返回软件启动", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stop = false;
        if (intent != null) {
            keyword = intent.getStringExtra("key");
            Log.i(TAG, "onStartCommand: " + keyword);
        }
        checkStateTime();
        return super.onStartCommand(intent, flags, startId);
    }

    private String keyword = "";
    AccessibilityNodeInfo rootInfo = null;


    private void checkStateTime() {
        if (stop) {
            return;
        }
        Log.i("stateTime", "已经停止了: " + (System.currentTimeMillis() - stateTime) / 1000);
        if (System.currentTimeMillis() - stateTime > 10000) {
            MyGesture();
            Toast.makeText(this, "超过十秒未检测到指定内容，不用要关闭无障碍", Toast.LENGTH_LONG).show();
            stateTime = System.currentTimeMillis();
        }
        MyPost.postDelayed(1000, this::checkStateTime);
    }

    private long stateTime = System.currentTimeMillis();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        stateTime = System.currentTimeMillis();
        if (keyword.equals("")) {
            Log.i(TAG, "关键词为空 不执行: ");
            //  Toast.makeText(this, "为获取到关键字，返回重试", Toast.LENGTH_SHORT).show();
            return;
        }

        rootInfo = event.getSource();
        //拿到根节点
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

        get(rootInfo);

    }


    private boolean stop = false;

    @Override
    public boolean onUnbind(Intent intent) {
        stop = true;
        Toast.makeText(this, "停止运行", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new EventStub("停止运行"));
        return super.onUnbind(intent);
    }


    private long clickBackTime;


    private void MyGesture() {//仿滑动
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Path path = new Path();
            int x1 = new Random(System.currentTimeMillis()).nextInt(100) + 500;
            int x2 = new Random(System.currentTimeMillis()).nextInt(100) + 500;
            int y1 = new Random(System.currentTimeMillis()).nextInt(100) + 1000;
            int y2 = new Random(System.currentTimeMillis()).nextInt(100) + 500;
            path.moveTo(x1, y1);   //滑动起点
            path.lineTo(x2, y2);//滑动终点

            GestureDescription.Builder builder = new GestureDescription.Builder();
            long startTime = new Random(System.currentTimeMillis()).nextInt(100) + 200;
            long duration = new Random(System.currentTimeMillis()).nextInt(100) + 500;
            GestureDescription description = builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, duration)).build();

            //100L 第一个是开始的时间，第二个是持续时间
            dispatchGesture(description, new MyCallBack(), null);
            if (!isBackClicked && System.currentTimeMillis() - scrollTime > 5000) {
                //  MyGesture();
                isBackClicked = true;
                scrollTime = System.currentTimeMillis();
                Log.i("MyGesture", "MyGesture: x1:" + x1 + "`y1:" + y1 + "`x2:" + x2 + "`y2:" + y2);
                Log.i("MyGesture", "MyGesture:startTime " + startTime + "duration:" + duration);
            }
        }
    }

    private long scrollTime;

    private boolean isBackClicked = false;

    //模拟手势的监听
    @RequiresApi(api = Build.VERSION_CODES.N)
    private class MyCallBack extends GestureResultCallback {
        public MyCallBack() {
            super();
        }

        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            super.onCompleted(gestureDescription);
            Log.i(TAG, "onCompleted: " + gestureDescription);
        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            super.onCancelled(gestureDescription);
            Log.i(TAG, "onCancelled: " + gestureDescription);
        }
    }


    private void back(AccessibilityNodeInfo info) {
        if (System.currentTimeMillis() - clickBackTime > 3000) {

            performGlobalAction(GLOBAL_ACTION_BACK);
            isBackClicked = true;
            clickBackTime = System.currentTimeMillis();
            Log.i("performBack", "back: ");


        }
    }


    private AccessibilityNodeInfo get(AccessibilityNodeInfo rootInfo) {
        isBackClicked = false;
        if (rootInfo.getChildCount() > 0) {
            for (int i = 0; i < rootInfo.getChildCount(); i++) {
                AccessibilityNodeInfo info = rootInfo.getChild(i);
                if (info == null) {
                    continue;
                }


                if (info.getContentDescription() != null && (
                        info.getContentDescription().toString().contains("任务完成")
                                || info.getContentDescription().toString().contains("任务已经")
                                || info.getContentDescription().toString().contains("全部完成啦")
                                || info.getContentDescription().toString().contains("继续退出")
                                || info.getContentDescription().toString().contains("任务已完成"))) {
                    MyPost.postDelayed(1000, () -> {
                        back(info);
                        Log.i("success", "完成 返回列表 ");
                    });
                }


                if (info.getText() != null && (
                        info.getText().toString().contains("任务完成")
                                || info.getText().toString().contains("任务已经")
                                || info.getText().toString().contains("全部完成啦")
                                || info.getText().toString().contains("继续退出")
                                || info.getText().toString().contains("任务已完成"))) {
                    MyPost.postDelayed(1000, () -> {
                        back(info);
                        Log.i("success", "完成 返回列表 ");
                    });
                }


                if (info.getClassName() != null && info.getClassName().toString().contains("android.widget.Button")) {
                    if (info.getText() != null && info.getText().toString().contains(keyword)) {
                        MyPost.postDelayed(2000, () -> {
                            Log.i("success", "找到节点 点击 " + keyword);
                            performClick(getClickable(info));
                        });
                    }
                }

                if (info.getText() != null && info.getText().toString().contains("好的，我知道了")) {
                    MyPost.postDelayed(2000, () -> {
                        Log.i("success", "找到领取节点 点击 好的，我知道了 " );
                        performClick(getClickable(info));
                    });
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

    private long clickTime;

    private void performClick(AccessibilityNodeInfo targetInfo) {

        if (System.currentTimeMillis() - clickTime > 3000) {
            Log.i("click", "点击: " + targetInfo.getText() + "````" + targetInfo.getClassName());
            targetInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            clickTime = System.currentTimeMillis();
            // MyPost.postDelayed(5000, this::MyGesture);
            isBackClicked = false;
        }

    }


}
