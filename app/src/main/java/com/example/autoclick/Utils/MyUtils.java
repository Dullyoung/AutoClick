package com.example.autoclick.Utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyUtils {


    public static void startBrowser(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }
    public static void startAppWithPackageName(Context context, String packageName) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            //获取app信息
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "获取APP信息失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packageName = 参数packname
            String packageName2 = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：package name.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName2, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /*weike  https://qr.alipay.com/fkx149768bnfyszttvbx068
     * diandu   https://qr.alipay.com/fkx18795b6hgoxumuugw8b6
     * 2  https://qr.alipay.com/fkx14495qnnpmgo1y29rx49
     *
     * */

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

    //UserID要具有商家收款才行 不然跳转提醒有风险 金额和备注不可修改 类似于集成 但是没有回调
    public static void goToAliPayTransferMoneyShop(Context context, String userID, int number, String remark) {
        String uri = "alipays://platformapi/startapp?appId=20000123&" +
                "actionType=scan\"+\"&biz_data={\"s\": \"money\",\"u\": \"" + userID + "\",\"a\": \"" + number + "\",\"m\": \"" + remark + "\"}";
        Intent intent = null;
        try {
            intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "支付宝未安装或版本不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //相当于直接扫描了二维码。
    public static void goToAliPayTransferMoney(Context context) {
        String uri = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=10.2.6&qrcode=https://qr.alipay.com/fkx149768bnfyszttvbx068";
        Intent intent = null;
        try {
            intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "支付宝未安装或版本不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void goToAliPayTransferMoney(Context context, String QRCode) {
        //转账界面
        //QR 收款码扫描二维码最后一段
        String url = "intent://platformapi/startapp?saId=10000007&" +
                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F" + QRCode + "%3F_s" +
                "%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
        Intent intent = null;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "支付宝未安装或版本不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    public static void sharedToWXCircle(Context context, String text, Uri image) {

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(componentName);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, image);
        //   intent.putExtra("android.intent.extra.TEXT", text);
        //  intent.putExtra("sms_body", text);
        intent.putExtra("Kdescription", text);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "微信未安装或版本不支持", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareToWXWithPics(Context context, List<File> files, String description) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (File f : files) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                imageUris.add(FileProvider.getUriForFile(context, "com.example.study.provider", f));
            } else {
                imageUris.add(Uri.fromFile(f));
            }

        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        intent.putExtra(Intent.EXTRA_TEXT, description);
        context.startActivity(intent);

    }

    public static void shareToWX(Context context, String description) {

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, description);
        intent.putExtra(Intent.EXTRA_TEXT, description);
        intent.putExtra(Intent.EXTRA_TEXT, description);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "微信未安装或版本不支持", Toast.LENGTH_SHORT).show();
        }
    }


    public static void shareToQQ(Context context, String description) {


        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TEXT, description);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "手机QQ未安装或版本不支持", Toast.LENGTH_SHORT).show();
        }

    }

    public static void shareToQQZone(Context context, String description, Uri image) {

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.qzone", "com.qzonex.module.operation.ui.QZonePublishMoodActivity");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, description);
        intent.putExtra(Intent.EXTRA_STREAM, image);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "QQ空间未安装或版本不支持", Toast.LENGTH_SHORT).show();
        }
    }

    public static void joinQQGroup(Context context, String groupNumber) {

        String uri = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=" + groupNumber + "&card_type=group&source=qrcode";

        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));

        } catch (Exception e) {
            Toast.makeText(context, "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show();
            // 未安装手Q或安装的版本不支持

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

    public static void saveImageToSD(Bitmap bmp, String dir, String name) {
        // 首先保存图片
        if (!new File(dir).exists()) {
            new File(dir).mkdirs();
        }
        File file = new File(dir, name);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));

    }

    public static boolean showed(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void setCache(Context context, String key, boolean msg) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, msg);
        editor.apply();
    }


}

