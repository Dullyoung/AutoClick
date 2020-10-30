package com.example.autoclick;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv_state)
    TextView mTvState;
    @BindView(R.id.tv_desp)
    TextView mTvDesp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTvState.setText("说明：\n1.需要自己到手机相应位置设置允许后台运行权限，不然部分机型运行一分钟左右后台进程就被系统杀了。" +
                "\n2.开启无障碍服务 - 大杨的双十一辅助" +
                "\n3.点击方式是根据文字来的，三种任务自己选" +
                "\n4.如果开了后台权限，用完了记得关闭无障碍，不然会一直运行");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsg(EventStub eventStub) {
        mTvDesp.setText("执行成功 --- " + eventStub.getOpen());
    }

    private Intent intent2;


    @OnClick({R.id.btn_permission, R.id.btn_start, R.id.btn_start2, R.id.btn_close, R.id.tv_alipay, R.id.tv_qq})
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
                intent2 = new Intent(this,MyAccessibilityService.class);
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
            case R.id.tv_alipay:
            //    goToAliPayTransferMoneyPerson(this, "6.66", "好活~当赏~", "2088612672749295");
                MyUtils.goToAliPayTransferMoney(this,"fkx175670dgp1a3jcsqilb4");
                break;
            case R.id.tv_qq:
                chatWithQQ(this, 664846453 + "");
                break;
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
