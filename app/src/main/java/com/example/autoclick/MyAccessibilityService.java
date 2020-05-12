package com.example.autoclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Service;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;


public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "aaaa";
    public static int done = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {



        try {
            //拿到根节点
            AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
            if (rootInfo == null) {
                return;
            }
            //开始找目标节点，这里拎出来细讲，直接往下看正文
            if (rootInfo.getChildCount() != 0) {
                MyTask(rootInfo);
            }
        } catch (Exception e) {
            Log.i(TAG, "onAccessibilityEvent: " + e);
        }
    }

    private void MyGesture(){//仿滑动
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(1000, 1000);//滑动起点
            path.lineTo(2000, 1000);//滑动终点
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription description = builder.addStroke(new GestureDescription.StrokeDescription(path, 100L, 100L)).build();
            dispatchGesture(description, new MyCallBack(), null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class MyCallBack extends GestureResultCallback {
        public MyCallBack() {
            super();
        }

        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            super.onCompleted(gestureDescription);
            Log.i(TAG, "onCompleted: ");
        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            super.onCancelled(gestureDescription);
            Log.i(TAG, "onCancelled: ");
        }
    }


    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt: ");
    }

    private void changeInput(AccessibilityNodeInfo info,String text) {  //改变editText的内容
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text);
        info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    private void MyTask(AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) {
            return;
        }
        //开始去找
       findByID(rootInfo, "com.tencent.mobileqq:id/chat_item_content_layout");
    }

    private AccessibilityNodeInfo findByText(AccessibilityNodeInfo rootInfo, String text) {
        if (rootInfo.getChildCount() > 0) {
            for (int i = 0; i < rootInfo.getChildCount(); i++) {
                AccessibilityNodeInfo child = rootInfo.getChild(i);
                try {
                    if (child.findAccessibilityNodeInfosByViewId(text).size() > 0) {
                        for (AccessibilityNodeInfo info : child.findAccessibilityNodeInfosByViewId(text)) {

                            if (info.getText().toString().equals(text)) {
                                performClick(getClickable(info));
                                return null;
//                                performClick(info);
                            }
                        }
                    }
                } catch (NullPointerException e) {
                }
                findByText(child, text);
            }
        }
        return null;

    }


    private AccessibilityNodeInfo findByID(AccessibilityNodeInfo rootInfo, String text) {
        if (rootInfo.getChildCount() > 0) {
            for (int i = 0; i < rootInfo.getChildCount(); i++) {
                AccessibilityNodeInfo child = rootInfo.getChild(i);
                try {
                    if (child.findAccessibilityNodeInfosByViewId(text).size() > 0) {
                        for (AccessibilityNodeInfo info : child.findAccessibilityNodeInfosByViewId(text)) {
                            performClick(getClickable(info));
//                        performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS); //模仿全局手势
                            return null;
                        }
                    }

                } catch (NullPointerException e) {
                }
                findByID(child, text);
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

    private void performClick(AccessibilityNodeInfo targetInfo) {
        targetInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
}
