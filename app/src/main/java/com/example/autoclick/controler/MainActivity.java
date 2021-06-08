package com.example.autoclick.controler;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.autoclick.R;
import com.example.autoclick.Utils.MyUtils;
import com.example.autoclick.model.bean.EventStub;
import com.example.autoclick.model.bean.ResultInfo;
import com.example.autoclick.model.bean.UpdateInfo;
import com.example.autoclick.model.engine.UpdataEngine;
import com.example.autoclick.services.MyAccessibilityService;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv_state)
    TextView mTvState;
    @BindView(R.id.tv_desp)
    TextView mTvDesp;

    static WeakReference<MainActivity> mainActivity = null;
    @BindView(R.id.s_oneKey)
    Switch mSOneKey;

    public static WeakReference<MainActivity> get() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainActivity = new WeakReference<MainActivity>(this);
        mTvState.setText("说明：\n1.需要自己设置允许后台运行权限，不然部分机型运行一分钟左右后台进程就被系统杀了。" +
                "\n2.开启无障碍服务 - 大杨的辅助-淘宝" +
                "\n3.点击方式是根据文字来的，三种任务自己选" +
                "\n4.打开淘宝任务列表就可以了");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getWindow().setStatusBarColor(0xff000000);
        checkUpdate();
        mLoadingDialog = new LoadingDialog(this);
        mSOneKey.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }
            isOnKeyMode = isChecked;
            if (isChecked) {
                Toast.makeText(this, "一键匹配已开启", Toast.LENGTH_SHORT).show();
            }
        });
    }

    LoadingDialog mLoadingDialog;

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    public static boolean isOnKeyMode = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsg(EventStub eventStub) {
        mTvDesp.setText("执行成功 --- " + eventStub.getOpen());
    }

    @OnClick({R.id.btn_permission, R.id.btn_start, R.id.btn_btn3,
            R.id.btn_start2, R.id.btn_close, R.id.tv_alipay,
            R.id.tv_qq, R.id.btn_start3, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_permission:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;
            case R.id.btn_start:
                MyAccessibilityService.start(this, "去完成");
                mTvDesp.setText("正在执行“去完成”任务，打开任务列表即可");
                break;
            case R.id.btn_start2:
                MyAccessibilityService.start(this, "去浏览");
                mTvDesp.setText("正在执行“去浏览”任务，打开任务列表即可");
                break;
            case R.id.btn_close:
                MyAccessibilityService.start(this, "去搜索");
                mTvDesp.setText("正在执行“去搜索”任务，打开任务列表即可");
                break;
            case R.id.btn_btn3:
                MyAccessibilityService.start(this, "去逛逛");
                mTvDesp.setText("正在执行“去逛逛”任务，打开任务列表即可");
                break;
            case R.id.btn_start3:
                mLoadingDialog.show("获取中...");
                checkUpdate();
                break;
            case R.id.btn_stop:
                MyAccessibilityService.stop(this);
                break;
            case R.id.tv_alipay:
                MyUtils.goToAliPayTransferMoney(this, "fkx175670dgp1a3jcsqilb4");
                break;
            case R.id.tv_qq:
                chatWithQQ(this, 664846453 + "");
                break;
        }
    }

    private void checkUpdate() {
        UpdataEngine engine = new UpdataEngine();
        engine.getInfo(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mLoadingDialog.dismiss();
                Toast.makeText(MainActivity.this, "请求失败" + e, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                mLoadingDialog.dismiss();
                ResultInfo<UpdateInfo> resultInfo;
                try {
                    resultInfo = JSONObject.parseObject(response, new TypeReference<ResultInfo<UpdateInfo>>() {
                    }.getType());
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "返回数据有误", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                if (resultInfo != null) {
                    if (resultInfo.getCode() == 1) {
                        success(resultInfo.getData());
                    } else {
                        Toast.makeText(MainActivity.this, resultInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void success(UpdateInfo updateInfo) {
        if (updateInfo == null) {
            return;
        }
        if (updateInfo.getVersion() > getPackageInfo().versionCode) {
            String msg = updateInfo.getDesp() + "\n大小:" + updateInfo.getSize();
            msg = msg.replace("\\n", "\n");
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("更新")
                    .setMessage(msg)
                    .setNegativeButton("不了", (dialog1, which) -> {
                        dialog1.dismiss();
                    })
                    .setPositiveButton("去下载", ((dialog2, which) -> {
                        MyUtils.startBrowser(this, updateInfo.getUrl());
                    }))
                    .create();
            dialog.show();
        } else {
            Toast.makeText(this, "已是最新版本", Toast.LENGTH_SHORT).show();
        }

    }

    public PackageInfo getPackageInfo() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            pInfo = new PackageInfo();
            pInfo.versionCode = 1;
            pInfo.versionName = "1.0.0";
        }
        return pInfo;
    }


    //普通转账界面 可以自动填写金额和备注 但是可以修改
    public static void goToAliPayTransferMoneyPerson(Context context, String money, String remarks, String userID) {
        String uri = "alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + money + "&userId=" + userID + "&memo=" + remarks;
        Intent intent = null;
        try {
            intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "支付宝未安装或版本不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void chatWithQQ(final Context context, String qq) {
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(context, "手机QQ未安装或该版本不支持", Toast.LENGTH_SHORT).show();
        }

    }


}
