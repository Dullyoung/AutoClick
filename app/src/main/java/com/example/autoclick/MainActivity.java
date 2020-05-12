package com.example.autoclick;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

import static com.example.autoclick.MyAccessibilityService.done;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    LinearLayout linearLayout;
    AutoTouch autoTouch = new AutoTouch();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.text_my);
        findViewById(R.id.t1).setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            //   intent.setData(Uri.fromParts("package",getPackageName(), null));
            startActivity(intent);
        });
        findViewById(R.id.t1).performClick();
        findViewById(R.id.t2).setOnClickListener(v -> {
            startService(new Intent(this, MyAccessibilityService.class));
            done = 0;
            v.postDelayed(() -> MyUtils.chatWithQQ(this,"664846453"),500);
        });




    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Log.i("aaaaa", "ACTION_DOWN: " + event.getX() + "," + event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("aaaaa", "ACTION_MOVE: " + event.getX() + "," + event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Log.i("aaaaa", "up: " + event.getX() + "," + event.getY());
                break;
            default:
                break;

        }
        return true;
    }


    public class AutoTouch {
        public int width = 0;
        public int height = 0;

        /**
         * 传入在屏幕中的比例位置，坐标左上角为基准
         *
         * @param act    传入Activity对象
         * @param ratioX 需要点击的x坐标在屏幕中的比例位置
         * @param ratioY 需要点击的y坐标在屏幕中的比例位置
         */
        public void autoClickRatio(Activity act, final double ratioX, final double ratioY) {
            Point point = new Point();
            act.getWindowManager().getDefaultDisplay().getSize(point);
            width = point.x;
            height = point.y;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 线程睡眠0.3s
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 生成点击坐标
                    int x = (int) (width * ratioX);
                    int y = (int) (height * ratioY);

                    // 利用ProcessBuilder执行shell命令
                    String[] order = {"input", "tap", "", "" + x, "" + y};
                    try {
                        new ProcessBuilder(order).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        /**
         * 传入在屏幕中的坐标，坐标左上角为基准
         *
         * @param act 传入Activity对象
         * @param x   需要点击的x坐标
         * @param y   需要点击的x坐标
         */
        public void autoClickPos(Activity act, final double x, final double y) {
            Point point = new Point();
            act.getWindowManager().getDefaultDisplay().getSize(point);
            width = point.x;
            height = point.y;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 线程睡眠0.3s
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 利用ProcessBuilder执行shell命令
                    String[] order = {"input", "tap", "" + x, "" + y};
                    try {
                        new ProcessBuilder(order).start();
                        Log.i("aaaa", "run: ");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
