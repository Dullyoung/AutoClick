package com.example.autoclick.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.autoclick.Utils.MyPost;
import com.example.autoclick.Utils.MyUtils;
import com.example.autoclick.controler.MainActivity;
import com.example.autoclick.model.bean.EventStub;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "aaaa";

    public static void start(Context context, String key) {
        if (MyUtils.isServiceRunning(context, MyAccessibilityService.class.getName())) {
            keyword = key;
            stop = false;
        } else {
            Intent intent2 = new Intent(context, MyAccessibilityService.class);
            intent2.putExtra("key", key);
            context.startService(intent2);
            stop = false;
        }
    }

    public static void stop(Context context) {
        Intent stopIntent = new Intent(context, MyAccessibilityService.class);
        stop = context.stopService(stopIntent);
        EventBus.getDefault().post(new EventStub("停止"));
        stateTime = 0;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "无障碍服务已开启: ");
        if (MainActivity.get() == null || MainActivity.get().get() == null || MainActivity.get().get().isDestroyed()) {
            Toast.makeText(this, "MainActivity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
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
        return START_STICKY;
    }

    public static String keyword = "";
    AccessibilityNodeInfo rootInfo = null;

    private void checkStateTime() {
        if (stop) {
            return;
        }
        Log.i("stateTime", "onkey:" + MainActivity.isOnKeyMode +
                "关键词：" + keyword + ",在任务列表页？" + isBackClicked
                + "已经停止了: " + (System.currentTimeMillis() - stateTime) / 1000);
        if (System.currentTimeMillis() - stateTime > (isBackClicked ? 5000 : 25000)) {
            MyGesture();
            stateTime = System.currentTimeMillis();
        }
        MyPost.postDelayed(1000, () -> {
            if (!stop) {
                checkStateTime();
            }
        });
    }

    private static long stateTime = 0;

    private long lastFindTime = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (stop) {
            return;
        }
        if (stateTime == 0) {
            checkStateTime();
        }

        if (System.currentTimeMillis() - lastFindTime < 1000) {
            lastFindTime = System.currentTimeMillis();
            return;
        }

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
        stop = true;
        stateTime = 0;
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


    private static boolean stop = true;

    @Override
    public boolean onUnbind(Intent intent) {
        stop = true;
        Toast.makeText(this, "停止运行", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new EventStub("停止运行"));
        stateTime = 0;
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
                scrollTime = System.currentTimeMillis();
                Log.i("MyGesture", "MyGesture: x1:" + x1 + "`y1:" + y1 + "`x2:" + x2 + "`y2:" + y2);
                Log.i("MyGesture", "MyGesture:startTime " + startTime + "duration:" + duration);
            }
        }
    }

    private long scrollTime;

    private boolean isBackClicked = true;

    //模拟手势的监听
    @RequiresApi(api = Build.VERSION_CODES.N)
    private class MyCallBack extends GestureResultCallback {
        public MyCallBack() {
            super();
        }

        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            super.onCompleted(gestureDescription);
        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            super.onCancelled(gestureDescription);
        }
    }


    private void back(AccessibilityNodeInfo info) {
        if (System.currentTimeMillis() - clickBackTime > 3000) {
            performGlobalAction(GLOBAL_ACTION_BACK);
            isBackClicked = true;
            clickBackTime = System.currentTimeMillis();
            Log.i("success", "back: ");
            stateTime = System.currentTimeMillis();
        }
    }

    private List<String> finishString = new ArrayList<>(Arrays.asList(
            "任务完成", "任务已经", "全部完成啦", "继续退出", "任务已完成"));

    private List<String> clickNodeInfo = new ArrayList<>(Arrays.asList(
            "逛店最多", "去浏览", "去逛逛", "去完成", "去搜索"
    ));

    private void get(AccessibilityNodeInfo rootInfo) {
        if (rootInfo.getChildCount() > 0) {
            for (int i = 0; i < rootInfo.getChildCount(); i++) {
                AccessibilityNodeInfo info = rootInfo.getChild(i);
                if (info == null) {
                    continue;
                }
                boolean containCD = info.getContentDescription() != null
                        && finishString.contains(info.getContentDescription().toString());
                boolean containText = info.getText() != null
                        && finishString.contains(info.getText().toString());
                if (containCD || containText) {
                    MyPost.postDelayed(1000, () -> {
                        back(info);
                        Log.i("success", "完成 返回列表 " + info.getText() + "-desp-" + info.getContentDescription());
                    });
                    return;
                }


                if (info.getClassName() != null && info.getClassName().toString().contains("android.widget.Button")) {
                    if (MainActivity.isOnKeyMode
                            || (info.getText() != null && info.getText().toString().contains(keyword))) {
                        if (info.getText() != null && clickNodeInfo.contains(info.getText().toString())) {
                            MyPost.postDelayed(1000, () -> {
                                Log.i("success", "找到节点 点击 " + info.getText().toString());
                                performClick(getClickable(info));
                                stateTime = System.currentTimeMillis();
                                isBackClicked = false;
                            });
                            return;
                        }
                    }
                }

                if (info.getClassName() != null && info.getClassName().toString().contains("android.view.View")) {
                    if (info.getText() != null && info.getText().toString().contains("逛店最多")) {
                        MyPost.postDelayed(1000, () -> {
                            Log.i("success", "找到节点 点击 " + "逛店最多");
                            performClick(getClickable(info));
                            stateTime = System.currentTimeMillis();
                            isBackClicked = false;
                        });
                        return;
                    }
                }

                if (info.getText() != null && info.getText().toString().contains("好的，我知道了")) {
                    MyPost.postDelayed(1000, () -> {
                        Log.i("success", "找到领取节点 点击 好的，我知道了 ");
                        performClick(getClickable(info));
                    });
                    return;
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
    }


    //有些节点不可点击 点击交给父级甚至父级的父级...来做的。
    private AccessibilityNodeInfo getClickable(AccessibilityNodeInfo info) {
        if (info == null) {
            return null;
        }
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
        if (targetInfo == null) return;
        if (System.currentTimeMillis() - clickTime > 3000) {
            Log.i("click", "点击: " + targetInfo.getText() + "````" + targetInfo.getClassName());
            targetInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            clickTime = System.currentTimeMillis();
            isBackClicked = false;
        }
    }


}
