package com.example.autoclick.controler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.autoclick.model.bean.EventStub;
import com.example.autoclick.services.MyAccessibilityService;
import com.example.autoclick.Utils.MyUtils;
import com.example.autoclick.R;
import com.example.autoclick.model.bean.UpdateInfo;
import com.example.autoclick.model.engine.UpdataEngine;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv_state)
    TextView mTvState;
    @BindView(R.id.tv_desp)
    TextView mTvDesp;

    static WeakReference<MainActivity> mainActivity = null;

    public static WeakReference<MainActivity> get() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainActivity = new WeakReference<MainActivity>(this);
        mTvState.setText("说明：\n1.需要自己到手机相应位置设置允许后台运行权限，不然部分机型运行一分钟左右后台进程就被系统杀了。" +
                "\n2.开启无障碍服务 - 大杨的双十一辅助" +
                "\n3.点击方式是根据文字来的，三种任务自己选" +
                "\n4.打开淘宝任务列表就可以了");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mLoadingDialog = new LoadingDialog(this);
    }

    LoadingDialog mLoadingDialog;

    long time;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - time > 1000) {
            time = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            onDestroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsg(EventStub eventStub) {
        mTvDesp.setText("执行成功 --- " + eventStub.getOpen());
    }

    private Intent intent2;


    @OnClick({R.id.btn_permission, R.id.btn_start,
            R.id.btn_start2, R.id.btn_close, R.id.tv_alipay,
            R.id.tv_qq, R.id.btn_start3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_permission:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;
            case R.id.btn_start:

                if (intent2 != null) {
                    stopService(intent2);
                }
                intent2 = new Intent(this, MyAccessibilityService.class);
                intent2.putExtra("key", "去完成");
                startService(intent2);
                mTvDesp.setText("正在执行“去完成”任务，打开任务列表即可");
                break;
            case R.id.btn_start2:
                if (intent2 != null) {
                    stopService(intent2);
                }
                intent2 = new Intent(this, MyAccessibilityService.class);
                intent2.putExtra("key", "去浏览");
                startService(intent2);

                mTvDesp.setText("正在执行“去浏览”任务，打开任务列表即可");
                break;
            case R.id.btn_close:

                if (intent2 != null) {
                    stopService(intent2);
                }
                intent2 = new Intent(this, MyAccessibilityService.class);
                intent2.putExtra("key", "去搜索");
                startService(intent2);
                mTvDesp.setText("正在执行“去搜索”任务，打开任务列表即可");
                break;
            case R.id.btn_start3:
                mLoadingDialog.show("获取中...");
                UpdataEngine engine = new UpdataEngine();

                engine.getInfo(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                        return response.body().string();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("aaaa", "onError: " + e);
                    }

                    @Override
                    public void onResponse(Object response, int id) {
                        mLoadingDialog.dismiss();
                        try {
                            String responseString = response.toString();
                            int start = responseString.lastIndexOf("###Info###");
                            int end = responseString.indexOf("***Info***");
                            String content = responseString.substring(start + 10, end);
                            content = content.replace("\\", "");
                            Log.i("aaaa", "返回数据: " + content);
                            updateInfo = JSONObject.parseObject(content, new TypeReference<UpdateInfo>() {
                            }.getType());
                            success(updateInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                break;
            case R.id.tv_alipay:
                //    goToAliPayTransferMoneyPerson(this, "6.66", "好活~当赏~", "2088612672749295");
                MyUtils.goToAliPayTransferMoney(this, "fkx175670dgp1a3jcsqilb4");
                break;
            case R.id.tv_qq:
                chatWithQQ(this, 664846453 + "");
                break;
        }
    }

    UpdateInfo updateInfo;

    private void success(UpdateInfo updateInfo) {
        if (updateInfo == null) {
            return;
        }
        if (updateInfo.getVersion() > 20) {
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
